package com.starry.myne.api

import okhttp3.*
import com.google.gson.Gson
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Data model representing the response from Baidu translation API.
 * @property from The language of the original text.
 * @property to The language of the translated text.
 * @property trans_result The list of translation results.
 */
data class TranslateResult(
    val from: String,
    val to: String,
    val trans_result: List<TransResult>
)

/**
 * Data model representing individual translation results.
 * @property src The original text.
 * @property dst The translated text.
 */
data class TransResult(
    val src: String, // Original text
    val dst: String  // Translated text
)

/**
 * Initiates the translation process with an optional delay.
 * This function simply calls the `translate` function.
 *
 * @param text The text to be translated.
 * @param targetLanguage The target language for translation (e.g., "zh" for Chinese).
 * @param callback Callback function to handle the translated text or an error.
 */
fun translateWithDelay(
    text: String,
    targetLanguage: String,
    callback: (String?) -> Unit
) {
    translate(text, targetLanguage, callback)
}

/**
 * Performs the actual translation of the given text to the target language using Baidu Translate API.
 *
 * @param text The text to be translated.
 * @param targetLanguage The target language for translation (e.g., "zh" for Chinese).
 * @param callback Callback function to handle the translated text or an error.
 */
fun translate(
    text: String,
    targetLanguage: String,
    callback: (String?) -> Unit
) {
    val appId = "20241128002213598"
    val key = "oNiOkKiqlTvBRv1vP9vf"
    val fromLanguage = "en"
    val salt = System.currentTimeMillis().toString()
    val sign = toMD5("$appId$text$salt$key")

    val url = "https://fanyi-api.baidu.com/api/trans/vip/translate" +
            "?appid=$appId" +
            "&q=${text.urlEncode()}" +
            "&from=$fromLanguage" +
            "&to=$targetLanguage" +
            "&salt=$salt" +
            "&sign=$sign"

    val client = OkHttpClient()
    val request = Request.Builder().url(url).get().build()

    // Asynchronously requests the translation.
    client.newCall(request).enqueue(object : Callback {
        /**
         * Handles the case where the HTTP request fails.
         *
         * @param call The call that was attempted.
         * @param e The exception thrown due to failure.
         */
        override fun onFailure(call: Call, e: IOException) {
            println("Translation request failed: ${e.message}")
            callback(null)
        }

        /**
         * Handles the case where the HTTP request receives a response.
         *
         * @param call The call that was attempted.
         * @param response The response from the server.
         */
        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                try {
                    val responseBody = response.body?.string()
                    val result = Gson().fromJson(responseBody, TranslateResult::class.java)

                    val translation = result.trans_result?.firstOrNull()?.dst
                    if (translation != null) {
                        println("Translation result: $translation")
                        callback(translation) // Returns the translation result.
                    } else {
                        println("Translation result is empty or invalid: $responseBody")
                        callback(null)
                    }
                } catch (e: Exception) {
                    println("Parsing error: ${e.message}")
                    callback(null)
                }
            } else {
                println("API response failed: ${response.code} - ${response.message}")
                callback(null)
            }
        }
    })
}

/**
 * Generates an MD5 hash of the given string.
 *
 * @param string The string to be hashed.
 * @return The MD5 hash of the string.
 */
fun toMD5(string: String):String {
    try {
        // Get the MD5 digest instance.
        val instance: MessageDigest = MessageDigest.getInstance("MD5")
        // Encrypt the string and return the byte array.
        val digest: ByteArray = instance.digest(string.toByteArray())
        var sb = StringBuffer()
        for (b in digest) {
            // Get the lower 8 bits.
            val i = b.toInt() and 0xff
            // Convert the integer to a hexadecimal string.
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                hexString = "0$hexString" // Pads with a leading zero if necessary.
            }
            sb.append(hexString)
        }
        return sb.toString()

    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }

    return ""
}

/**
 * URL encodes the given string.
 *
 * @return The URL-encoded version of the string.
 */
fun String.urlEncode(): String = java.net.URLEncoder.encode(this, "UTF-8")

