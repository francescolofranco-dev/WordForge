package com.wordforge.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlin.jvm.java


@Database(entities = [Word::class], version = 1, exportSchema = false)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    companion object {
        @Volatile
        private var wordDatabaseInstance: WordDatabase? = null
        fun getDatabase(context: Context): WordDatabase {
            return wordDatabaseInstance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordDatabase::class.java,
                    "wordforge_db"
                ).build()
                wordDatabaseInstance = instance
                instance
            }
        }
    }
}