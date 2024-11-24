package com.starry.myne.ui.screens.sample_sentence.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starry.myne.database.sampleSentence.SampleSentence
import com.starry.myne.database.sampleSentence.SampleSentenceDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel for managing the business logic of the SampleSentence screen.
 * Provides methods for interacting with the `SampleSentence` database table, including retrieval,
 * insertion, and deletion of sample sentences. This ViewModel is lifecycle-aware and integrates
 * with Room Database and Hilt for dependency injection.
 *
 * @constructor Injects the required dependencies using Hilt.
 * @property sampleSentenceDao DAO for interacting with the `SampleSentence` table.
 */
@HiltViewModel
class SampleSentenceViewModel @Inject constructor(
    private val sampleSentenceDao: SampleSentenceDao,

): ViewModel() {
    /**
     * Holds all sample sentences related to a specific vocabulary ID.
     * This LiveData is initialized when calling `getAllSampleSentence`.
     */
    private lateinit var allSentence: LiveData<List<SampleSentence>>

    /**
     * Retrieves all sample sentences associated with a specific vocabulary ID.
     * The results are stored in the `allSentence` LiveData variable.
     *
     * @param vocabularyId The ID of the vocabulary for which sample sentences are fetched.
     */
    fun getAllSampleSentence(vocabularyId: Int):  LiveData<List<SampleSentence>>{
        allSentence = sampleSentenceDao.getAllByVocabularyId(vocabularyId)
        return allSentence
    }

    /**
     * Deletes a specific sample sentence from the database.
     *
     * @param sampleSentence The sample sentence to be deleted.
     */
    fun deleteSampleSentenceFromDB(sampleSentence: SampleSentence) {
        viewModelScope.launch(Dispatchers.IO) {
            sampleSentenceDao.delete(sampleSentence)
        }
    }

    /**
     * Deletes all sample sentences associated with a specific vocabulary ID from the database.
     * This method uses the current value of `allSentence` LiveData.
     *
     * @param vocabularyId The ID of the vocabulary for which all sample sentences should be deleted.
     */
    fun deleteAllFromDB(vocabularyId: Int) {
        if (::allSentence.isInitialized) {
            allSentence = sampleSentenceDao.getAllByVocabularyId(vocabularyId)
            val sentenceList = allSentence.value
            if (sentenceList != null) { // Ensure the data is not null
                for (sentence in sentenceList) {
                    viewModelScope.launch(Dispatchers.IO) {
                        sampleSentenceDao.delete(sentence)
                    }

                }
            }
        }
         else {
            // Handle case where the LiveData has no value
            println("No sentences to delete!")
        }
    }

    /**
     * Inserts a new sample sentence into the database.
     * Calls the provided `onComplete` callback after insertion is complete.
     *
     * @param sentence The content of the sample sentence to be inserted.
     * @param resourceBookName The name of the resource or book where the sentence is sourced from.
     * @param vocabularyId The ID of the vocabulary this sentence is associated with.
     * @param onComplete A callback executed after the insertion operation is completed.
     */
    fun insertNewSampleSentenceToDB(sentence: String,
                                    resourceBookName: String,
                                    vocabularyId: Int,
                                    onComplete: () -> Unit) {
        val sampleSentence = SampleSentence(
            sentence = sentence,
            resource = resourceBookName,
            vocabularyId = vocabularyId
        )
        viewModelScope.launch(Dispatchers.IO) { sampleSentenceDao.insert(sampleSentence)

            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }
}