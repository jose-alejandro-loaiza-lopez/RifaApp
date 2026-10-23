package com.rifaapp.core.database

import com.rifaapp.core.database.dao.BoletaDao
import com.rifaapp.core.database.entity.PagoEntity
import com.rifaapp.core.domain.common.ErrorDominio
import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Boleta
import com.rifaapp.core.domain.repository.BoletaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant

class RoomBoletaRepository(
    private val dao: BoletaDao,
) : BoletaRepository {

    override fun observarPorRifa(rifaId: Long): Flow<List<Boleta>> =
        dao.observarPorRifa(rifaId).map { lista -> lista.map { it.toDomain() } }

    override suspend fun obtenerPorRifa(rifaId: Long): List<Boleta> = withContext(Dispatchers.IO) {
        dao.obtenerPorRifa(rifaId).map { it.toDomain() }
    }

    override suspend fun obtenerPorNumero(rifaId: Long, numero: Int): Boleta? = withContext(Dispatchers.IO) {
        dao.obtenerPorNumero(rifaId, numero)?.toDomain()
    }

    override suspend fun guardar(boleta: Boleta): Resultado<Boleta> = withContext(Dispatchers.IO) {
        val existente = dao.obtenerPorNumero(boleta.rifaId, boleta.numero)
        val id = when {
            boleta.id > 0L -> boleta.id
            existente != null -> existente.boleta.id
            else -> 0L
        }
        if (id > 0L) {
            dao.guardar(boleta.toEntity(preferredId = id))
        } else {
            dao.insertar(boleta.toEntity(preferredId = 0L))
        }
        val fresh = dao.obtenerPorNumero(boleta.rifaId, boleta.numero)?.toDomain() ?: boleta
        Resultado.Exito(fresh)
    }

    override suspend fun registrarPago(
        boletaId: Long,
        monto: Long,
        fecha: Instant,
        nota: String?,
    ): Resultado<Boleta> = withContext(Dispatchers.IO) {
        if (dao.obtenerPorId(boletaId) == null) {
            return@withContext Resultado.Fallo(ErrorDominio.BoletaNoEncontrada)
        }
        dao.insertarPago(
            PagoEntity(
                boletaId = boletaId,
                monto = monto,
                fecha = fecha,
                nota = nota,
            )
        )
        val fresh = dao.obtenerPorId(boletaId)?.toDomain()
            ?: return@withContext Resultado.Fallo(ErrorDominio.BoletaNoEncontrada)
        Resultado.Exito(fresh)
    }

    override suspend fun liberar(boletaId: Long): Resultado<Unit> = withContext(Dispatchers.IO) {
        dao.eliminar(boletaId)
        Resultado.Exito(Unit)
    }
}
