package com.rifaapp.core.database

import com.rifaapp.core.database.dao.RifaDao
import com.rifaapp.core.domain.common.Resultado
import com.rifaapp.core.domain.model.Rifa
import com.rifaapp.core.domain.repository.RifaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomRifaRepository(
    private val dao: RifaDao,
) : RifaRepository {

    override fun observarActiva(): Flow<Rifa?> =
        dao.observarActiva().map { it?.toDomain() }

    override suspend fun obtenerActiva(): Rifa? = withContext(Dispatchers.IO) {
        dao.obtenerActiva()?.toDomain()
    }

    override suspend fun guardar(rifa: Rifa): Resultado<Rifa> = withContext(Dispatchers.IO) {
        val baseId = when {
            rifa.id > 0 -> rifa.id
            else -> dao.obtenerActiva()?.id ?: 0L
        }
        dao.guardar(rifa.toEntity(preferredId = baseId))
        val fresh = dao.obtenerActiva()?.toDomain() ?: rifa
        Resultado.Exito(fresh)
    }
}
