package com.wordforge.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert
    suspend fun insert(word: Word)

    @Update
    suspend fun update(word: Word)

    @Query("SELECT * FROM word ORDER BY nextPromptAt ASC")
    fun getAll(): Flow<List<Word>>

    @Query("SELECT * FROM word WHERE id = :wordId")
    suspend fun findWordById(wordId: String): Word?

    @Delete
    suspend fun delete(word: Word)

    @Query("DELETE FROM word")
    suspend fun deleteAll()

    @Query("SELECT * FROM word WHERE nextPromptAt <= :currentTime")
    suspend fun getAllForNextPrompting(currentTime: Long): List<Word>
}