package com.starry.myne.database.vocabulary

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
    fun getFormattedVocabulary(): String {
        return "Word: $vocabulary, Translation: $translation"
    }
}