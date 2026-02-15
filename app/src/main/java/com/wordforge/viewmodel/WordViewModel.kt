package com.wordforge.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wordforge.data.Word
import com.wordforge.data.WordDatabase
import com.wordforge.data.WordRepository
import com.wordforge.notification.NotificationScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WordRepository
    val allWords: StateFlow<List<Word>>

    init {
        val dao = WordDatabase.getDatabase(application).wordDao()
        repository = WordRepository(dao)

        allWords = repository.getAllWords()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun addWord(word: String, meaning: String) {
        viewModelScope.launch {
            val newWord = repository.addWord(word, meaning)
            scheduleNotification(newWord)
        }
    }

    fun onAnswerCorrect(word: Word) {
        viewModelScope.launch {
            val updatedWord = repository.onAnswerCorrect(word)
            scheduleNotification(updatedWord)
        }
    }

    fun onAnswerIncorrect(word: Word) {
        viewModelScope.launch {
            val updatedWord = repository.onAnswerIncorrect(word)
            scheduleNotification(updatedWord)
        }
    }

    fun deleteWord(word: Word) {
        viewModelScope.launch {
            repository.delete(word)
            NotificationScheduler.cancel(getApplication(), word.id)
        }
    }

    fun deleteAllWords() {
        viewModelScope.launch {
            repository.deleteAll()
            // cancelAll wipes everything including the daily catch-up,
            // so re-schedule it immediately after
            NotificationScheduler.cancelAll(getApplication())
            NotificationScheduler.scheduleDailyCatchUp(getApplication())
        }
    }

    suspend fun getWordById(id: String): Word? {
        return repository.getWordById(id)
    }

    private fun scheduleNotification(word: Word) {
        val delayMs = (word.nextPromptAt - System.currentTimeMillis()).coerceAtLeast(0)
        NotificationScheduler.schedule(
            context = getApplication(),
            wordId = word.id,
            wordText = word.word,
            delayMs = delayMs
        )
    }
}