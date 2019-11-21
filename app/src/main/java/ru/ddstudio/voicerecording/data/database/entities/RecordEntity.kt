package ru.ddstudio.voicerecording.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime
import org.joda.time.Duration
import java.io.Serializable

@Entity(tableName = "record")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name : String,
    @ColumnInfo(name = "file_path")
    val filePath: String,
    val duration: Duration,
    @ColumnInfo(name = "created_dateTime")
    val createdDateTime: DateTime

) : Serializable