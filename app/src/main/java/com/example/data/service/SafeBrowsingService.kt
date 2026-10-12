package com.example.data.service

import com.example.BuildConfig
import com.example.data.model.UrlAnalysisResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object SafeBrowsingService {

    private const val SAFE_BROWSING_URL = "https://safebrowsing.googleapis.com/v4/threatMatches:find"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    /**
     * Checks a URL against the Google Safe Browsing API v4 and combines it with local URL heuristics.
     * Never opens, executes, or redirects to the URL.
     */
    suspend fun checkUrlReputation(rawUrl: String): UrlAnalysisResult = withContext(Dispatchers.IO) {
        val baseInspection = RuleEngine.inspectUrl(rawUrl)
        val apiKey = BuildConfig.GEMINI_API_KEY // Can use Google API Key configured in secrets

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'", Locale.US)
        val timestamp = dateFormat.format(Date())

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext baseInspection.copy(
                reputationStatus = "${baseInspection.reputationStatus} (Google Safe Browsing: Standby / Local Heuristics)",
                timestampChecked = timestamp,
                securityNote = "Provider: Local Threat Engine. Note: 'No detection' never means safe. Phishing links often evade blocklists for the first 24-48 hours."
            )
        }

        val normalizedUrl = if (!rawUrl.startsWith("http://") && !rawUrl.startsWith("https://")) {
            "https://$rawUrl"
        } else rawUrl

        try {
            val requestJson = JSONObject().apply {
                put("client", JSONObject().apply {
                    put("clientId", "com.aistudio.scamshield")
                    put("clientVersion", "1.0.0")
                })
                put("threatInfo", JSONObject().apply {
                    put("threatTypes", JSONArray().apply {
                        put("MALWARE")
                        put("SOCIAL_ENGINEERING")
                        put("UNWANTED_SOFTWARE")
                        put("POTENTIALLY_HARMFUL_APPLICATION")
                    })
                    put("platformTypes", JSONArray().apply {
                        put("ANY_PLATFORM")
                    })
                    put("threatEntryTypes", JSONArray().apply {
                        put("URL")
                    })
                    put("threatEntries", JSONArray().apply {
                        put(JSONObject().apply {
                            put("url", normalizedUrl)
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url("$SAFE_BROWSING_URL?key=$apiKey")
                .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                val rootJson = JSONObject(responseBody)
                val matches = rootJson.optJSONArray("matches")

                if (matches != null && matches.length() > 0) {
                    val firstMatch = matches.getJSONObject(0)
                    val threatType = firstMatch.optString("threatType", "SOCIAL_ENGINEERING")
                    return@withContext baseInspection.copy(
                        reputationStatus = "⚠️ Google Safe Browsing Flagged: Confirmed $threatType Threat",
                        timestampChecked = timestamp,
                        securityNote = "Google Safe Browsing officially classifies this domain as an active malicious portal ($threatType). Do not navigate."
                    )
                } else {
                    return@withContext baseInspection.copy(
                        reputationStatus = "Google Safe Browsing: No active signature detected at $timestamp",
                        timestampChecked = timestamp,
                        securityNote = "Google Safe Browsing returned no match. Warning: 'No detection' never guarantees safety; zero-day fraud domains appear daily."
                    )
                }
            } else {
                return@withContext baseInspection.copy(
                    reputationStatus = "${baseInspection.reputationStatus} (Google Safe Browsing: HTTP ${response.code})",
                    timestampChecked = timestamp,
                    securityNote = "Safe Browsing query completed with status code ${response.code}. Evaluated using local heuristic safeguards."
                )
            }
        } catch (e: Exception) {
            return@withContext baseInspection.copy(
                reputationStatus = "${baseInspection.reputationStatus} (Google Safe Browsing: Offline / Local Heuristics)",
                timestampChecked = timestamp,
                securityNote = "Safe Browsing connection timed out or unreachable (${e.message ?: "Network error"}). Local threat heuristics applied."
            )
        }
    }
}
