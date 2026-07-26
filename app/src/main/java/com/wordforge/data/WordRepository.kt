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

    suspend fun getAllOnce(): List<Word> {
        return wordDao.getAllOnce()
    }

    suspend fun upsertAll(words: List<Word>) {
        wordDao.upsertAll(words)
    }

    /**
     * Creates a type-aware learning item and inserts it into the database.
     * Returns the persisted item so the caller can schedule a notification.
     */
    suspend fun addItem(draft: LearningItemDraft): Word {
        val normalized = draft.normalized()
        require(normalized.isComplete) { "Learning item is incomplete" }
        val currentTime = System.currentTimeMillis()
        val newWord = Word(
            word = normalized.term,
            meaning = normalized.meaning,
            createdAt = currentTime,
            nextPromptAt = currentTime + SpacedRepetition.nextDelayMs(0),
            randomlyFlip = normalized.randomlyFlip,
            itemType = normalized.type,
            verbConjugation = normalized.verbConjugation,
        )
        wordDao.insert(newWord)
        return newWord
    }

    /** Compatibility wrapper for callers that still create a simple word. */
    suspend fun addWord(word: String, meaning: String, randomlyFlip: Boolean): Word =
        addItem(
            LearningItemDraft(
                term = word,
                meaning = meaning,
                randomlyFlip = randomlyFlip,
            )
        )

    /**
     * Updates the word after a correct answer.
     * Returns the updated Word so the caller can reschedule the notification.
     */
    suspend fun onAnswerCorrect(word: Word): Word {
        val newTier = SpacedRepetition.onCorrect(word.currentTier)
        val currentTime = System.currentTimeMillis()
        val updatedWord = word.copy(
            currentTier = newTier,
            nextPromptAt = currentTime + SpacedRepetition.nextDelayMs(newTier),
            totalCorrect = word.totalCorrect + 1,
            lastAnsweredAt = currentTime,
            currentStreak = word.currentStreak + 1,
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
            nextPromptAt = currentTime + SpacedRepetition.nextDelayMs(newTier),
            totalIncorrect = word.totalIncorrect + 1,
            lastAnsweredAt = currentTime,
            currentStreak = 0,
        )
        wordDao.update(updatedWord)
        return updatedWord
    }
}
