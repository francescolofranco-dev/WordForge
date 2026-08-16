package com.wordforge.data

/**
 * The grammatical tenses currently supported by the verb-conjugation flow.
 *
 * Keeping this list fixed prevents newly created or edited verbs from using
 * arbitrary labels while still allowing legacy items to keep their old value.
 */
val SupportedVerbTenses = listOf(
    "presente de indicativo",
    "pretérito perfecto simple",
    "pretérito imperfecto",
    "futuro simple",
    "condicional simple",
    "presente de subjuntivo",
)
