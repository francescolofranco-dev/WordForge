package com.wordforge.data

import androidx.room.TypeConverter

class WordTypeConverters {
    @TypeConverter
    fun itemTypeToString(value: LearningItemType): String = value.name

    @TypeConverter
    fun stringToItemType(value: String): LearningItemType =
        LearningItemType.valueOf(value)
}
