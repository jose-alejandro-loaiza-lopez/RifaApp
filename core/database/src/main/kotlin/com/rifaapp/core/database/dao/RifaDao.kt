package com.rifaapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.rifaapp.core.database.entity.RifaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RifaDao {

    @Query("SELECT * FROM rifa LIMIT 1")
    fun observarActiva(): Flow<RifaEntity?>

    @Query("SELECT * FROM rifa LIMIT 1")
    suspend fun obtenerActiva(): RifaEntity?

    @Upsert
    suspend fun guardar(entity: RifaEntity)
}
