package com.starry.myne.ui.screens.sample_sentence.composables

import android.widget.Toast
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
import androidx.compose.material.icons.outlined.GTranslate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.iflytek.cloud.SpeechEvaluator
import com.starry.myne.R
import com.starry.myne.api.TextToSpeechHelper
import com.starry.myne.api.models.Bridge
import com.starry.myne.api.translateWithDelay
import com.starry.myne.database.sampleSentence.SampleSentence
import com.starry.myne.database.vocabulary.Vocabulary
import com.starry.myne.helpers.toToast
import com.starry.myne.ui.common.CustomTopAppBar
import com.starry.myne.ui.common.NoBooksAvailable
import com.starry.myne.ui.screens.main.bottomNavPadding
import com.starry.myne.ui.screens.sample_sentence.viewmodels.SampleSentenceViewModel
import com.starry.myne.ui.screens.vocabularies.viewmodels.VocabulariesViewModel
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
    val context = LocalContext.current

    // TTS API
    var isTTSReady by remember { mutableStateOf(false) }
    val ttsHelper = remember {
        TextToSpeechHelper(context) { initialized ->
            isTTSReady = initialized
        }
    }
    // Cleanup TextToSpeech when this screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            ttsHelper.release()
        }
    }

    // IFLY API
    val speechEvaluator = SpeechEvaluator.createEvaluator(context, null)
    val bridge = Bridge(context, speechEvaluator)

    val viewModel: SampleSentenceViewModel = hiltViewModel()
    val vocabularyViewModel: VocabulariesViewModel = hiltViewModel()

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
            vocabulariesViewModel = vocabularyViewModel,
            lazyListState = lazyListState,
            paddingValues = paddingValues,
            vocabularyId = vocabularyId,
            textToSpeechHelper = ttsHelper,
            bridge = bridge
        )
    }

}

/**
 * Composable function to display the contents of the Sample Sentence Screen.
 * This includes a list of sentences or a placeholder message if no sentences are found.
 *
 * @param viewModel ViewModel managing the data for sample sentences.
 * @param vocabulariesViewModel ViewModel managing vocabulary data.
 * @param lazyListState State object for controlling the scroll position of the LazyColumn.
 * @param paddingValues Padding values applied to the content.
 * @param vocabularyId The ID of the vocabulary whose sentences are being displayed.
 * @param textToSpeechHelper Helper object for text-to-speech functionality.
 * @param bridge Bridge object for handling TTS or other operations related to speech.
 */
@Composable
private fun SampleSentenceContents(
    viewModel: SampleSentenceViewModel,
    vocabulariesViewModel: VocabulariesViewModel,
    lazyListState: LazyListState,
    paddingValues: PaddingValues,
    vocabularyId: Int,
    textToSpeechHelper: TextToSpeechHelper,
    bridge: Bridge
) {
    val sentences = viewModel.getAllSampleSentence(vocabularyId).observeAsState(listOf()).value
    val vocabulary =
        vocabulariesViewModel.getVocabulary(vocabularyId).collectAsState(initial = null).value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
    ) {
        if (sentences.isEmpty() || vocabulary == null) {
            if (vocabulary != null) {
                VocabularyCard(
                    vocabulary = vocabulary,
                    textToSpeechHelper = textToSpeechHelper,
                    bridge = bridge
                )
            } else {
                NoBooksAvailable(text = stringResource(id = R.string.empty_word_books))
            }
        } else {
            VocabularyCard(
                vocabulary = vocabulary,
                textToSpeechHelper = textToSpeechHelper,
                bridge = bridge
            )
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
                        textToSpeechHelper = textToSpeechHelper
                    )
                }
            }

        }
    }
}

/**
 * Composable function to display a vocabulary card.
 * The card includes the vocabulary word, its translation, and options for text-to-speech functionality.
 *
 * @param vocabulary The vocabulary object containing the word and its translation.
 * @param textToSpeechHelper Helper object for text-to-speech functionality.
 * @param bridge Bridge object for handling TTS or other operations related to speech.
 */
