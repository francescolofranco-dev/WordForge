package com.wordforge.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Embedded
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Word (
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val word: String,
    val meaning: String,
    val currentTier: Int = 0,
    val nextPromptAt: Long,
    val createdAt: Long,
    val lastAnsweredAt: Long? = null,
    val totalCorrect: Int = 0,
    val totalIncorrect: Int = 0,
    // Consecutive correct answers since the last "Nope" tap. Reset to 0
    // on any incorrect answer; incremented on each correct one.
    val currentStreak: Int = 0,
    // When true, the quiz randomly shows either the word or the meaning as
    // the prompt. When false, the word is always the prompt (recall meaning).
    val randomlyFlip: Boolean = true,
    // Determines which content-specific form, detail view, and quiz to render.
    @ColumnInfo(defaultValue = "'SIMPLE_WORD'")
    val itemType: LearningItemType = LearningItemType.SIMPLE_WORD,
    // Populated only for VERB_CONJUGATION items. All six Spanish-person forms
    // are reviewed and scored together as one spaced-repetition item.
    @Embedded(prefix = "verb_")
    val verbConjugation: VerbConjugation? = null,
)
