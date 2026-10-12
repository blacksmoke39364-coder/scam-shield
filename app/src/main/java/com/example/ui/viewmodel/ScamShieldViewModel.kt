package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AnalysisEntity
import com.example.data.local.AppDatabase
import com.example.data.local.PreferencesManager
import com.example.data.model.AnalysisResult
import com.example.data.model.RiskLevel
import com.example.data.model.UserSettings
import com.example.data.samples.SampleScams
import com.example.data.service.GeminiAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen {
    DASHBOARD,
    RESULT_DETAIL,
    HISTORY,
    LEARN_MODE,
    SETTINGS_PROFILE,
    TERMS_PRIVACY
}

data class UiState(
    val currentScreen: AppScreen = AppScreen.DASHBOARD,
    val inputText: String = "",
    val isAnalyzing: Boolean = false,
    val currentResult: AnalysisResult? = null,
    val selectedLanguage: String = "EN", // "EN" or "TE"
    val historySearchQuery: String = "",
    val historyFilterRisk: RiskLevel? = null,
    val toastMessage: String? = null,
    val feedbackSubmitted: Boolean = false
)

class ScamShieldViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val dao = db.analysisDao()
    private val prefs = PreferencesManager(application)

    val userSettings: StateFlow<UserSettings> = prefs.settings

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val rawHistory: StateFlow<List<AnalysisEntity>> = dao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalAnalysesCount: StateFlow<Int> = dao.getTotalCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val highRiskCount: StateFlow<Int> = dao.getHighRiskCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val filteredHistory: StateFlow<List<AnalysisEntity>> = combine(
        rawHistory,
        _uiState
    ) { history, state ->
        history.filter { entity ->
            val matchesSearch = if (state.historySearchQuery.isBlank()) true else {
                entity.originalInput.contains(state.historySearchQuery, ignoreCase = true) ||
                        entity.summary.contains(state.historySearchQuery, ignoreCase = true)
            }
            val matchesRisk = if (state.historyFilterRisk == null) true else {
                entity.riskLevel.equals(state.historyFilterRisk.name, ignoreCase = true)
            }
            matchesSearch && matchesRisk
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Set default language from preferences
        val initialLang = prefs.settings.value.preferredLanguage
        if (initialLang == "TE") {
            _uiState.value = _uiState.value.copy(selectedLanguage = "TE")
        }
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }

    fun setInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun processScreenshot(context: Context, uri: android.net.Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAnalyzing = true)
            try {
                showToast("Scanning screenshot for Telugu and English text...")
                val extractedText = com.example.data.service.OcrService.recognizeTextFromUri(context, uri)
                if (extractedText.isNotBlank()) {
                    setInputText(extractedText)
                    showToast("Text recognized from screenshot. Tap Analyze to verify threat.")
                } else {
                    showToast("No readable text detected in this image. Try another screenshot.")
                }
            } catch (e: Exception) {
                showToast("OCR Error: ${e.message}")
            } finally {
                _uiState.value = _uiState.value.copy(isAnalyzing = false)
            }
        }
    }

    fun toggleLanguage(lang: String) {
        _uiState.value = _uiState.value.copy(selectedLanguage = lang)
    }

    fun setHistorySearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(historySearchQuery = query)
    }

    fun setHistoryRiskFilter(risk: RiskLevel?) {
        _uiState.value = _uiState.value.copy(historyFilterRisk = risk)
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }

    fun showToast(message: String) {
        _uiState.value = _uiState.value.copy(toastMessage = message)
    }

    fun loadSample(sample: AnalysisResult) {
        _uiState.value = _uiState.value.copy(
            currentResult = sample,
            currentScreen = AppScreen.RESULT_DETAIL
        )
    }

    fun viewResult(result: AnalysisResult) {
        _uiState.value = _uiState.value.copy(
            currentResult = result,
            currentScreen = AppScreen.RESULT_DETAIL
        )
    }

    fun analyzeMessage(customText: String? = null) {
        val textToAnalyze = (customText ?: _uiState.value.inputText).trim()
        if (textToAnalyze.isBlank()) {
            showToast("Please enter a message, URL, or select a sample first.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAnalyzing = true)
            try {
                val result = GeminiAnalyzer.analyzeMessage(textToAnalyze)

                // Save to Room database (respecting privacy opt-in setting)
                val storeFull = prefs.settings.value.storeFullMessages
                dao.insert(AnalysisEntity.fromDomainModel(result, storeFull))

                _uiState.value = _uiState.value.copy(
                    isAnalyzing = false,
                    currentResult = result,
                    currentScreen = AppScreen.RESULT_DETAIL,
                    inputText = ""
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isAnalyzing = false)
                showToast("Analysis error: ${e.message ?: "Unable to complete analysis"}")
            }
        }
    }

    fun deleteHistoryItem(id: String) {
        viewModelScope.launch {
            dao.deleteById(id)
            showToast("Analysis removed from history.")
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            dao.clearAll()
            showToast("All analysis history cleared.")
        }
    }

    fun updateLanguagePreference(lang: String) {
        prefs.updateLanguage(lang)
        _uiState.value = _uiState.value.copy(selectedLanguage = if (lang == "TE") "TE" else "EN")
        showToast("Preferred language updated to $lang.")
    }

    fun updateDarkModePreference(isDark: Boolean) {
        prefs.updateDarkMode(isDark)
    }

    fun updateStoreFullMessagesPreference(optIn: Boolean) {
        prefs.updateStoreFullMessages(optIn)
        showToast(if (optIn) "Full message text storage enabled." else "Privacy mode active: Only summaries stored.")
    }

    fun acceptTerms() {
        prefs.acceptTerms("1.0.0")
        showToast("Terms of service accepted.")
    }

    fun submitFeedback(resultId: String, reason: String) {
        _uiState.value = _uiState.value.copy(feedbackSubmitted = true)
        showToast("Feedback submitted. Thank you for improving detection accuracy.")
    }

    fun formatReportForSharing(result: AnalysisResult, lang: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        val formattedDate = dateFormat.format(Date(result.timestamp))
        val isTelugu = lang == "TE"

        val sb = StringBuilder()
        sb.append("🛡️ ScamShield AI Analysis Report\n")
        sb.append("Time: $formattedDate\n")
        sb.append("Risk Evaluation: ${result.riskLevel.getDisplayName(lang)} (Score: ${result.heuristicScore}/100)\n")
        sb.append("--------------------------------\n")
        sb.append(if (isTelugu) "వివరణ:\n" else "Explanation:\n")
        sb.append(if (isTelugu) result.plainExplanationTe else result.plainExplanationEn)
        sb.append("\n\n")

        if (result.flaggedPhrases.isNotEmpty()) {
            sb.append(if (isTelugu) "గుర్తించిన హెచ్చరికలు:\n" else "Flagged Evidence:\n")
            result.flaggedPhrases.forEachIndexed { i, f ->
                sb.append("${i + 1}. \"${f.phrase}\" [${f.category}]\n")
                sb.append("   - ${if (isTelugu) f.whyItMattersTe else f.whyItMatters}\n")
            }
            sb.append("\n")
        }

        if (result.urlAnalysis != null) {
            sb.append("URL Check: ${result.urlAnalysis.domain}\n")
            sb.append("Status: ${result.urlAnalysis.reputationStatus}\n\n")
        }

        sb.append(if (isTelugu) "రక్షణ చర్యలు:\n" else "Safe Next Steps:\n")
        val steps = if (isTelugu) result.safeNextStepsTe else result.safeNextSteps
        steps.forEach { step ->
            sb.append("• $step\n")
        }
        sb.append("\nNotice: Low risk is not a guarantee. Suspicious? Verify independently.")
        return sb.toString()
    }

    fun copyReportToClipboard(context: Context, result: AnalysisResult, lang: String) {
        val report = formatReportForSharing(result, lang)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
        clipboard?.setPrimaryClip(android.content.ClipData.newPlainText("ScamShield Report", report))
        showToast(if (lang == "TE") "రిపోర్ట్ క్లిప్‌బోర్డ్‌కు కాపీ చేయబడింది." else "Report copied to clipboard.")
    }

    fun shareReport(context: Context, result: AnalysisResult, lang: String) {
        val report = formatReportForSharing(result, lang)
        val title = if (lang == "TE") "మోసం హెచ్చరికను షేర్ చేయండి" else "Share Scam Alert via..."
        val success = com.example.ui.util.ShareHelper.shareText(
            context = context,
            content = report,
            subject = "⚠️ Scam Alert: Verified by ScamShield AI",
            chooserTitle = title
        )
        if (success) {
            showToast(if (lang == "TE") "షేర్ విండో ప్రారంభించబడింది మరియు రిపోర్ట్ కాపీ చేయబడింది." else "Share menu opened & report copied to clipboard.")
        } else {
            showToast(if (lang == "TE") "రిపోర్ట్ క్లిప్‌బోర్డ్‌కు కాపీ చేయబడింది (బాహ్య యాప్ లేదు)." else "Report copied to clipboard (no external app found).")
        }
    }

    fun shareReportToWhatsApp(context: Context, result: AnalysisResult, lang: String) {
        val report = formatReportForSharing(result, lang)
        val title = if (lang == "TE") "వాట్సాప్ ద్వారా షేర్ చేయండి" else "Share via WhatsApp"
        val success = com.example.ui.util.ShareHelper.shareToWhatsApp(context, report, fallbackChooserTitle = title)
        if (success) {
            showToast(if (lang == "TE") "వాట్సాప్ షేర్ ప్రారంభించబడింది." else "WhatsApp share launched.")
        } else {
            copyReportToClipboard(context, result, lang)
        }
    }

    fun shareReportToSms(context: Context, result: AnalysisResult, lang: String) {
        val report = formatReportForSharing(result, lang)
        val success = com.example.ui.util.ShareHelper.shareToSms(context, report)
        if (success) {
            showToast(if (lang == "TE") "ఎస్‌ఎంఎస్ షేర్ ప్రారంభించబడింది." else "SMS share launched.")
        } else {
            copyReportToClipboard(context, result, lang)
        }
    }

    fun exportHistoryToCsv(context: Context) {
        viewModelScope.launch {
            val history = rawHistory.value
            if (history.isEmpty()) {
                val sampleExport = "ID,Timestamp,Risk_Level,Heuristic_Score,Is_Url_Only,Summary,Original_Snippet\n" +
                        SampleScams.ALL_SAMPLES.joinToString("\n") { s ->
                            "\"${s.id}\",\"Sample Benchmark\",\"${s.riskLevel}\",${s.heuristicScore},${s.isUrlOnly},\"${s.summary.replace("\"", "\"\"")}\",\"${s.inputSnippet.replace("\"", "\"\"")}\""
                        }
                com.example.ui.util.ShareHelper.shareText(
                    context = context,
                    content = "ScamShield Benchmark Scenarios CSV:\n\n$sampleExport",
                    subject = "ScamShield AI Benchmark Scenarios Export",
                    chooserTitle = "Share Analysis Records"
                )
                return@launch
            }

            try {
                val csvHeader = "ID,Timestamp,Risk_Level,Heuristic_Score,Is_Url_Only,Summary,Original_Snippet\n"
                val rows = history.joinToString("\n") { entity ->
                    val cleanSnippet = entity.inputSnippet.replace("\"", "\"\"").replace("\n", " ")
                    val cleanSummary = entity.summary.replace("\"", "\"\"").replace("\n", " ")
                    val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(entity.timestamp))
                    "\"${entity.id}\",\"$date\",\"${entity.riskLevel}\",${entity.heuristicScore},${entity.isUrlOnly},\"$cleanSummary\",\"$cleanSnippet\""
                }

                val csvContent = csvHeader + rows
                val file = File(context.cacheDir, "ScamShield_History_Export.csv")
                file.writeText(csvContent)

                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                val success = com.example.ui.util.ShareHelper.shareFile(
                    context = context,
                    fileUri = uri,
                    mimeType = "text/csv",
                    subject = "ScamShield AI History Export",
                    chooserTitle = "Export Analysis History CSV"
                )
                if (!success) {
                    throw IllegalStateException("File share fallback needed")
                }
            } catch (_: Exception) {
                // Fallback to text share
                val summaryText = "ScamShield Analysis History (${history.size} records):\n\n" + history.joinToString("\n---\n") {
                    "[${it.riskLevel}] Score: ${it.heuristicScore}/100 - ${it.summary}\nText: ${it.inputSnippet}"
                }
                com.example.ui.util.ShareHelper.shareText(
                    context = context,
                    content = summaryText,
                    subject = "ScamShield Analysis History",
                    chooserTitle = "Export History via..."
                )
            }
        }
    }
}
