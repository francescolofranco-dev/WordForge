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

    data class ImportPreview(
        val words: List<Word>,
        val newCount: Int,
        val updatedCount: Int,
    ) {
        val totalCount: Int get() = words.size
    }

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

    fun addWord(word: String, meaning: String, randomlyFlip: Boolean) {
        viewModelScope.launch {
            val newWord = repository.addWord(word, meaning, randomlyFlip)
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
            NotificationScheduler.refreshSummary(getApplication())
        }
    }

    fun restoreWord(word: Word) {
        viewModelScope.launch {
            repository.upsertAll(listOf(word))
            scheduleAccordingToDueTime(word)
        }
    }

    /**
     * Persists edits to a word's text, meaning, or flip setting. The review
     * schedule (nextPromptAt) is untouched, so no notification reschedule is
     * needed. The reminder worker resolves the latest text by id at fire time.
     */
    fun updateWord(word: Word) {
        viewModelScope.launch {
            repository.update(word)
        }
    }

    fun deleteAllWords() {
        viewModelScope.launch {
            val wordIds = repository.getAllOnce().map { it.id }
            repository.deleteAll()
            // Leave the unique daily catch-up work intact; only word work belongs here.
            NotificationScheduler.cancelAll(getApplication(), wordIds)
        }
    }

    suspend fun getWordById(id: String): Word? {
        return repository.getWordById(id)
    }

    suspend fun getOverdueWords(): List<Word> {
        return repository.getAllForNextPrompting(System.currentTimeMillis())
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
            o.put("currentStreak", w.currentStreak)
            o.put("randomlyFlip", w.randomlyFlip)
            arr.put(o)
        }
        return JSONObject().apply {
            put("version", 3)
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
    suspend fun previewImport(json: String): ImportPreview {
        val root = JSONObject(json)
        val version = root.optInt("version", 1)
        require(version in 1..3) { "Unsupported export version: $version" }
        val arr = root.getJSONArray("words")
        require(arr.length() <= MAX_IMPORT_WORDS) {
            "This file contains too many words (${arr.length()})"
        }
        val list = ArrayList<Word>(arr.length())
        val seenIds = HashSet<String>(arr.length())
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val importedWord = Word(
                id = o.getString("id"),
                word = o.getString("word").trim(),
                meaning = o.getString("meaning").trim(),
                currentTier = o.getInt("currentTier"),
                nextPromptAt = o.getLong("nextPromptAt"),
                createdAt = o.getLong("createdAt"),
                lastAnsweredAt = if (o.isNull("lastAnsweredAt")) null else o.getLong("lastAnsweredAt"),
                totalCorrect = o.getInt("totalCorrect"),
                totalIncorrect = o.getInt("totalIncorrect"),
                currentStreak = if (o.has("currentStreak")) o.getInt("currentStreak") else 0,
                randomlyFlip = if (o.has("randomlyFlip")) o.getBoolean("randomlyFlip") else true,
            )
            require(importedWord.id.isNotBlank()) { "Word ${i + 1} has no id" }
            require(seenIds.add(importedWord.id)) { "Duplicate word id at item ${i + 1}" }
            require(importedWord.word.isNotBlank()) { "Word ${i + 1} has empty text" }
            require(importedWord.meaning.isNotBlank()) { "Word ${i + 1} has an empty meaning" }
            require(importedWord.currentTier in 0..8) { "Word ${i + 1} has an invalid tier" }
            require(importedWord.nextPromptAt > 0L && importedWord.createdAt > 0L) {
                "Word ${i + 1} has an invalid date"
            }
            require(importedWord.lastAnsweredAt == null || importedWord.lastAnsweredAt > 0L) {
                "Word ${i + 1} has an invalid last-answer date"
            }
            require(importedWord.totalCorrect >= 0 && importedWord.totalIncorrect >= 0) {
                "Word ${i + 1} has invalid answer totals"
            }
            require(importedWord.currentStreak >= 0) { "Word ${i + 1} has an invalid streak" }
            list.add(importedWord)
        }

        val existingIds = repository.getAllOnce().mapTo(HashSet()) { it.id }
        val updatedCount = list.count { it.id in existingIds }
        return ImportPreview(
            words = list,
            newCount = list.size - updatedCount,
            updatedCount = updatedCount,
        )
    }

    suspend fun commitImport(preview: ImportPreview): Int {
        val list = preview.words
        repository.upsertAll(list)
        val now = System.currentTimeMillis()
        list.forEach { word ->
            if (word.nextPromptAt > now) {
                scheduleNotification(word)
            } else {
                NotificationScheduler.cancel(getApplication(), word.id)
            }
        }
        NotificationScheduler.refreshSummary(getApplication())
        return list.size
    }

    /** Kept as a small compatibility wrapper for callers outside the UI. */
    suspend fun importFromJson(json: String): Int = commitImport(previewImport(json))

    private fun scheduleNotification(word: Word) {
        val delayMs = (word.nextPromptAt - System.currentTimeMillis()).coerceAtLeast(0)
        NotificationScheduler.schedule(
            context = getApplication(),
            wordId = word.id,
            delayMs = delayMs
        )
    }

    private fun scheduleAccordingToDueTime(word: Word) {
        if (word.nextPromptAt > System.currentTimeMillis()) {
            scheduleNotification(word)
        } else {
            NotificationScheduler.refreshSummary(getApplication())
        }
    }

    private companion object {
        const val MAX_IMPORT_WORDS = 50_000
    }
}
