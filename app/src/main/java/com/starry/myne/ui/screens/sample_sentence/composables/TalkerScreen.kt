package com.starry.myne.ui.screens.sample_sentence.composables

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

import com.starry.myne.R
import android.Manifest
import android.widget.Toast.makeText
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.res.colorResource
import com.iflytek.cloud.SpeechEvaluator
import com.starry.myne.api.models.Bridge
import com.starry.myne.database.vocabulary.Vocabulary

@Composable
fun TalkerScreen(
    onBackPressed: () -> Unit,
    vocabulary: Vocabulary,
    bridge: Bridge
) {
    val context = LocalContext.current
    val hasPermission = remember { mutableStateOf(false) }

    val text = remember { mutableStateOf("Tap the button below to start recording") }
    val isRecording = remember { mutableStateOf(false) }

    // Permission launcher
    RequestAudioPermission(
        context = context,
        onPermissionGranted = {
            hasPermission.value = true
        },
        onPermissionDenied = {
            hasPermission.value = false
            // Show a dialog or toast explaining why the permission is needed
        }
    )

    if (hasPermission.value) {
        // Show recording UI
        makeText(context, "Permission granted! Ready to record.", Toast.LENGTH_SHORT).show()
    } else {
        // Show fallback UI or message
        makeText(context, "Permission is required to record audio.", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onBackPressed() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "Pronunciation Validation",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text.value,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            IconButton(
                onClick = {
                    if (isRecording.value) {
                        bridge.stopRecord()
                        isRecording.value = false
                        text.value = "evaluating..."
                        bridge.setOnResultCallback { result ->
                            text.value = result
                        }
                    } else {
                        text.value = "recording..."
                        isRecording.value = true
                        println(1)
                        bridge.startRecord(
                            content = vocabulary.vocabulary,
                            timeout = "5000",
                            vad_bos = "5000",
                            vad_eos = "1800"
                        )
                        println(2)
                    }
                },
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        if (isRecording.value) Color.Red else colorResource(id = R.color.purple_500),
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_talker),
                    contentDescription = "Mic",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun RequestAudioPermission(
    context: Context,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val permission = Manifest.permission.RECORD_AUDIO

    // Check if permission is already granted
    val isPermissionGranted = remember {
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    // Launcher to request permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        if (!isPermissionGranted) {
            permissionLauncher.launch(permission)
        } else {
            onPermissionGranted()
        }
    }
}
