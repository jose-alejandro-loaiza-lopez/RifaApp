package com.rifaapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rifaapp.core.database.converters.Converters
import com.rifaapp.core.database.dao.BoletaDao
import com.rifaapp.core.database.dao.RifaDao
import com.rifaapp.core.database.entity.BoletaEntity
import com.rifaapp.core.database.entity.PagoEntity
import com.rifaapp.core.database.entity.RifaEntity

@Database(
    entities = [RifaEntity::class, BoletaEntity::class, PagoEntity::class],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rifaDao(): RifaDao
    abstract fun boletaDao(): BoletaDao
}
