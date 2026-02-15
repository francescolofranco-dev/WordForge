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
)