package com.wordforge.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlin.jvm.java


@Database(entities = [Word::class], version = 4, exportSchema = false)
@TypeConverters(WordTypeConverters::class)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    companion object {
        // v1 → v2: add the currentStreak column (default 0 for existing rows).
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Word ADD COLUMN currentStreak INTEGER NOT NULL DEFAULT 0")
            }
        }

        // v2 → v3: add the randomlyFlip column. Existing rows default to 1
        // (true) so they keep the app's original random-flip behavior.
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Word ADD COLUMN randomlyFlip INTEGER NOT NULL DEFAULT 1")
            }
        }

        // v3 → v4: introduce type-aware learning items. Existing rows remain
        // simple words; verb-specific columns stay null for those rows.
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE Word ADD COLUMN itemType TEXT NOT NULL DEFAULT 'SIMPLE_WORD'"
                )
                db.execSQL("ALTER TABLE Word ADD COLUMN verb_tense TEXT")
                db.execSQL("ALTER TABLE Word ADD COLUMN verb_yo TEXT")
                db.execSQL("ALTER TABLE Word ADD COLUMN verb_tu TEXT")
                db.execSQL("ALTER TABLE Word ADD COLUMN verb_elEllaUsted TEXT")
                db.execSQL("ALTER TABLE Word ADD COLUMN verb_nosotros TEXT")
                db.execSQL("ALTER TABLE Word ADD COLUMN verb_vosotros TEXT")
                db.execSQL("ALTER TABLE Word ADD COLUMN verb_ellosEllasUstedes TEXT")
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                wordDatabaseInstance = instance
                instance
            }
        }
    }
}
