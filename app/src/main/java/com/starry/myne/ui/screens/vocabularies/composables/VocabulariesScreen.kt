package com.starry.myne.ui.screens.vocabularies.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.starry.myne.BuildConfig
import com.starry.myne.MainActivity
import com.starry.myne.R
import com.starry.myne.database.vocabulary.Vocabulary
import com.starry.myne.helpers.book.BookLanguage
import com.starry.myne.helpers.book.BookUtils
import com.starry.myne.helpers.getActivity
import com.starry.myne.helpers.isScrollingUp
import com.starry.myne.helpers.weakHapticFeedback
import com.starry.myne.ui.common.CustomTopAppBar
import com.starry.myne.ui.common.NoBooksAvailable
import com.starry.myne.ui.screens.library.viewmodels.LibraryViewModel
import com.starry.myne.ui.screens.main.bottomNavPadding
import com.starry.myne.ui.screens.settings.viewmodels.SettingsViewModel
import com.starry.myne.ui.screens.settings.viewmodels.ThemeMode
import com.starry.myne.ui.screens.vocabularies.viewmodels.VocabulariesViewModel
import com.starry.myne.ui.theme.poppinsFont
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.io.File

/**
 * Composable function that displays the Vocabularies screen with a list of vocabularies and the option
 * to add new vocabularies. This screen includes a floating action button to import new vocabularies
 * and manages visibility and actions for vocabulary items.
 *
 * @param navController Navigation controller used to navigate between screens.
 */
@Composable
fun VocabulariesScreen(navController: NavController) {
    val view = LocalView.current
    val context = LocalContext.current
    val viewModel: VocabulariesViewModel = hiltViewModel()

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val showCreateNewVocabularyList = remember { mutableStateOf(false) }


    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = bottomNavPadding),
        topBar = {
            CustomTopAppBar(
                headerText = stringResource(id = R.string.word_book_header),
                iconRes = R.drawable.ic_nav_word_books
            )
        },
        floatingActionButton = {
            val density = LocalDensity.current
            AnimatedVisibility(
                visible = !showCreateNewVocabularyList.value && lazyListState.isScrollingUp(),
                enter = slideInVertically {
                    with(density) { 40.dp.roundToPx() }
                } + fadeIn(),
                exit = fadeOut(
                    animationSpec = keyframes {
                        this.durationMillis = 120
                    }
                )
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        view.weakHapticFeedback()
                        showCreateNewVocabularyList.value = true
                    },
                    modifier = Modifier
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = stringResource(id = R.string.import_button_desc),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.import_button_text),
                        fontWeight = FontWeight.Medium,
                        fontFamily = poppinsFont,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                }
            }
        }
    ) { paddingValues ->
        VocabularyContents(
            viewModel = viewModel,
            lazyListState = lazyListState,
            paddingValues = paddingValues
        )

        if (showCreateNewVocabularyList.value) {
            VocabularyImportScreen(
                onSaveClick = { word, sourceLang, targetLang, translation ->
                    viewModel.insertNewVocabularyToDB(
                        context = word,
                        srcLang = sourceLang,
                        tarLang = targetLang,
                        translation = translation,
                        onComplete = {
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar(
                                    message = "Vocabulary saved successfully",
                                    actionLabel = context.getString(R.string.ok),
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                    showCreateNewVocabularyList.value = false
                },
                onCancelClick = {
                    showCreateNewVocabularyList.value = false
                }
            )
        }

    }

}

/**
 * Composable function that displays the contents of the vocabulary list.
 * It shows a list of vocabularies or a message when no vocabularies are available.
 *
 * @param viewModel The ViewModel that manages the vocabulary data.
 * @param lazyListState The state object to control the scroll position of the LazyColumn.
 * @param paddingValues Padding values applied to the content.
 */
@Composable
private fun VocabularyContents(
    viewModel: VocabulariesViewModel,
    lazyListState: LazyListState,
    paddingValues: PaddingValues
) {
    val context = LocalContext.current
    val settingsVm = (context.getActivity() as MainActivity).settingsViewModel
    val vocabularies = viewModel.allVocabulary.observeAsState(listOf()).value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
    ) {
        if (vocabularies.isEmpty()) {
            NoBooksAvailable(text = stringResource(id = R.string.empty_word_books))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                state = lazyListState
            ) {
                items(
                    count = vocabularies.size,
                    key = { i -> vocabularies[i].vocabularyId!! }
                ) { i ->
                    val item = vocabularies[i]
                    VocabularyLazyItem(
                        modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null),
                        vocabulary = item,
                        viewModel = viewModel,
                        settingsVm = settingsVm
                    )
                }
            }

        }
    }
}

/**
 * Composable function that displays a single vocabulary item in a swipeable card.
 * The card contains the vocabulary, source and target languages, and translation.
 * It supports swipe actions to delete the vocabulary.
 *
 * @param modifier Modifier applied to the item.
 * @param vocabulary The vocabulary data to display.
 * @param viewModel The ViewModel to manage actions like deleting a vocabulary.
 */
