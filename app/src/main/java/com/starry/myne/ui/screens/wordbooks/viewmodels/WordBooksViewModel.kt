package com.starry.myne.ui.screens.wordbooks.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.starry.myne.database.vocabulary.Vocabulary
import com.starry.myne.database.vocabulary.VocabularyDao
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.starry.myne.database.library.LibraryItem
import com.starry.myne.helpers.PreferenceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WordBooksViewModel @Inject constructor(
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