@Composable
private fun VocabularyCard(
    vocabulary: Vocabulary,
    textToSpeechHelper: TextToSpeechHelper,
    bridge: Bridge
) {
    val showDialog = remember { mutableStateOf(false) }
    val showTalkerDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer // Unique color for vocabulary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp) // More rounded for distinction
    ) {
        Column(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp)
        ) {
            Row {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = vocabulary.vocabulary,
                        fontStyle = MaterialTheme.typography.headlineMedium.fontStyle,
                        fontSize = 28.sp,
                        fontFamily = poppinsFont,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = vocabulary.translation,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        maxLines = 1,
                        fontStyle = MaterialTheme.typography.bodySmall.fontStyle,
                        fontFamily = poppinsFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                IconButton(
                    onClick = {
                        if (textToSpeechHelper.isInitialized) {
                            textToSpeechHelper.speak(vocabulary.vocabulary)
                        } else {
                            Toast.makeText(context, "TTS is not ready yet!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                    ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_speaker),
                        contentDescription = "speaker_icon"
                    )
                }
            }


            Row(
                modifier = Modifier
                    .offset(y = (-8).dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = vocabulary.srcLang,
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
                        text = vocabulary.tarLang,
                        fontFamily = poppinsFont,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Light,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
                Row {
                    IconButton(
                        onClick = {
                            if (showDialog.value) {
                                showDialog.value = false
                            }
                            showTalkerDialog.value = true
                        },

                        ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_talker),
                            contentDescription = "talker_icon"
                        )
                    }
                    IconButton(
                        onClick = {
                            if (showTalkerDialog.value) {
                                showTalkerDialog.value = false
                            }
                            showDialog.value = true
                        }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pencil),
                            contentDescription = "writer_icon"
                        )
                    }
                }
            }

        }
    }

    // Show the spelling dialog if the state is true
    if (showDialog.value) {
        SpellingDialog(
            vocabulary = vocabulary,
            onDismiss = { showDialog.value = false }, // Close the dialog
            onSubmit = { isCorrect ->
                if (isCorrect) {
                    println("User spelled correctly!")
                } else {
                    println("Incorrect spelling attempt.")
                }
            }
        )
    }

    if (showTalkerDialog.value) {
        TalkerScreen(
           onBackPressed = { showTalkerDialog.value = false },
            vocabulary = vocabulary,
            bridge = bridge
        )
    }
}


/**
 * Composable function to display a single sentence item in a swipeable card.
 * The card supports swipe actions for deleting the sentence and displays sentence details.
 *
 * @param modifier Modifier applied to the item for styling and animations.
 * @param sentence The sentence data to display.
 * @param viewModel ViewModel to handle actions like deleting the sentence.
 * @param textToSpeechHelper Helper for text-to-speech functionality.
 */
@Composable
private fun SampleSentenceLazyItem(
    modifier: Modifier,
    sentence: SampleSentence,
    viewModel: SampleSentenceViewModel,
    textToSpeechHelper: TextToSpeechHelper
) {
    val deleteAction = SwipeAction(
        onSwipe = {
            viewModel.deleteSampleSentenceFromDB(sentence)},
        icon = painterResource(R.drawable.ic_delete),
        background = MaterialTheme.colorScheme.primary,
        )

    SwipeableActionsBox(
        modifier = modifier.padding(vertical = 4.dp),
        endActions = listOf(deleteAction),
        swipeThreshold = 85.dp
    ) {
        SentenceCard(
            sentence = sentence.sentence,
            source = sentence.resource,
            textToSpeechHelper = textToSpeechHelper,
            onDeleteClick = { viewModel.deleteSampleSentenceFromDB(sentence) })
    }
}

/**
 * Composable function to display the details of a sample sentence in a card format.
 * This includes:
 * - The sample sentence text.
 * - The source information for the sentence (e.g., book name).
 * - A button for deleting the sentence.
 * - A button for text-to-speech functionality.
 *
 * @param sentence The sample sentence text to display.
 * @param source The source or reference of the sentence (e.g., book name).
 * @param textToSpeechHelper Helper for text-to-speech functionality.
 * @param onDeleteClick Callback triggered when the delete button is clicked.
 */
@Composable
private fun SentenceCard(
    sentence: String,
    source: String,
    textToSpeechHelper: TextToSpeechHelper,
    onDeleteClick: () -> Unit
) {
    val context = LocalContext.current
    val commonTextStyle = TextStyle(
        fontStyle = MaterialTheme.typography.headlineMedium.fontStyle,
        fontSize = 18.sp,
        fontFamily = poppinsFont,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
    val sampleSentenceTextStyle = commonTextStyle.copy(
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
    )
    val sourceBookTextStyle = commonTextStyle.copy(
        fontWeight = FontWeight.Normal,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )
    val sentenceTranslation = remember { mutableStateOf("") }

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
            Column(
                modifier = Modifier.padding(8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = sentence,
                        style = sampleSentenceTextStyle,
                        maxLines = 30,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            sentenceTranslation.value = "translating...."
                            translateWithDelay(text = sentence, targetLanguage = "zh") { result ->
                                if (result != null) {
                                    sentenceTranslation.value = result
                                } else {
                                    println("translate failed")
                                }
                            }
                        },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.GTranslate,
                            contentDescription = "translate"
                        )
                    }
                }
                if (sentenceTranslation.value != "") {
                    Row(modifier = Modifier.offset(y = (-8).dp)) {
                        Text(
                            text = "Translation: ${sentenceTranslation.value}",
                            style = sourceBookTextStyle,
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(modifier = Modifier.offset(y = (-8).dp)) {
                    Text(
                        text = "Source Book: $source",
                        style = sourceBookTextStyle,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.offset(y = (-4).dp)) {
                    SentenceCardButton(text = stringResource(id = R.string.word_book_delete_button),
                        icon = Icons.Outlined.Delete,
                        onClick = { onDeleteClick() })
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
            Column {
                IconButton(
                    onClick = {
                        if (textToSpeechHelper.isInitialized) {
                            textToSpeechHelper.speak(sentence)
                        } else {
                            Toast.makeText(context, "TTS is not ready yet!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_speaker),
                        contentDescription = "speaker_icon"
                    )
                }
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
