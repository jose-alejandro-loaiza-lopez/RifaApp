package com.rifaapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.rifaapp.core.database.entity.BoletaEntity
import com.rifaapp.core.database.entity.PagoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BoletaDao {

    @Query("SELECT * FROM boleta WHERE rifaId = :rifaId")
    fun observarPorRifa(rifaId: Long): Flow<List<BoletaConPagos>>

    @Transaction
    @Query("SELECT * FROM boleta WHERE rifaId = :rifaId")
    suspend fun obtenerPorRifa(rifaId: Long): List<BoletaConPagos>

    @Query("SELECT * FROM boleta WHERE rifaId = :rifaId AND numero = :numero LIMIT 1")
    suspend fun obtenerPorNumero(rifaId: Long, numero: Int): BoletaConPagos?

    @Query("SELECT * FROM boleta WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Long): BoletaConPagos?

    @Upsert
    suspend fun guardar(entity: BoletaEntity)

    @Insert
    suspend fun insertar(entity: BoletaEntity): Long

    @Query("DELETE FROM boleta WHERE id = :boletaId")
    suspend fun eliminar(boletaId: Long)

    @Insert
    suspend fun insertarPago(entity: PagoEntity): Long

    @Query("DELETE FROM pago WHERE boletaId = :boletaId")
    suspend fun eliminarPagos(boletaId: Long)
}
