package com.rifaapp.core.database

import com.rifaapp.core.database.dao.BoletaConPagos
import com.rifaapp.core.database.entity.BoletaEntity
import com.rifaapp.core.database.entity.PagoEntity
import com.rifaapp.core.database.entity.RifaEntity
import com.rifaapp.core.domain.model.Boleta
import com.rifaapp.core.domain.model.Cliente
import com.rifaapp.core.domain.model.Pago
import com.rifaapp.core.domain.model.Rifa

internal fun RifaEntity.toDomain(): Rifa = Rifa(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    fechaSorteo = fechaSorteo,
    precioBoleta = precioBoletaCentavos,
    numeroInicio = numeroInicio,
    numeroFin = numeroFin,
    cantidadDigitos = cantidadDigitos,
    columnas = columnas,
    loteria = loteria,
)

internal fun Rifa.toEntity(preferredId: Long? = null): RifaEntity {
    val actualId = when {
        preferredId != null && preferredId > 0 -> preferredId
        id > 0 -> id
        else -> 0L
    }
    return RifaEntity(
        id = actualId,
        titulo = titulo,
        descripcion = descripcion,
        fechaSorteo = fechaSorteo,
        precioBoletaCentavos = precioBoleta,
        numeroInicio = numeroInicio,
        numeroFin = numeroFin,
        cantidadDigitos = cantidadDigitos,
        columnas = columnas,
        loteria = loteria,
    )
}

internal fun BoletaEntity.toDomain(pagos: List<PagoEntity>): Boleta = Boleta(
    id = id,
    rifaId = rifaId,
    numero = numero,
    cliente = Cliente(clienteNombre, clienteTelefono),
    pagos = pagos.map { it.toDomain() },
    notas = notas,
)

internal fun Boleta.toEntity(preferredId: Long? = null): BoletaEntity {
    val actualId = when {
        preferredId != null && preferredId > 0 -> preferredId
        id > 0 -> id
        else -> 0L
    }
    return BoletaEntity(
        id = actualId,
        rifaId = rifaId,
        numero = numero,
        clienteNombre = cliente.nombre,
        clienteTelefono = cliente.telefonoE164,
        notas = notas,
    )
}

internal fun PagoEntity.toDomain(): Pago = Pago(
    id = id,
    boletaId = boletaId,
    monto = monto,
    fecha = fecha,
    nota = nota,
)

internal fun BoletaConPagos.toDomain(): Boleta = boleta.toDomain(pagos)
