package com.starry.myne.ui.screens.sample_sentence.composables

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
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
import com.starry.myne.database.vocabulary.Vocabulary
import com.starry.myne.helpers.getActivity
import com.starry.myne.ui.common.CustomTopAppBar
import com.starry.myne.ui.common.NoBooksAvailable
import com.starry.myne.ui.screens.main.bottomNavPadding
import com.starry.myne.ui.screens.sample_sentence.viewmodels.SampleSentenceViewModel
import com.starry.myne.ui.screens.settings.viewmodels.SettingsViewModel
import com.starry.myne.ui.screens.settings.viewmodels.ThemeMode
import com.starry.myne.ui.screens.vocabularies.viewmodels.VocabulariesViewModel
import com.starry.myne.ui.theme.poppinsFont
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun SpellingDialog(
    vocabulary: Vocabulary,
    onDismiss: () -> Unit,
    onSubmit: (Boolean) -> Unit
) {
    val userInput = remember { mutableStateOf("") }
    val isCorrect = remember { mutableStateOf<Boolean?>(null) }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
            .clickable(onClick = { onDismiss() }, indication = null, interactionSource = remember { MutableInteractionSource() })
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Spell the word:",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextField(
                    value = userInput.value,
                    onValueChange = { userInput.value = it },
                    placeholder = { Text(text = "Type your answer") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = isCorrect.value == false // Highlight text field on error
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "Cancel", color = MaterialTheme.colorScheme.primary)
                    }
                    Button(onClick = {
                        val inputTrimmed = userInput.value.trim()
                        isCorrect.value = inputTrimmed.equals(vocabulary.vocabulary, ignoreCase = true)
                        if (isCorrect.value == true) {
                            onSubmit(true) // Notify correct input
                            onDismiss() // Close the dialog
                            Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT).show()
                        } else {
                            userInput.value = "" // Clear the input field
                        }
                    }) {
                        Text(text = "Submit")
                    }
                }
                if (isCorrect.value == false) {
                    Text(
                        text = "Incorrect, try again.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewDialog() {
    SpellingDialog(
        vocabulary = Vocabulary(
            vocabulary = "Hello",
            srcLang = "safw",
            tarLang = "rewf",
            translation = "nihao"
        ),
        onDismiss = {},
        onSubmit = {}
    )

}