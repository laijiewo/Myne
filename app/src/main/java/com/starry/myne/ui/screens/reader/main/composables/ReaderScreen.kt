/**
 * Copyright (c) [2022 - Present] Stɑrry Shivɑm
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.starry.myne.ui.screens.reader.main.composables

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.starry.myne.R
import com.starry.myne.api.TextToSpeechHelper
import com.starry.myne.api.translateWithDelay
import com.starry.myne.helpers.toToast
import com.starry.myne.ui.screens.reader.main.viewmodel.ReaderScreenState
import com.starry.myne.ui.screens.reader.main.viewmodel.ReaderViewModel
import com.starry.myne.ui.screens.sample_sentence.viewmodels.SampleSentenceViewModel
import com.starry.myne.ui.screens.vocabularies.viewmodels.VocabulariesViewModel
import com.starry.myne.ui.theme.poppinsFont
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel,
    vocabulariesViewModel: VocabulariesViewModel,
    onScrollToChapter: suspend (Int) -> Unit,
    chaptersContent: @Composable (paddingValues: PaddingValues) -> Unit
) {
    val context = LocalContext.current
    val state = viewModel.state.collectAsState().value
    // Hide reader menu on back press.
    BackHandler(state.showReaderMenu) {
        viewModel.hideReaderInfo()
    }

    val textToSpeechHelper = TextToSpeechHelper(context)
    val showFontDialog = remember { mutableStateOf(false) }
    ReaderFontChooserDialog(
        showFontDialog = showFontDialog,
        fontFamily = state.fontFamily,
        onFontFamilyChanged = { viewModel.setFontFamily(it) }
    )

    val snackBarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    BackHandler(drawerState.isOpen) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        }
    }

    ChaptersDrawer(
        drawerState = drawerState,
        chapters = state.chapters,
        currentChapterIndex = state.currentChapterIndex,
        onScrollToChapter = { coroutineScope.launch { onScrollToChapter(it) } },
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
            topBar = {
                ReaderTopAppBar(
                    state = state,
                    onChapterListClicked = { coroutineScope.launch { drawerState.open() } }
                )
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = state.showReaderMenu,
                    enter = expandVertically(initialHeight = { 0 }) + fadeIn(),
                    exit = shrinkVertically(targetHeight = { 0 }) + fadeOut(),
                ) {
                    ReaderBottomBar(
                        state = state,
                        showFontDialog = showFontDialog,
                        snackBarHostState = snackBarHostState,
                        onFontSizeChanged = { viewModel.setFontSize(it) },
                    )
                }
                VocabularyMenu(
                    state = state,
                    onAddToWordBook = {
                        val vocabulary = viewModel.getSelectedVocabulary()
                        val sourceLang = "en"
                        val targetLang = "cn"
                        val translation = viewModel.getTranslation()

                        // Add to database
                        vocabulariesViewModel.insertNewVocabularyToDB(
                            vocabulary,
                            sourceLang,
                            targetLang,
                            translation,
                            onComplete = {})
                        viewModel.setTranslation("translating....")
                        context.getString(R.string.add_to_word_book).toToast(context)
                    },
                    getVocabulary = { viewModel.getSelectedVocabulary() },
                    getSentence = { viewModel.getSelectedSentence() },
                    setTranslation = { translation ->
                        viewModel.setTranslation(translation = translation)
                    },
                    textToSpeechHelper = textToSpeechHelper,
                    isVocabularyExist = { vocabulary ->
                        vocabulariesViewModel.isVocabularyExist(vocabulary)
                    }
                )
            },
            content = { paddingValues ->
                Crossfade(
                    targetState = state.isLoading,
                    label = "reader content loading cross fade"
                ) { loading ->
                    if (loading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 65.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (state.shouldShowLoader) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    } else {
                        chaptersContent(paddingValues)
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderTopAppBar(
    state: ReaderScreenState,
    onChapterListClicked: () -> Unit,
) {
    AnimatedVisibility(
        visible = state.showReaderMenu,
        enter = expandVertically(initialHeight = { 0 }, expandFrom = Alignment.Top)
                + fadeIn(),
        exit = shrinkVertically(targetHeight = { 0 }, shrinkTowards = Alignment.Top)
                + fadeOut(),
    ) {
        Surface(color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)) {
            Column(modifier = Modifier.displayCutoutPadding()) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            2.dp
                        ),
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            2.dp
                        ),
                    ),
                    title = {
                        Text(
                            text = state.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.animateContentSize(),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = poppinsFont
                        )

                    },
                    actions = {
                        IconButton(onClick = onChapterListClicked) {
                            Icon(
                                Icons.Filled.Menu, null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
                Column(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .padding(horizontal = 16.dp),
                ) {
                    Text(
                        text = state.currentChapter.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = poppinsFont,
                        fontWeight = FontWeight.Medium
                    )
                }
                val chapterProgressbarState = animateFloatAsState(
                    targetValue = state.chapterScrollPercent,
                    label = "chapter progress bar state animation"
                )
                LinearProgressIndicator(
                    progress = { chapterProgressbarState.value },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

/**
 * A Composable that displays a vocabulary menu with options to add the word to the vocabulary book,
 * listen to its pronunciation, and show translations.
 *
 * @param state The current state of the reader screen, including whether the vocabulary menu is shown.
 * @param onAddToWordBook A lambda that adds the selected vocabulary to the vocabulary book.
 * @param getVocabulary A function that returns the selected vocabulary word.
 * @param getSentence A function that returns the selected sentence.
 * @param setTranslation A function that sets the translation of the vocabulary.
 * @param textToSpeechHelper An instance of the TextToSpeechHelper to manage text-to-speech functionality.
 * @param isVocabularyExist A function that checks if the vocabulary word already exists in the database.
 */
