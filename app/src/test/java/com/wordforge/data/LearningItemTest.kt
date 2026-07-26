package com.wordforge.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LearningItemTest {
    private val decir = VerbConjugation(
        tense = " Presente de indicativo ",
        yo = " digo ",
        tu = " dices ",
        elEllaUsted = " dice ",
        nosotros = " decimos ",
        vosotros = " decís ",
        ellosEllasUstedes = " dicen ",
    )

    @Test
    fun spanishConjugationHasTheSixFixedPersonGroups() {
        val rows = decir.normalized().rows()

        assertEquals(
            listOf(
                "Yo",
                "Tú",
                "Él / ella / usted",
                "Nosotros / nosotras",
                "Vosotros / vosotras",
                "Ellos / ellas / ustedes",
            ),
            rows.map { it.person },
        )
        assertEquals(
            listOf("digo", "dices", "dice", "decimos", "decís", "dicen"),
            rows.map { it.form },
        )
    }

    @Test
    fun verbDraftRequiresEveryFormAndDisablesRandomFlip() {
        val complete = LearningItemDraft(
            type = LearningItemType.VERB_CONJUGATION,
            term = " decir ",
            meaning = " to say ",
            randomlyFlip = true,
            verbConjugation = decir,
        )

        assertTrue(complete.isComplete)
        val normalized = complete.normalized()
        assertEquals("decir", normalized.term)
        assertEquals("Presente de indicativo", normalized.verbConjugation?.tense)
        assertFalse(normalized.randomlyFlip)

        assertFalse(
            complete.copy(
                verbConjugation = decir.copy(vosotros = "")
            ).isComplete
        )
    }

    @Test
    fun simpleWordDropsUnusedVerbPayload() {
        val normalized = LearningItemDraft(
            type = LearningItemType.SIMPLE_WORD,
            term = " hola ",
            meaning = " hello ",
            verbConjugation = decir,
        ).normalized()

        assertTrue(normalized.isComplete)
        assertEquals("hola", normalized.term)
        assertNull(normalized.verbConjugation)
    }

    @Test
    fun editingContentPreservesReviewProgress() {
        val original = Word(
            id = "id",
            word = "decir",
            meaning = "to say",
            currentTier = 4,
            nextPromptAt = 200L,
            createdAt = 100L,
            totalCorrect = 8,
        )
        val updated = original.withContent(
            LearningItemDraft(
                type = LearningItemType.VERB_CONJUGATION,
                term = "decir",
                meaning = "to tell or say",
                verbConjugation = decir,
            )
        )

        assertEquals(LearningItemType.VERB_CONJUGATION, updated.itemType)
        assertEquals(4, updated.currentTier)
        assertEquals(200L, updated.nextPromptAt)
        assertEquals(8, updated.totalCorrect)
    }
}
