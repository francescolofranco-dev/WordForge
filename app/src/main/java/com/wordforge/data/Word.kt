package com.wordforge.data

import androidx.room.Entity
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
)