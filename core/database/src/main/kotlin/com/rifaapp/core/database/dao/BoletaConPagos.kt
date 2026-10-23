package com.rifaapp.core.database.dao

import androidx.room.Embedded
import androidx.room.Relation
import com.rifaapp.core.database.entity.BoletaEntity
import com.rifaapp.core.database.entity.PagoEntity

data class BoletaConPagos(
    @Embedded val boleta: BoletaEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "boletaId",
        entity = PagoEntity::class,
    )
    val pagos: List<PagoEntity>,
)
