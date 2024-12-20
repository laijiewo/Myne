package com.starry.myne.ui.screens.vocabularies.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.starry.myne.database.vocabulary.Vocabulary
import com.starry.myne.database.vocabulary.VocabularyDao
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * ViewModel class responsible for managing the logic and data for the vocabulary screen.
 * It interacts with the database through VocabularyDao to perform CRUD operations on the vocabulary table.
 * The ViewModel uses a LiveData object to observe and retrieve all vocabularies from the database.
 *
 * @param vocabularyDao Data Access Object to interact with the vocabulary table in the database.
 */
@HiltViewModel
class VocabulariesViewModel @Inject constructor(
    private val vocabularyDao: VocabularyDao,
): ViewModel() {

    /**
     * LiveData that holds a list of all vocabularies from the database.
     * Observing this will provide updates when the vocabulary list changes.
     */
    val allVocabulary: LiveData<List<Vocabulary>> = vocabularyDao.getAllVocabulary()

    /**
     * Retrieves a specific vocabulary by its ID from the database.
     *
     * @param vocabularyId The ID of the vocabulary to retrieve.
     * @return A Flow that emits the Vocabulary object corresponding to the provided ID.
     */
    fun getVocabulary(vocabularyId: Int): Flow<Vocabulary> {
        return vocabularyDao.getVocabulary(vocabularyId)
    }

    /**
     * Deletes a given vocabulary item from the database.
     *
     * @param vocabulary The Vocabulary object to be deleted.
     */
    fun deleteVocabularyFromDB(vocabulary: Vocabulary) {
        viewModelScope.launch(Dispatchers.IO) { vocabularyDao.delete(vocabulary) }
    }

    /**
     * Checks if a vocabulary already exists in the database by its string value.
     *
     * @param vocabulary The word to check in the database.
     * @return A Flow that emits the vocabulary ID if it exists, or null if not.
     */
    fun isVocabularyExist(vocabulary: String): Flow<Int?> {
        return vocabularyDao.getVocabularyId(vocabulary)
    }

    /**
     * Inserts a new vocabulary item into the database and triggers a completion callback once done.
     * This method runs the insert operation in a background thread (IO dispatcher), and once complete,
     * the callback is executed on the main thread.
     *
     * @param context The word or phrase to be inserted.
     * @param srcLang The source language of the word.
     * @param tarLang The target language of the word.
     * @param translation The translation of the word into the target language.
     * @param onComplete Callback function triggered after the insertion is complete.
     */
    fun insertNewVocabularyToDB(context: String,
                                srcLang: String,
                                tarLang: String,
                                translation: String,
                                onComplete: () -> Unit) {
        val vocabulary = Vocabulary(
            vocabulary = context.lowercase(),
            srcLang = srcLang,
            tarLang = tarLang,
            translation = translation
        )
        viewModelScope.launch(Dispatchers.IO) { vocabularyDao.insert(vocabulary)

            withContext(Dispatchers.Main) {
                onComplete()
            }
        }

    }
}