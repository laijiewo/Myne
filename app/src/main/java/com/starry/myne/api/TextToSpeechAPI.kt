package com.starry.myne.api

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import java.util.*

/**
 * A helper class to manage Text-to-Speech functionality
 * @param context The context for accessing system services
 * @param onInitialized Optional callback to indicate whether TTS was successfully initialized
 */
class TextToSpeechHelper(
    private val context: Context,
    private val onInitialized: ((Boolean) -> Unit)? = null // Optional callback
) {
    private var tts: TextToSpeech? = null
    var isInitialized = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                isInitialized = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED

                // Notify about initialization status
                onInitialized?.invoke(isInitialized)

                if (!isInitialized) {
                    Toast.makeText(context, "TTS language not supported.", Toast.LENGTH_SHORT).show()
                }
            } else {
                isInitialized = false
                onInitialized?.invoke(false)
                Toast.makeText(context, "TTS initialization failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Speak the given text
     * @param text The text to convert to speech
     */
    fun speak(text: String) {
        if (!isInitialized) {
            Toast.makeText(context, "TTS is not initialized. Please wait.", Toast.LENGTH_SHORT).show()
            return
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    /**
     * Set the language for speech
     * @param locale The desired Locale (e.g., Locale.US, Locale.CHINESE)
     */
    fun setLanguage(locale: Locale) {
        if (isInitialized) {
            tts?.setLanguage(locale)
        }
    }

    /**
     * Stop the ongoing speech
     */
    fun stop() {
        tts?.stop()
    }

    /**
     * Release resources when done
     */
    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
