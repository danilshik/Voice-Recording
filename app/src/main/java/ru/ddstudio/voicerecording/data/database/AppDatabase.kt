package ru.ddstudio.voicerecording.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.ddstudio.voicerecording.data.database.converters.DateTimeConverter
import ru.ddstudio.voicerecording.data.database.converters.DurationConverter
import ru.ddstudio.voicerecording.data.database.daos.RecordDao
import ru.ddstudio.voicerecording.data.database.entities.RecordEntity

@Database(
    entities = [RecordEntity::class],version = 1)
@TypeConverters(DurationConverter::class, DateTimeConverter::class)
abstract class AppDatabase  : RoomDatabase(){
    abstract fun getRecordDao() : RecordDao

    companion object{
        @Volatile private var INSTANCE : AppDatabase? = null

        fun getInstance(context : Context) : AppDatabase =
            INSTANCE ?: synchronized(this){
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext, AppDatabase::class.java, "database"
            ).build()

    }




}