package com.wordforge.ui.screens

import com.wordforge.data.SupportedVerbTenses
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
    fun supportedTensesAreFixedAndDictionaryStyleLowercase() {
        assertEquals(
            listOf(
                "presente de indicativo",
                "pretérito perfecto simple",
                "pretérito imperfecto",
                "futuro simple",
                "condicional simple",
                "presente de subjuntivo",
            ),
            SupportedVerbTenses,
        )
        assertTrue(SupportedVerbTenses.all { it.first().isLowerCase() })
    }
}
