package com.starry.myne.database.vocabulary

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a vocabulary entry in the database.
 * Each vocabulary consists of the word/phrase, its source and target languages, a translation,
 * and optionally the ID of the book it was sourced from.
 *
 * @param vocabularyId The primary key, auto-generated for each vocabulary.
 * @param sourceBookId The ID of the book where the vocabulary comes from (optional).
 * @param vocabulary The word or phrase.
 * @param srcLang The source language of the vocabulary.
 * @param tarLang The target language of the vocabulary.
 * @param translation The translation of the vocabulary into the target language.
 */
@Entity(tableName = "vocabulary")
data class Vocabulary(
    @ColumnInfo(name = "vocabulary_id")
    @PrimaryKey(autoGenerate = true) val vocabularyId: Int? = null,
    @ColumnInfo(name = "source_book_id")
    val sourceBookId: Int? = null,
    @ColumnInfo(name = "vocabulary")
    val vocabulary: String,
    @ColumnInfo(name = "source_language")
    val srcLang: String,
    @ColumnInfo(name = "target_language")
    val tarLang: String,
    @ColumnInfo(name = "translation")
    val translation: String
) {
    /**
     * Returns a formatted string combining the vocabulary and its translation.
     *
     * @return A formatted string in the format "Word: <vocabulary>, Translation: <translation>"
     */
    fun getFormattedVocabulary(): String {
        return "Word: $vocabulary, Translation: $translation"
    }
}