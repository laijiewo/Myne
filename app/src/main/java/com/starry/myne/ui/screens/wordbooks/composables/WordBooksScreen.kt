package com.starry.myne.ui.screens.wordbooks.composables

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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.starry.myne.R
import com.starry.myne.database.vocabulary.Vocabulary
import com.starry.myne.helpers.Constants
import com.starry.myne.helpers.book.BookLanguage
import com.starry.myne.helpers.isScrollingUp
import com.starry.myne.helpers.weakHapticFeedback
import com.starry.myne.ui.common.BookCardPreview
import com.starry.myne.ui.common.CustomTopAppBar
import com.starry.myne.ui.common.NoBooksAvailable
import com.starry.myne.ui.screens.main.bottomNavPadding
import com.starry.myne.ui.screens.wordbooks.viewmodels.WordBooksViewModel
import com.starry.myne.ui.theme.poppinsFont
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeableActionsBox

@Composable
fun WordBooksScreen(navController: NavController) {
    val view = LocalView.current
    val context = LocalContext.current
    val viewModel: WordBooksViewModel = hiltViewModel()

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
                        // 当点击浮动按钮时，显示导入表单
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
        LibraryContents(
            viewModel = viewModel,
            lazyListState = lazyListState,
            paddingValues = paddingValues
        )

        if (showCreateNewVocabularyList.value) {
            VocabularyImportScreen(
                onSaveClick = { word, sourceLang, targetLang, translation ->
                    // 调用 ViewModel 方法保存单词到数据库
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

@Composable
private fun LibraryContents(
    viewModel: WordBooksViewModel,
    lazyListState: LazyListState,
    paddingValues: PaddingValues
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
                    )
                }
            }

        }
    }
}

@Composable
private fun VocabularyLazyItem(
    modifier: Modifier,
    vocabulary: Vocabulary
) {
    val openDeleteDialog = remember { mutableStateOf(false) }

    SwipeableActionsBox(
        modifier = modifier.padding(vertical = 4.dp),
        swipeThreshold = 85.dp
    ) {
        VocabularyCard(vocabulary = vocabulary.vocabulary,
            srcLang = vocabulary.srcLang,
            tarLANG = vocabulary.tarLang,
            translation = vocabulary.translation,
            onReviewClick = {
            },
            onDeleteClick = { openDeleteDialog.value = true })
    }
}

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


@ExperimentalMaterial3Api
@Composable
@Preview
fun WordsBookScreenPreview() {
    VocabularyCard(
        vocabulary = "Hello",
        srcLang = "en",
        tarLANG = "cn",
        translation = "你好",
        onReviewClick = {},
        onDeleteClick = {})
}

@Composable
fun VocabularyImportScreen(
    onSaveClick: (String, String, String, String) -> Unit, // 保存按钮回调
    onCancelClick: () -> Unit // 取消按钮回调
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

            // 输入单词
            TextField(
                value = word.value,
                onValueChange = { word.value = it },
                label = { Text("Word") },
                modifier = Modifier.width(300.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 输入源语言选择框
            LanguageDropdownMenu(
                label = "Source Language",
                selectedLanguage = sourceLanguage.value,
                onLanguageSelected = { selected -> sourceLanguage.value = selected },
                availableLanguages = availableLanguages
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 输入目标语言选择框
            LanguageDropdownMenu(
                label = "Target Language",
                selectedLanguage = targetLanguage.value,
                onLanguageSelected = { selected -> targetLanguage.value = selected },
                availableLanguages = availableLanguages
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 输入翻译结果
            TextField(
                value = translation.value,
                onValueChange = { translation.value = it },
                label = { Text("Translation") },
                modifier = Modifier.width(300.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 保存和取消按钮
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
                            // 这里可以提示用户填写完整
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