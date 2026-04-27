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
import org.json.JSONArray
import org.json.JSONObject

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

    /**
     * Snapshot of every word as a pretty-printed JSON document.
     * Round-trips with [importFromJson] — every persisted field is preserved.
     */
    suspend fun exportToJson(): String {
        val words = repository.getAllOnce()
        val arr = JSONArray()
        for (w in words) {
            val o = JSONObject()
            o.put("id", w.id)
            o.put("word", w.word)
            o.put("meaning", w.meaning)
            o.put("currentTier", w.currentTier)
            o.put("nextPromptAt", w.nextPromptAt)
            o.put("createdAt", w.createdAt)
            if (w.lastAnsweredAt != null) o.put("lastAnsweredAt", w.lastAnsweredAt)
                else o.put("lastAnsweredAt", JSONObject.NULL)
            o.put("totalCorrect", w.totalCorrect)
            o.put("totalIncorrect", w.totalIncorrect)
            arr.put(o)
        }
        return JSONObject().apply {
            put("version", 1)
            put("exportedAt", System.currentTimeMillis())
            put("count", words.size)
            put("words", arr)
        }.toString(2)
    }

    /**
     * Imports words from a JSON document produced by [exportToJson].
     * Existing words with the same id are overwritten; new ones are inserted.
     * Returns the number of words processed. Throws if the JSON is malformed.
     */
    suspend fun importFromJson(json: String): Int {
        val root = JSONObject(json)
        val arr = root.getJSONArray("words")
        val list = ArrayList<Word>(arr.length())
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(
                Word(
                    id = o.getString("id"),
                    word = o.getString("word"),
                    meaning = o.getString("meaning"),
                    currentTier = o.getInt("currentTier"),
                    nextPromptAt = o.getLong("nextPromptAt"),
                    createdAt = o.getLong("createdAt"),
                    lastAnsweredAt = if (o.isNull("lastAnsweredAt")) null else o.getLong("lastAnsweredAt"),
                    totalCorrect = o.getInt("totalCorrect"),
                    totalIncorrect = o.getInt("totalIncorrect"),
                )
            )
        }
        repository.upsertAll(list)
        list.forEach { scheduleNotification(it) }
        return list.size
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