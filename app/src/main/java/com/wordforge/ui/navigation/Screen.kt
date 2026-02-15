package com.wordforge.ui.navigation

sealed class Screen(val route: String) {
    object WordList : Screen("word_list")
    object AddWord : Screen("add_word")
    object Quiz : Screen("quiz/{wordId}") {
        fun createRoute(wordId: String) = "quiz/$wordId"
    }
    object WordDetail : Screen("word_detail/{wordId}") {
        fun createRoute(wordId: String) = "word_detail/$wordId"
    }
}