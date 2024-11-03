package com.starry.myne.database.vocabulary

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.starry.myne.helpers.book.BookLanguage

@Dao
interface VocabularyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vocabulary: Vocabulary)

    @Delete
    fun delete(vocabulary: Vocabulary)

    @Query("SELECT * FROM vocabulary")
    fun getAllVocabulary(): LiveData<List<Vocabulary>>

    @Query("SELECT * FROM vocabulary WHERE source_book_id = :bookId")
    fun getAllWordsBySrcBookId(bookId: Int): LiveData<List<Vocabulary>>

    @Query("SELECT *FROM vocabulary WHERE source_language = :srcLang AND target_language = :tarLang")
    fun getWordsBySrcLangAndToLang(srcLang: String, tarLang: String): LiveData<List<Vocabulary>>
}