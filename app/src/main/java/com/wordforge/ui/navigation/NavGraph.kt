package com.wordforge.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.wordforge.ui.screens.AddWordScreen
import com.wordforge.ui.screens.QuizScreen
import com.wordforge.ui.screens.WordDetailScreen
import com.wordforge.ui.screens.WordListScreen
import com.wordforge.viewmodel.WordViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: WordViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.WordList.route
    ) {
        // Home screen — list of all words
        composable(Screen.WordList.route) {
            WordListScreen(
                viewModel = viewModel,
                onNavigateToAddWord = {
                    navController.navigate(Screen.AddWord.route)
                },
                onNavigateToDetail = { wordId ->
                    navController.navigate(Screen.WordDetail.createRoute(wordId))
                }
            )
        }

        // Add a new word
        composable(Screen.AddWord.route) {
            AddWordScreen(
                onAddWord = { word, meaning ->
                    viewModel.addWord(word, meaning)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Quiz — opened from notification or from word list
        composable(
            route = Screen.Quiz.route,
            arguments = listOf(navArgument("wordId") { type = NavType.StringType })
        ) { backStackEntry ->
            val wordId = backStackEntry.arguments?.getString("wordId") ?: return@composable
            QuizScreen(
                wordId = wordId,
                viewModel = viewModel,
                onFinished = {
                    navController.popBackStack()
                }
            )
        }

        // Word detail — tap a word in the list
        composable(
            route = Screen.WordDetail.route,
            arguments = listOf(navArgument("wordId") { type = NavType.StringType })
        ) { backStackEntry ->
            val wordId = backStackEntry.arguments?.getString("wordId") ?: return@composable
            WordDetailScreen(
                wordId = wordId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}