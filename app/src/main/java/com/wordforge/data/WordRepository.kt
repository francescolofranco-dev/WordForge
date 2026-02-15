package com.wordforge.data

import com.wordforge.domain.SpacedRepetition
import kotlinx.coroutines.flow.Flow

class WordRepository(private val wordDao: WordDao) {

    fun getAllWords(): Flow<List<Word>> {
        return wordDao.getAll()
    }

    suspend fun getWordById(id: String): Word? {
        return wordDao.findWordById(wordId = id)
    }

    suspend fun update(word: Word) {
        wordDao.update(word)
    }

    suspend fun delete(word: Word) {
        wordDao.delete(word)
    }

    suspend fun deleteAll() {
        wordDao.deleteAll()
    }

    suspend fun getAllForNextPrompting(currentTime: Long): List<Word> {
        return wordDao.getAllForNextPrompting(currentTime)
    }

    /**
     * Creates a new word and inserts it into the database.
     * Returns the created Word so the caller can schedule a notification.
     */
    suspend fun addWord(word: String, meaning: String): Word {
        val currentTime = System.currentTimeMillis()
        val newWord = Word(
            word = word,
            meaning = meaning,
            createdAt = currentTime,
            nextPromptAt = currentTime + SpacedRepetition.getIntervalMs(0)
        )
        wordDao.insert(newWord)
        return newWord
    }

    /**
     * Updates the word after a correct answer.
     * Returns the updated Word so the caller can reschedule the notification.
     */
    suspend fun onAnswerCorrect(word: Word): Word {
        val newTier = SpacedRepetition.onCorrect(word.currentTier)
        val currentTime = System.currentTimeMillis()
        val updatedWord = word.copy(
            currentTier = newTier,
            nextPromptAt = currentTime + SpacedRepetition.getIntervalMs(newTier),
            totalCorrect = word.totalCorrect + 1,
            lastAnsweredAt = currentTime
        )
        wordDao.update(updatedWord)
        return updatedWord
    }

    /**
     * Updates the word after an incorrect answer.
     * Returns the updated Word so the caller can reschedule the notification.
     */
    suspend fun onAnswerIncorrect(word: Word): Word {
        val newTier = SpacedRepetition.onIncorrect(word.currentTier)
        val currentTime = System.currentTimeMillis()
        val updatedWord = word.copy(
            currentTier = newTier,
            nextPromptAt = currentTime + SpacedRepetition.getIntervalMs(newTier),
            totalIncorrect = word.totalIncorrect + 1,
            lastAnsweredAt = currentTime
        )
        wordDao.update(updatedWord)
        return updatedWord
    }
}