@Composable
private fun VocabularyLazyItem(
    modifier: Modifier,
    vocabulary: Vocabulary,
    viewModel: VocabulariesViewModel,
    settingsVm: SettingsViewModel
) {
    val openDeleteDialog = remember { mutableStateOf(false) }

    // Swipe actions to delete word.
    val deleteAction = SwipeAction(icon = painterResource(
        id = if (settingsVm.getCurrentTheme() == ThemeMode.Dark) R.drawable.ic_share else R.drawable.ic_share_white
    ), background = MaterialTheme.colorScheme.primary, onSwipe = {
        viewModel.deleteVocabularyFromDB(vocabulary)
    })

    SwipeableActionsBox(
        modifier = modifier.padding(vertical = 4.dp),
        endActions = listOf(deleteAction),
        swipeThreshold = 85.dp
    ) {
        VocabularyCard(vocabulary = vocabulary.vocabulary,
            srcLang = vocabulary.srcLang,
            tarLANG = vocabulary.tarLang,
            translation = vocabulary.translation,
            onReviewClick = {
            },
            onDeleteClick = { viewModel.deleteVocabularyFromDB(vocabulary) })
    }
}

/**
 * Composable function that represents the card displaying vocabulary information.
 * It shows the vocabulary, source and target languages, and translation, with buttons for review and delete.
 *
 * @param vocabulary The word or phrase to display.
 * @param srcLang The source language of the vocabulary.
 * @param tarLANG The target language of the vocabulary.
 * @param translation The translation of the vocabulary.
 * @param onReviewClick Action triggered when the review button is clicked.
 * @param onDeleteClick Action triggered when the delete button is clicked.
 */
@Composable
private fun VocabularyCard(
    vocabulary: String,
    srcLang: String,
    tarLANG: String,
    translation: String,
    onReviewClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
        ), shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row (modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = vocabulary,
                        fontStyle = MaterialTheme.typography.headlineMedium.fontStyle,
                        fontSize = 18.sp,
                        fontFamily = poppinsFont,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = translation,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        maxLines = 1,
                        fontStyle = MaterialTheme.typography.bodySmall.fontStyle,
                        fontFamily = poppinsFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }


                Row(modifier = Modifier.offset(y = (-8).dp)) {
                    Text(
                        text = srcLang,
                        fontFamily = poppinsFont,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Light,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    VerticalDivider(
                        modifier = Modifier
                            .height(17.5.dp)
                            .width(1.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = tarLANG,
                        fontFamily = poppinsFont,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Light,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                Row(modifier = Modifier.offset(y = (-4).dp)) {
                    LibraryCardButton(text = stringResource(id = R.string.word_book_review_button),
                        icon = ImageVector.vectorResource(id = R.drawable.ic_library_read),
                        onClick = { onReviewClick() })

                    Spacer(modifier = Modifier.width(10.dp))

                    LibraryCardButton(text = stringResource(id = R.string.word_book_delete_button),
                        icon = Icons.Outlined.Delete,
                        onClick = { onDeleteClick() })
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

@Composable
private fun LibraryCardButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Favorite Icon",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Composable function to display a screen for importing a new vocabulary.
 * It allows the user to input a word, select source and target languages, and provide a translation.
 * It provides "Save" and "Cancel" buttons for saving the vocabulary or dismissing the screen.
 *
 * @param onSaveClick Lambda function triggered when the "Save" button is clicked. Takes the word, source language, target language, and translation as parameters.
 * @param onCancelClick Lambda function triggered when the "Cancel" button is clicked.
 */
@Composable
fun VocabularyImportScreen(
    onSaveClick: (String, String, String, String) -> Unit,
    onCancelClick: () -> Unit
) {
    val word = remember { mutableStateOf("") }
    val sourceLanguage = remember { mutableStateOf<BookLanguage?>(null) }
    val targetLanguage = remember { mutableStateOf<BookLanguage?>(null) }
    val translation = remember { mutableStateOf("") }

    val availableLanguages = BookLanguage.getAllLanguages().drop(1)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .size(400.dp)
                .width(420.dp)
                .padding(16.dp)
                .background(color = Color.DarkGray),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = word.value,
                onValueChange = { word.value = it },
                label = { Text("Word") },
                modifier = Modifier.width(300.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            LanguageDropdownMenu(
                label = "Source Language",
                selectedLanguage = sourceLanguage.value,
                onLanguageSelected = { selected -> sourceLanguage.value = selected },
                availableLanguages = availableLanguages
            )

            Spacer(modifier = Modifier.height(16.dp))

            LanguageDropdownMenu(
                label = "Target Language",
                selectedLanguage = targetLanguage.value,
                onLanguageSelected = { selected -> targetLanguage.value = selected },
                availableLanguages = availableLanguages
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = translation.value,
                onValueChange = { translation.value = it },
                label = { Text("Translation") },
                modifier = Modifier.width(300.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (word.value.isNotEmpty() && sourceLanguage.value != null && targetLanguage.value != null && translation.value.isNotEmpty()) {
                            onSaveClick(
                                word.value,
                                sourceLanguage.value!!.name,
                                targetLanguage.value!!.name,
                                translation.value
                            )
                        } else {
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }

                Spacer(modifier = Modifier.width(6.dp))

                Button(
                    onClick = { onCancelClick() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

/**
 * Composable function to create a dropdown menu for selecting a language.
 * It displays a TextField for the selected language and expands a dropdown menu
 * when clicked, allowing the user to choose from a list of available languages.
 *
 * @param label The label for the TextField.
 * @param selectedLanguage The currently selected language.
 * @param onLanguageSelected Lambda function triggered when a language is selected.
 * @param availableLanguages A list of languages that can be selected from the dropdown.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageDropdownMenu(
    label: String,
    selectedLanguage: BookLanguage?,
    onLanguageSelected: (BookLanguage) -> Unit,
    availableLanguages: List<BookLanguage>
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedText = selectedLanguage?.name ?: "Select $label"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .width(300.dp),
            singleLine = true
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableLanguages.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language.name) },
                    onClick = {
                        onLanguageSelected(language)
                        expanded = false
                    }
                )
            }
        }
    }
}