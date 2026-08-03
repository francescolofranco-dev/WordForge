package com.wordforge.ui.screens

import com.wordforge.data.LearningItemDraft
import com.wordforge.data.LearningItemType
import com.wordforge.data.VerbConjugation
import com.wordforge.data.Word
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class QuickAddItemFormTest {

    @Test
    fun parsesSixFormsFromSupportedSeparators() {
        assertEquals(
            listOf("digo", "dices", "dice", "decimos", "decís", "dicen"),
            parseSixConjugationForms("digo, dices; dice\tdecimos\ndecís\ndicen"),
        )
    }

    @Test
    fun stripsOptionalPersonLabelsAndKeepsCompoundFormsTogether() {
        val pasted = """
            yo: he dicho
            tú: has dicho
            él: ha dicho
            nosotros: hemos dicho
            vosotros: habéis dicho
            ellos: han dicho
        """.trimIndent()

        assertEquals(
            listOf(
                "he dicho",
                "has dicho",
                "ha dicho",
                "hemos dicho",
                "habéis dicho",
                "han dicho",
            ),
            parseSixConjugationForms(pasted),
        )
    }

    @Test
    fun personLabelsMayContainCommasWhenFormsAreOnSeparateLines() {
        val pasted = """
            yo: digo
            tú: dices
            él, ella, usted: dice
            nosotros, nosotras: decimos
            vosotros, vosotras: decís
            ellos, ellas, ustedes: dicen
        """.trimIndent()

        assertEquals(
            listOf("digo", "dices", "dice", "decimos", "decís", "dicen"),
            parseSixConjugationForms(pasted),
        )
    }

    @Test
    fun rejectsAnythingOtherThanSixForms() {
        assertNull(parseSixConjugationForms("digo, dices, dice, decimos, decís"))
        assertNull(parseSixConjugationForms("digo dices dice decimos decís dicen"))
        assertNull(parseSixConjugationForms(""))
    }

    @Test
    fun recentTensesComeBeforeDefaultsWithoutCaseInsensitiveDuplicates() {
        val items = listOf(
            verbWord("hablar", "presente DE indicativo", createdAt = 200L),
            verbWord("decir", "Pretérito imperfecto", createdAt = 300L),
        )

        val suggestions = suggestedVerbTenses(items)

        assertEquals("Pretérito imperfecto", suggestions[0])
        assertEquals("presente DE indicativo", suggestions[1])
        assertEquals(1, suggestions.count { it.equals("Presente de indicativo", true) })
        assertEquals(6, suggestions.size)
    }

    @Test
    fun reusesMeaningOnlyWhenExistingMeaningsAgree() {
        val existing = listOf(
            verbWord(
                term = " decir ",
                tense = "Presente de indicativo",
                createdAt = 100L,
                meaning = " to say ",
            ),
            verbWord(
                term = "DECIR",
                tense = "Pretérito imperfecto",
                createdAt = 200L,
                meaning = "to say",
            ),
        )

        assertEquals("to say", existingVerbMeaning("decir", existing))

        val conflicting = existing + verbWord(
            term = "decir",
            tense = "Futuro simple",
            createdAt = 300L,
            meaning = "to tell",
        )
        assertNull(existingVerbMeaning("decir", conflicting))
    }

    @Test
    fun meaningCanComeFromAnItemAddedInTheCurrentSession() {
        val submitted = LearningItemDraft(
            type = LearningItemType.VERB_CONJUGATION,
            term = "decir",
            meaning = "to say",
            verbConjugation = completeConjugation("Presente de indicativo"),
        )

        assertEquals(
            "to say",
            existingVerbMeaning(
                term = "DECIR",
                existingItems = emptyList(),
                submittedDrafts = listOf(submitted),
            ),
        )
    }

    private fun verbWord(
        term: String,
        tense: String,
        createdAt: Long,
        meaning: String = "meaning",
    ) = Word(
        word = term,
        meaning = meaning,
        nextPromptAt = createdAt + 1L,
        createdAt = createdAt,
        itemType = LearningItemType.VERB_CONJUGATION,
        verbConjugation = completeConjugation(tense),
    )

    private fun completeConjugation(tense: String) = VerbConjugation(
        tense = tense,
        yo = "one",
        tu = "two",
        elEllaUsted = "three",
        nosotros = "four",
        vosotros = "five",
        ellosEllasUstedes = "six",
    )
}
