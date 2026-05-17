package com.wordforge.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlin.jvm.java


@Database(entities = [Word::class], version = 2, exportSchema = false)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    companion object {
        // v1 → v2: add the currentStreak column (default 0 for existing rows).
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Word ADD COLUMN currentStreak INTEGER NOT NULL DEFAULT 0")
            }
        }

        @Volatile
        private var wordDatabaseInstance: WordDatabase? = null
        fun getDatabase(context: Context): WordDatabase {
            return wordDatabaseInstance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordDatabase::class.java,
                    "wordforge_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                wordDatabaseInstance = instance
                instance
            }
        }
    }
}
