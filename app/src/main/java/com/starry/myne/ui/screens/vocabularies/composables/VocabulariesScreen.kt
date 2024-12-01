package com.starry.myne.ui.screens.vocabularies.composables

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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.starry.myne.R
import com.starry.myne.database.vocabulary.Vocabulary
import com.starry.myne.ui.common.CustomTopAppBar
import com.starry.myne.ui.common.NoBooksAvailable
import com.starry.myne.ui.navigation.Screens
import com.starry.myne.ui.screens.main.bottomNavPadding
import com.starry.myne.ui.screens.sample_sentence.viewmodels.SampleSentenceViewModel
import com.starry.myne.ui.screens.vocabularies.viewmodels.VocabulariesViewModel
import com.starry.myne.ui.theme.poppinsFont
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

/**
 * Composable function that displays the main Vocabularies screen.
 * This screen includes:
 * - A top bar with a header.
 * - A floating action button for importing new vocabularies.
 * - A list of vocabularies that can be scrolled and managed.
 * - A Snackbar to display success messages.
 *
 * @param navController Navigation controller used to navigate between screens.
 */
@Composable
fun VocabulariesScreen(navController: NavController) {
    val viewModel: VocabulariesViewModel = hiltViewModel()
    val sentenceViewModel: SampleSentenceViewModel = hiltViewModel()
    val snackBarHostState = remember { SnackbarHostState() }
    val lazyListState = rememberLazyListState()

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
        }
    ) { paddingValues ->
        VocabularyContents(
            viewModel = viewModel,
            sampleSentenceViewModel = sentenceViewModel,
            lazyListState = lazyListState,
            paddingValues = paddingValues,
            navController = navController,
        )
    }
}

/**
 * Composable function that displays the main content of the vocabulary list.
 * - Displays a LazyColumn containing all vocabulary items.
 * - Shows a placeholder message if the list is empty.
 *
 * @param viewModel The ViewModel managing vocabulary data.
 * @param sampleSentenceViewModel The ViewModel managing sample sentence actions.
 * @param lazyListState The LazyListState to control the scroll position.
 * @param paddingValues Padding values for the content.
 * @param navController The navigation controller for moving between screens.
 */
@Composable
private fun VocabularyContents(
    viewModel: VocabulariesViewModel,
    sampleSentenceViewModel: SampleSentenceViewModel,
    lazyListState: LazyListState,
    paddingValues: PaddingValues,
    navController: NavController
) {
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
                        sampleSentenceViewModel = sampleSentenceViewModel,
                        navController = navController
                    )
                }
            }

        }
    }
}

/**
 * Composable function to display a single vocabulary item as a swipeable card.
 * This card contains:
 * - Vocabulary word and its translation.
 * - Source and target languages.
 * - Swipe actions for deleting the vocabulary.
 *
 * @param modifier Modifier applied to the item for styling or animations.
 * @param vocabulary The vocabulary item to display.
 * @param viewModel The ViewModel to manage vocabulary actions like deletion.
 * @param sampleSentenceViewModel The ViewModel to manage related sample sentences.
 * @param settingsVm The ViewModel managing application settings.
 * @param navController Navigation controller for handling navigation events.
 */
@Composable
private fun VocabularyLazyItem(
    modifier: Modifier,
    vocabulary: Vocabulary,
    viewModel: VocabulariesViewModel,
    sampleSentenceViewModel: SampleSentenceViewModel,
    navController: NavController
) {
    // Swipe actions to delete word.
    val deleteAction = SwipeAction(icon = painterResource(R.drawable.ic_delete),
        background = MaterialTheme.colorScheme.primary,
        onSwipe = {
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
                navController.navigate(
                    Screens.SampleSentenceScreen.withVocabularyId(
                        vocabulary.vocabularyId.toString()
                    )
                )
            },
            onDeleteClick = {
                viewModel.deleteVocabularyFromDB(vocabulary)
                sampleSentenceViewModel.deleteAllFromDB(vocabulary.vocabularyId!!)
            })
    }
}

/**
 * Composable function representing the card displaying vocabulary information.
 * This card includes:
 * - Vocabulary word.
 * - Source and target languages.
 * - Translation.
 * - Buttons for review and delete actions.
 *
 * @param vocabulary The vocabulary word or phrase.
 * @param srcLang The source language of the vocabulary.
 * @param tarLANG The target language of the vocabulary.
 * @param translation The translation of the vocabulary.
 * @param onReviewClick Callback for when the review button is clicked.
 * @param onDeleteClick Callback for when the delete button is clicked.
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
                    VocabularyCardButton(text = stringResource(id = R.string.word_book_review_button),
                        icon = ImageVector.vectorResource(id = R.drawable.ic_library_read),
                        onClick = { onReviewClick() })

                    Spacer(modifier = Modifier.width(10.dp))

                    VocabularyCardButton(text = stringResource(id = R.string.word_book_delete_button),
                        icon = Icons.Outlined.Delete,
                        onClick = { onDeleteClick() })
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

/**
 * Composable function to create a button used within the vocabulary card.
 * This button displays an icon and a text label, and executes an action on click.
 *
 * @param text The label text for the button.
 * @param icon The icon to display next to the label.
 * @param onClick Callback triggered when the button is clicked.
 */
@Composable
private fun VocabularyCardButton(
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
