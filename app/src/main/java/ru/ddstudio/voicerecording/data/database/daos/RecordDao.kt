package ru.ddstudio.voicerecording.data.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.ddstudio.voicerecording.data.database.entities.RecordEntity

@Dao
interface RecordDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recordEntity: RecordEntity)

    @Query("SELECT * FROM record ORDER BY created_dateTime ASC")
    fun getAllRecording() : LiveData<List<RecordEntity>>
}