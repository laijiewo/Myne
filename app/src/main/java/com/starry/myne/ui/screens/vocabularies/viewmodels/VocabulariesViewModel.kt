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
import javax.inject.Inject

@HiltViewModel
class VocabulariesViewModel @Inject constructor(
    private val vocabularyDao: VocabularyDao,
): ViewModel() {

    val allVocabulary: LiveData<List<Vocabulary>> = vocabularyDao.getAllVocabulary()

    fun deleteVocabularyFromDB(vocabulary: Vocabulary) {
        viewModelScope.launch(Dispatchers.IO) { vocabularyDao.delete(vocabulary) }
    }

    fun insertNewVocabularyToDB(context: String,
                                srcLang: String,
                                tarLang: String,
                                translation: String,
                                onComplete: () -> Unit) {
        val vocabulary = Vocabulary(
            vocabulary = context,
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