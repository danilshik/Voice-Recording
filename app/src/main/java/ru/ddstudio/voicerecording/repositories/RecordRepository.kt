package ru.ddstudio.voicerecording.repositories


import android.util.Log
import androidx.lifecycle.LiveData
import ru.ddstudio.voicerecording.data.database.AppDatabase
import ru.ddstudio.voicerecording.data.database.entities.RecordEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordRepository @Inject constructor(private val appDatabase: AppDatabase){
    private val allRecords : LiveData<List<RecordEntity>> = appDatabase.getRecordDao().getAllRecording()
    suspend fun addRecording(recordEntity: RecordEntity) {
        appDatabase.getRecordDao().insert(recordEntity)
        Log.d("Repository", "${recordEntity.toString()} добавлена" )
    }

    fun getAllRecording(): LiveData<List<RecordEntity>> = allRecords

    suspend fun deleteFile(filePath: String, name: String) {
        Log.d("Test", "test")
    }

}

