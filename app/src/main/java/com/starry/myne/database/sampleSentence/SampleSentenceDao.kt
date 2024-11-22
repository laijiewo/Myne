package com.starry.myne.database.sampleSentence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) for interacting with the "sample_sentence" table in the database.
 *
 * This interface provides methods for inserting, deleting, and retrieving sample sentences.
 * It integrates with LiveData for observing database changes in real-time.
 */
@Dao
interface SampleSentenceDao {

    /**
     * Inserts a new sample sentence into the "sample_sentence" table.
     * If a conflict occurs (e.g., a duplicate `sentence_id`), the existing record will be replaced.
     *
     * @param sampleSentence The sample sentence to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sampleSentence: SampleSentence)

    /**
     * Deletes an existing sample sentence from the "sample_sentence" table.
     *
     * @param sampleSentence The sample sentence to be deleted.
     */
    @Delete
    fun delete(sampleSentence: SampleSentence)

    /**
     * Retrieves all sample sentences associated with a specific vocabulary ID.
     * The results are wrapped in a `LiveData` object, enabling real-time updates
     * whenever the data in the database changes.
     *
     * @param vocabularyId The ID of the vocabulary entry whose sample sentences are to be retrieved.
     * @return A `LiveData` object containing a list of `SampleSentence` objects associated with the given vocabulary ID.
     */
    @Query("SELECT * FROM sample_sentence WHERE vocabulary_id = :vocabularyId")
    fun getAllByVocabularyId(vocabularyId: Int): LiveData<List<SampleSentence>>
}