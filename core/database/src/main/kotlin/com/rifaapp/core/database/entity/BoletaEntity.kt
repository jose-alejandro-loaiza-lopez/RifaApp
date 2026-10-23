package com.rifaapp.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "boleta",
    foreignKeys = [
        ForeignKey(
            entity = RifaEntity::class,
            parentColumns = ["id"],
            childColumns = ["rifaId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["rifaId", "numero"], unique = true)],
)
data class BoletaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val rifaId: Long,
    val numero: Int,
    val clienteNombre: String,
    val clienteTelefono: String,
    val notas: String? = null,
)

@Entity(
    tableName = "pago",
    foreignKeys = [
        ForeignKey(
            entity = BoletaEntity::class,
            parentColumns = ["id"],
            childColumns = ["boletaId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("boletaId")],
)
data class PagoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val boletaId: Long,
    val monto: Long,
    val fecha: Instant,
    val nota: String? = null,
)
