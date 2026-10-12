package com.example.data.model

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    UNCERTAIN;

    fun getDisplayName(language: String = "EN"): String {
        return when (this) {
            LOW -> if (language == "TE") "తక్కువ ప్రమాదం (పరిశీలించండి)" else "Low Risk"
            MEDIUM -> if (language == "TE") "మధ్యస్థ ప్రమాదం (జాగ్రత్త)" else "Medium Risk"
            HIGH -> if (language == "TE") "అధిక ప్రమాదం (మోసం అవకాశం)" else "High Risk"
            UNCERTAIN -> if (language == "TE") "సందేహాస్పదం (ధృవీకరించండి)" else "Uncertain"
        }
    }
}

data class FlaggedPhrase(
    val phrase: String,
    val category: String,
    val whyItMatters: String,
    val safeNextStep: String,
    val whyItMattersTe: String = "",
    val safeNextStepTe: String = ""
)

data class RuleSignal(
    val category: String,
    val matchedKeyword: String,
    val severity: String,
    val explanation: String
)

data class UrlAnalysisResult(
    val url: String,
    val domain: String,
    val isIpAddress: Boolean,
    val hasPunycode: Boolean,
    val isShortener: Boolean,
    val suspiciousTld: Boolean,
    val reputationStatus: String,
    val timestampChecked: String,
    val securityNote: String
)

data class AnalysisResult(
    val id: String,
    val originalInput: String,
    val inputSnippet: String,
    val isUrlOnly: Boolean,
    val riskLevel: RiskLevel,
    val heuristicScore: Int, // 0 to 100 heuristic indicator (never shown as probability)
    val summary: String,
    val plainExplanationEn: String,
    val plainExplanationTe: String,
    val flaggedPhrases: List<FlaggedPhrase>,
    val ruleSignals: List<RuleSignal>,
    val urlAnalysis: UrlAnalysisResult?,
    val safeNextSteps: List<String>,
    val safeNextStepsTe: List<String>,
    val disclaimer: String = "This heuristic risk indicator is based on detected patterns and language traits. Low risk is not a guarantee of legitimacy. When in doubt, verify directly through official channels.",
    val isSample: Boolean = false,
    val sampleLabel: String? = null,
    val analysisEngine: String = "Heuristic Rule Pipeline + Semantic Analysis",
    val timestamp: Long = System.currentTimeMillis()
)

data class ScamPattern(
    val id: String,
    val title: String,
    val targetChannel: String, // SMS, WhatsApp, Email, UPI
    val summary: String,
    val psychologicalLever: String, // Urgency, Fear, Greed, Authority
    val redFlags: List<String>,
    val realWorldExample: String,
    val safeResponse: String,
    val teluguTip: String
)

data class UserSettings(
    val preferredLanguage: String = "EN", // "EN", "TE", "BOTH"
    val isDarkMode: Boolean = true,
    val storeFullMessages: Boolean = false, // Privacy opt-in
    val termsAcceptedVersion: String = "",
    val termsAcceptedTimestamp: Long = 0L
)
