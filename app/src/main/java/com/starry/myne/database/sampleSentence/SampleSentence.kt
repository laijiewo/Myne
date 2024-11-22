package com.starry.myne.database.sampleSentence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.starry.myne.database.vocabulary.Vocabulary

/**
 * Represents a database entity for storing sample sentences related to specific vocabulary words.
 *
 * This table is named "sample_sentence" and has a foreign key relationship with the "Vocabulary" table.
 * Each sample sentence is associated with a vocabulary entry and contains additional information
 * such as the sentence text and its resource.
 *
 * @property sentenceId The unique identifier for the sample sentence. Auto-generated primary key.
 * @property sentence The text of the sample sentence. This field is non-nullable.
 * @property resource The source book name of the sample sentence.
 * @property vocabularyId The foreign key referencing the related vocabulary entry in the "Vocabulary" table.
 */
@Entity(tableName = "sample_sentence",
    foreignKeys = [
        ForeignKey(
            entity = Vocabulary::class,
            parentColumns = ["vocabulary_id"],
            childColumns = ["vocabulary_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("vocabulary_id")]
)
data class SampleSentence(
    @ColumnInfo(name = "sentence_id")
    @PrimaryKey(autoGenerate = true)
    val sentenceId: Int? = null, // Auto-incremented unique ID for the sample sentence.

    @ColumnInfo(name = "sentence")
    val sentence: String, // The actual sample sentence. Cannot be null.

    @ColumnInfo(name = "resource")
    val resource: String, // Source book name of the sample sentence.

    @ColumnInfo(name = "vocabulary_id")
    val vocabularyId: Int // Foreign key linking to the related vocabulary entry.
)