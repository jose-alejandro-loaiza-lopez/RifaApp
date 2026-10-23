package com.rifaapp.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "rifa")
data class RifaEntity(
    @PrimaryKey val id: Long = 0L,
    val titulo: String,
    val descripcion: String = "",
    val fechaSorteo: LocalDate? = null,
    val precioBoletaCentavos: Long,
    val numeroInicio: Int,
    val numeroFin: Int,
    val cantidadDigitos: Int,
    val columnas: Int = 5,
    val loteria: String? = null,
)
