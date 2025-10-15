package com.marina.hotelmanagement.data

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromDate(date: Long): Date {
        return Date(date)
    }

    @TypeConverter
    fun dateToLong(date: Date): Long {
        return date.time
    }
}