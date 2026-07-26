package com.wordforge.data

/**
 * Selects the content-specific form and review experience for an item.
 * The review schedule itself remains shared across every item type.
 */
enum class LearningItemType(val displayName: String) {
    SIMPLE_WORD("Simple word"),
    VERB_CONJUGATION("Verb conjugation"),
}

/**
 * One complete Spanish conjugation. The six rows are intentionally fixed:
 * WordForge currently teaches Spanish only.
 */
data class VerbConjugation(
    val tense: String,
    val yo: String,
    val tu: String,
    val elEllaUsted: String,
    val nosotros: String,
    val vosotros: String,
    val ellosEllasUstedes: String,
) {
    fun rows(): List<ConjugationRow> = listOf(
        ConjugationRow("Yo", yo),
        ConjugationRow("Tú", tu),
        ConjugationRow("Él / ella / usted", elEllaUsted),
        ConjugationRow("Nosotros / nosotras", nosotros),
        ConjugationRow("Vosotros / vosotras", vosotros),
        ConjugationRow("Ellos / ellas / ustedes", ellosEllasUstedes),
    )

    fun normalized(): VerbConjugation = copy(
        tense = tense.trim(),
        yo = yo.trim(),
        tu = tu.trim(),
        elEllaUsted = elEllaUsted.trim(),
        nosotros = nosotros.trim(),
        vosotros = vosotros.trim(),
        ellosEllasUstedes = ellosEllasUstedes.trim(),
    )

    val isComplete: Boolean
        get() = tense.isNotBlank() && rows().all { it.form.isNotBlank() }

    companion object {
        val Empty = VerbConjugation(
            tense = "",
            yo = "",
            tu = "",
            elEllaUsted = "",
            nosotros = "",
            vosotros = "",
            ellosEllasUstedes = "",
        )
    }
}

data class ConjugationRow(
    val person: String,
    val form: String,
)

/**
 * Type-aware content submitted by the add/edit forms.
 */
data class LearningItemDraft(
    val type: LearningItemType = LearningItemType.SIMPLE_WORD,
    val term: String = "",
    val meaning: String = "",
    val randomlyFlip: Boolean = true,
    val verbConjugation: VerbConjugation? = null,
) {
    val isComplete: Boolean
        get() = term.isNotBlank() &&
            meaning.isNotBlank() &&
            (type != LearningItemType.VERB_CONJUGATION ||
                verbConjugation?.isComplete == true)

    fun normalized(): LearningItemDraft = copy(
        term = term.trim(),
        meaning = meaning.trim(),
        randomlyFlip = type == LearningItemType.SIMPLE_WORD && randomlyFlip,
        verbConjugation = if (type == LearningItemType.VERB_CONJUGATION) {
            verbConjugation?.normalized()
        } else {
            null
        },
    )
}

fun Word.toDraft(): LearningItemDraft = LearningItemDraft(
    type = itemType,
    term = word,
    meaning = meaning,
    randomlyFlip = randomlyFlip,
    verbConjugation = verbConjugation,
)

fun Word.withContent(draft: LearningItemDraft): Word {
    val normalized = draft.normalized()
    return copy(
        word = normalized.term,
        meaning = normalized.meaning,
        itemType = normalized.type,
        randomlyFlip = normalized.randomlyFlip,
        verbConjugation = normalized.verbConjugation,
    )
}
