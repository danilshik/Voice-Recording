package ru.ddstudio.voicerecording.repositories


import android.util.Log
import ru.ddstudio.voicerecording.data.database.AppDatabase
import ru.ddstudio.voicerecording.data.database.entities.RecordEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordRepository @Inject constructor(private val appDatabase: AppDatabase){
    fun addRecording(recordEntity: RecordEntity) {
        appDatabase.getRecordDao().insert(recordEntity)
    }

    fun deleteFile(filePath: String, name: String) {
        Log.d("Test", "test")
    }

}