@Composable
private fun VocabularyMenu(
    state: ReaderScreenState,
    onAddToWordBook: () -> Unit,
    getVocabulary: () -> String,
    getSentence: () -> List<String>,
    setTranslation: (String) -> Unit,
    textToSpeechHelper: TextToSpeechHelper,
    isVocabularyExist: (String) -> Flow<Int?>
) {
    val context = LocalContext.current
    val vocabulary = getVocabulary()
    val vocabularyId = isVocabularyExist(vocabulary.lowercase()).collectAsState(initial = null).value
    val translation = remember { mutableStateOf("translating....") }
    val showSentencesDialog = remember { mutableStateOf(false) }
    val sentenceViewModel: SampleSentenceViewModel = hiltViewModel()

    // Trigger translation on vocabulary
    translateWithDelay(getVocabulary(), "zh") { result ->
        if (result != null) {
            println(result)
            translation.value = result
            setTranslation(translation.value)
        } else {
            ("translate failed").toToast(context)
        }
    }

    // Animated visibility of the vocabulary menu
    AnimatedVisibility(
        visible = state.showVocabularyMenu,
        enter = expandVertically(initialHeight = { 0 }, expandFrom = Alignment.Top)
                + fadeIn(),
        exit = shrinkVertically(targetHeight = { 0 }, shrinkTowards = Alignment.Top)
                + fadeOut(),
    ) {
        // Surface for displaying the vocabulary menu
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 8.dp,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Column to arrange elements vertically
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                // Row for displaying the vocabulary word and buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Vocabulary title
                    Text(
                        text = vocabulary,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    // Button to show sentences containing the vocabulary
                    IconButton(
                        onClick = {
                            if (vocabularyId != null) {
                                showSentencesDialog.value = true
                            } else {
                                ("Add vocabulary first!").toToast(context)
                            }
                        },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "add"
                        )
                    }
                    // Button to trigger text-to-speech for the vocabulary word
                    IconButton(
                        onClick = {
                            if (textToSpeechHelper.isInitialized) {
                                textToSpeechHelper.speak(vocabulary)
                            } else {
                                Toast.makeText(context, "TTS is not ready yet!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_speaker),
                            contentDescription = "speaker_icon"
                        )
                    }
                }
                // Display the translation
                Text(
                    text = translation.value,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                // Button to add the word to the vocabulary book if not already added
                if (vocabularyId != null) {
                    Button(
                        onClick = {  },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    ) {
                        Text(text = "Added to Vocabulary Book")
                    }
                } else {
                    Button(
                        onClick = { onAddToWordBook() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = translation.value != "translating...."
                    ) {
                        Text(text = "Add to Vocabulary Book")
                    }
                }
            }
        }
        // Display a dialog with a list of sentences if the dialog should be shown and the vocabulary exists in the database
        if (showSentencesDialog.value && vocabularyId != null) {
            SentenceSelectionDialog(
                sentences = getSentence(),
                onDismiss = { showSentencesDialog.value = false },
                addSentenceToDB = { sentence ->
                    sentenceViewModel.insertNewSampleSentenceToDB(
                        sentence = sentence,
                        resourceBookName = state.title,
                        vocabularyId = vocabularyId,
                        onComplete = {}
                    )
                    ("Add sample sentence successful!").toToast(context)
                    showSentencesDialog.value = false
                }
            )
        }

    }
}

/**
 * A composable dialog that allows the user to select a sentence to add to the database.
 *
 * @param sentences A list of sentences to display in the dialog.
 * @param onDismiss A lambda function to be called when the dialog is dismissed.
 * @param addSentenceToDB A lambda function to be called when a sentence is selected.
 */
@Composable
fun SentenceSelectionDialog(
    sentences: List<String>,
    onDismiss: () -> Unit,
    addSentenceToDB: (String) -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Select sample sentence", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            LazyColumn {
                items(sentences) { sentence ->
                    Text(
                        text = sentence,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { addSentenceToDB(sentence) }
                            .padding(vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text(text = "Close")
            }
        }
    )
}