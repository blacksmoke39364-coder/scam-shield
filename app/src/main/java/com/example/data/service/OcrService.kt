package com.example.data.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.example.BuildConfig
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

object OcrService {

    private val mlKitIndicRecognizer by lazy {
        TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())
    }

    private val mlKitLatinRecognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Extracts text from an image URI using Google ML Kit and Gemini Vision for Telugu script recognition.
     */
    suspend fun recognizeTextFromUri(context: Context, imageUri: Uri): String = withContext(Dispatchers.IO) {
        val bitmap = loadScaledBitmap(context, imageUri) ?: return@withContext ""

        // 1. Run Google ML Kit Indic & Latin Recognizers
        val mlKitText = recognizeWithMlKit(bitmap)

        // 2. If Gemini API is available, leverage Gemini 3.5 Flash Vision for Native Telugu Script OCR
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val geminiTeluguText = recognizeTeluguWithGeminiVision(bitmap, apiKey)
                if (geminiTeluguText.isNotBlank()) {
                    return@withContext geminiTeluguText
                }
            } catch (_: Exception) {
                // Graceful fallback to ML Kit result
            }
        }

        return@withContext mlKitText
    }

    /**
     * Executes Google ML Kit Text Recognition on the bitmap.
     * Combines both Indic and Latin recognition passes if needed so that
     * URLs, English phone numbers, and Indic/Telugu characters are both captured.
     */
    private suspend fun recognizeWithMlKit(bitmap: Bitmap): String = suspendCancellableCoroutine { continuation ->
        val image = InputImage.fromBitmap(bitmap, 0)

        // First attempt with Indic/Devanagari recognizer
        mlKitIndicRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                val indicText = visionText.text.trim()
                // Also run Latin recognizer to capture any English URLs or codes that Indic might have omitted
                mlKitLatinRecognizer.process(image)
                    .addOnSuccessListener { latinText ->
                        val latin = latinText.text.trim()
                        val combined = when {
                            indicText.isBlank() -> latin
                            latin.isBlank() -> indicText
                            indicText.contains(latin) -> indicText
                            latin.contains(indicText) -> latin
                            else -> "$indicText\n$latin"
                        }
                        continuation.resume(combined)
                    }
                    .addOnFailureListener {
                        continuation.resume(indicText)
                    }
            }
            .addOnFailureListener {
                // Fallback to Latin recognizer
                mlKitLatinRecognizer.process(image)
                    .addOnSuccessListener { latinText ->
                        continuation.resume(latinText.text.trim())
                    }
                    .addOnFailureListener {
                        continuation.resume("")
                    }
            }
    }

    /**
     * Calls Gemini 3.5 Flash Multimodal Vision for native Telugu Script (తెలుగు లిపి) text extraction.
     */
    private fun recognizeTeluguWithGeminiVision(bitmap: Bitmap, apiKey: String): String {
        val base64Image = bitmapToBase64(bitmap)

        val prompt = "Extract all text exactly as shown in this screenshot of a message or notification. Specifically preserve Telugu script (తెలుగు అక్షరాలు), English text, phone numbers, UPI handles, amounts, and web URLs. Do not summarize or explain. Output only the extracted raw text."

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                        put(JSONObject().apply {
                            put("inlineData", JSONObject().apply {
                                put("mimeType", "image/jpeg")
                                put("data", base64Image)
                            })
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.1)
            })
        }

        val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(requestBody)
            .build()

        val response = httpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: return ""
            val jsonRoot = JSONObject(responseBody)
            val candidates = jsonRoot.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            return parts?.optJSONObject(0)?.optString("text")?.trim() ?: ""
        }
        return ""
    }

    private fun loadScaledBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            val maxDimension = 1600
            var inSampleSize = 1
            if (options.outHeight > maxDimension || options.outWidth > maxDimension) {
                val halfHeight = options.outHeight / 2
                val halfWidth = options.outWidth / 2
                while ((halfHeight / inSampleSize) >= maxDimension && (halfWidth / inSampleSize) >= maxDimension) {
                    inSampleSize *= 2
                }
            }

            val scaleOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
            }
            val scaledStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(scaledStream, null, scaleOptions)
            scaledStream?.close()
            bitmap
        } catch (_: Exception) {
            null
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
