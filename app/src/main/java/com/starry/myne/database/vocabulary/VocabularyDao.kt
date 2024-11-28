package com.starry.myne.database.vocabulary

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.starry.myne.helpers.book.BookLanguage
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) interface for managing vocabulary operations in the database.
 * It provides methods for inserting, deleting, and querying vocabulary data.
 */
@Dao
interface VocabularyDao {

    /**
     * Inserts a vocabulary entry into the database. If the vocabulary already exists, it will be replaced.
     *
     * @param vocabulary The Vocabulary object to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vocabulary: Vocabulary)

    /**
     * Deletes a vocabulary entry from the database.
     *
     * @param vocabulary The Vocabulary object to be deleted.
     */
    @Delete
    fun delete(vocabulary: Vocabulary)


    @Query("SELECT * FROM vocabulary WHERE vocabulary_id = :vocabularyId")
    fun getVocabulary(vocabularyId: Int): Flow<Vocabulary>

    /**
     * Retrieves all vocabulary entries from the database.
     *
     * @return LiveData that contains a list of all vocabulary entries.
     */
    @Query("SELECT * FROM vocabulary")
    fun getAllVocabulary(): LiveData<List<Vocabulary>>

    /**
     * Retrieves all vocabulary entries that were sourced from a specific book.
     *
     * @param bookId The ID of the source book.
     * @return LiveData that contains a list of vocabulary entries for the specified book.
     */
    @Query("SELECT * FROM vocabulary WHERE source_book_id = :bookId")
    fun getAllWordsBySrcBookId(bookId: Int): LiveData<List<Vocabulary>>

    /**
     * Retrieves vocabulary entries by matching the source language and target language.
     *
     * @param srcLang The source language.
     * @param tarLang The target language.
     * @return LiveData that contains a list of vocabulary entries for the specified languages.
     */
    @Query("SELECT *FROM vocabulary WHERE source_language = :srcLang AND target_language = :tarLang")
    fun getWordsBySrcLangAndToLang(srcLang: String, tarLang: String): LiveData<List<Vocabulary>>
}