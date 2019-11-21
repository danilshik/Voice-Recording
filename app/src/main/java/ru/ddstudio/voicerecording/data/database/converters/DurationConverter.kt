package ru.ddstudio.voicerecording.data.database.converters

import androidx.room.TypeConverter
import org.joda.time.Duration

class DurationConverter {

    @TypeConverter
    fun fromDuration(duration : Duration) : Long{
        return duration.millis
    }


    @TypeConverter
    fun toDuration(duration : Long) : Duration{
        return Duration.millis(duration)
    }
}