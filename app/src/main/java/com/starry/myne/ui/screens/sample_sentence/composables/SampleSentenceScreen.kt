package com.starry.myne.ui.screens.sample_sentence.composables

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
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.starry.myne.MainActivity
import com.starry.myne.R
import com.starry.myne.database.sampleSentence.SampleSentence
import com.starry.myne.helpers.getActivity
import com.starry.myne.ui.common.CustomTopAppBar
import com.starry.myne.ui.common.NoBooksAvailable
import com.starry.myne.ui.screens.main.bottomNavPadding
import com.starry.myne.ui.screens.sample_sentence.viewmodels.SampleSentenceViewModel
import com.starry.myne.ui.screens.settings.viewmodels.SettingsViewModel
import com.starry.myne.ui.screens.settings.viewmodels.ThemeMode
import com.starry.myne.ui.theme.poppinsFont
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

/**
 * Main composable function for the Sample Sentence Screen.
 * This screen displays all sample sentences related to a specific vocabulary ID.
 * It includes a top bar and a list of sentences managed through a ViewModel.
 *
 * @param vocabularyId The ID of the vocabulary whose sample sentences are displayed.
 */
@Composable
fun SampleSentenceScreen(
    vocabularyId: Int
) {
    val viewModel: SampleSentenceViewModel = hiltViewModel()

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
        },
    ) { paddingValues ->
        SampleSentenceContents(
            viewModel = viewModel,
            lazyListState = lazyListState,
            paddingValues = paddingValues,
            vocabularyId = vocabularyId
        )
    }

}

/**
 * Composable function to display the contents of the Sample Sentence Screen.
 * This includes a list of sentences or a placeholder message if no sentences are found.
 *
 * @param viewModel ViewModel managing the data for sample sentences.
 * @param lazyListState State object for controlling the scroll position of the LazyColumn.
 * @param paddingValues Padding values applied to the content.
 * @param vocabularyId The ID of the vocabulary whose sentences are being displayed.
 */
@Composable
private fun SampleSentenceContents(
    viewModel: SampleSentenceViewModel,
    lazyListState: LazyListState,
    paddingValues: PaddingValues,
    vocabularyId: Int
) {
    val context = LocalContext.current
    val settingsVm = (context.getActivity() as MainActivity).settingsViewModel
    val sentences = viewModel.getAllSampleSentence(vocabularyId).observeAsState(listOf()).value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
    ) {
        if (sentences.isEmpty()) {
            NoBooksAvailable(text = stringResource(id = R.string.empty_word_books))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                state = lazyListState
            ) {
                items(
                    count = sentences.size,
                    key = { i -> sentences[i].sentenceId!! }
                ) { i ->
                    val item = sentences[i]
                    SampleSentenceLazyItem(
                        modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null),
                        sentence = item,
                        viewModel = viewModel,
                        settingsVm = settingsVm
                    )
                }
            }

        }
    }
}

/**
 * Composable function to display a single sentence item in a swipeable card.
 * The card supports swipe actions for deleting the sentence and displays sentence details.
 *
 * @param modifier Modifier applied to the item for styling and animations.
 * @param sentence The sentence data to display.
 * @param viewModel ViewModel to handle actions like deleting the sentence.
 * @param settingsVm ViewModel for managing app settings like theme mode.
 */
@Composable
private fun SampleSentenceLazyItem(
    modifier: Modifier,
    sentence: SampleSentence,
    viewModel: SampleSentenceViewModel,
    settingsVm: SettingsViewModel
) {
    val deleteAction = SwipeAction(icon = painterResource(
        id = if (settingsVm.getCurrentTheme() == ThemeMode.Dark) R.drawable.ic_share else R.drawable.ic_share_white
    ), background = MaterialTheme.colorScheme.primary, onSwipe = {
        viewModel.deleteSampleSentenceFromDB(sentence)
    })

    SwipeableActionsBox(
        modifier = modifier.padding(vertical = 4.dp),
        endActions = listOf(deleteAction),
        swipeThreshold = 85.dp
    ) {
        SentenceCard (
            sentence = sentence.sentence,
            source = sentence.resource,
            onDeleteClick = { viewModel.deleteSampleSentenceFromDB(sentence) })
    }
}

/**
 * Composable function to display the details of a sample sentence in a card format.
 * This includes:
 * - The sample sentence text.
 * - The source information for the sentence.
 * - A button for deleting the sentence.
 *
 * @param sentence The sample sentence text to display.
 * @param source The source or reference of the sentence (e.g., book name).
 * @param onDeleteClick Callback triggered when the delete button is clicked.
 */
@Composable
private fun SentenceCard(
    sentence: String,
    source: String,
    onDeleteClick: () -> Unit
) {
    val commonTextStyle = TextStyle(
        fontStyle = MaterialTheme.typography.headlineMedium.fontStyle,
        fontSize = 18.sp,
        fontFamily = poppinsFont,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
    val sampleSentenceTextStyle = commonTextStyle.copy(
        fontWeight = FontWeight.Bold, // 让样本句子文本加粗显示
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f) // 颜色稍深些
    )
    val sourceBookTextStyle = commonTextStyle.copy(
        fontWeight = FontWeight.Normal,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) // 颜色稍浅些
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row (modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Sample Sentence: $sentence",
                        style = sampleSentenceTextStyle,
                        maxLines = 16,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.offset(y = (-8).dp)) {
                    Text(
                        text = "Source Book: $source",
                        style = sourceBookTextStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.offset(y = (-4).dp)) {
                    SentenceCardButton (text = stringResource(id = R.string.word_book_delete_button),
                        icon = Icons.Outlined.Delete,
                        onClick = { onDeleteClick() })
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

/**
 * Composable function for creating a button used inside a sentence card.
 * The button includes an icon and a text label and executes an action when clicked.
 *
 * @param text The label text displayed on the button.
 * @param icon The icon displayed next to the text.
 * @param onClick Callback triggered when the button is clicked.
 */
@Composable
private fun SentenceCardButton(
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
 * Preview function to visualize how the `SentenceCard` composable will look in isolation.
 * Provides multiple sample cards with mock data for testing the design and layout.
 */
@Preview
@Composable
fun PreviewSentenceCard() {
    Column {
        SentenceCard(
            sentence = "bkjeabkj sjabfbkjwan kjanfjknwqkj njkawnkjnfjwankj nkjn kjn kjwnj nafj nkgj nakwn knw",
            source = "Oliver Twist"
        ){}
        SentenceCard(
            sentence = "bkjeabkj sjabfbkjwan kjanfjknwqkj njkawnkjnfjwankj nkjn kjn kjwnj nafj nkgj nakwn knw",
            source = "Oliver Twist"
        ){}
        SentenceCard(
            sentence = "bkjeabkj sjabfbkjwan kjanfjknwqkj njkawnkjnfjwankj nkjn kjn kjwnj nafj nkgj nakwn knw",
            source = "Oliver Twist"
        ){}
        SentenceCard(
            sentence = "bkjeabkj sjabfbkjwan kjanfjknwqkj njkawnkjnfjwankj nkjn kjn kjwnj nafj nkgj nakwn knw",
            source = "Oliver Twist"
        ){}
    }

}