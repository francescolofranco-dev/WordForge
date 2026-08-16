package com.wordforge.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wordforge.ui.theme.WordForgeTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WordCardInstrumentedTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun verbTenseUsesTheFullWidthAboveTheVerbWithoutMeaning() {
        composeRule.setContent {
            WordForgeTheme {
                Box(Modifier.width(320.dp)) {
                    WordCard(
                        word = "tener",
                        meaning = null,
                        tier = 0,
                        dueLabel = "57m 53s",
                        typeLabel = "PRETÉRITO PERFECTO SIMPLE",
                        onClick = {},
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }
        }

        composeRule.onNodeWithText("PRETÉRITO PERFECTO SIMPLE").assertExists()
        composeRule.onNodeWithText("VERB · PRETÉRITO PERFECTO SIMPLE").assertDoesNotExist()
        composeRule.onNodeWithTag("word_card_meaning", useUnmergedTree = true)
            .assertDoesNotExist()

        val labelBounds = composeRule
            .onNodeWithTag("word_card_type_label", useUnmergedTree = true)
            .fetchSemanticsNode()
            .boundsInRoot
        val termBounds = composeRule
            .onNodeWithTag("word_card_term", useUnmergedTree = true)
            .fetchSemanticsNode()
            .boundsInRoot

        assertTrue(labelBounds.bottom <= termBounds.top)
        assertEquals(labelBounds.left, termBounds.left, 1f)
    }

    @Test
    fun simpleWordMeaningPreservesEnteredCapitalization() {
        composeRule.setContent {
            WordForgeTheme {
                WordCard(
                    word = "tiempo",
                    meaning = "time",
                    tier = 0,
                    dueLabel = "ready",
                    onClick = {},
                )
            }
        }

        composeRule.onNodeWithText("tiempo").assertExists()
        composeRule.onNodeWithText("time").assertExists()
        composeRule.onNodeWithText("Time").assertDoesNotExist()
    }
}
