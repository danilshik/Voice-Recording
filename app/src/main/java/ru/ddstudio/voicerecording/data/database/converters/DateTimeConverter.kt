package ru.ddstudio.voicerecording.data.database.converters

import androidx.room.TypeConverter
import org.joda.time.DateTime

class DateTimeConverter {

    @TypeConverter
    fun fromDatetime(dateTime : DateTime) : Long{
        return dateTime.millis
    }

    @TypeConverter
    fun toDateTime(dateTime : Long) : DateTime{
        return DateTime(dateTime)
    }
}