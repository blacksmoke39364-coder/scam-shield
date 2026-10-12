package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AnalysisResult
import com.example.data.model.FlaggedPhrase
import com.example.data.model.RiskLevel
import com.example.data.model.RuleSignal
import com.example.data.model.UrlAnalysisResult
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "analysis_history")
data class AnalysisEntity(
    @PrimaryKey
    val id: String,
    val originalInput: String,
    val inputSnippet: String,
    val isUrlOnly: Boolean,
    val riskLevel: String,
    val heuristicScore: Int,
    val summary: String,
    val plainExplanationEn: String,
    val plainExplanationTe: String,
    val flaggedPhrasesJson: String,
    val ruleSignalsJson: String,
    val urlAnalysisJson: String?,
    val safeNextStepsJson: String,
    val safeNextStepsTeJson: String,
    val isSample: Boolean,
    val sampleLabel: String?,
    val analysisEngine: String,
    val timestamp: Long
) {
    fun toDomainModel(): AnalysisResult {
        val level = try {
            RiskLevel.valueOf(riskLevel)
        } catch (_: Exception) {
            RiskLevel.UNCERTAIN
        }

        val flagged = mutableListOf<FlaggedPhrase>()
        try {
            val arr = JSONArray(flaggedPhrasesJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                flagged.add(
                    FlaggedPhrase(
                        phrase = obj.optString("phrase"),
                        category = obj.optString("category"),
                        whyItMatters = obj.optString("whyItMatters"),
                        safeNextStep = obj.optString("safeNextStep"),
                        whyItMattersTe = obj.optString("whyItMattersTe"),
                        safeNextStepTe = obj.optString("safeNextStepTe")
                    )
                )
            }
        } catch (_: Exception) {}

        val signals = mutableListOf<RuleSignal>()
        try {
            val arr = JSONArray(ruleSignalsJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                signals.add(
                    RuleSignal(
                        category = obj.optString("category"),
                        matchedKeyword = obj.optString("matchedKeyword"),
                        severity = obj.optString("severity"),
                        explanation = obj.optString("explanation")
                    )
                )
            }
        } catch (_: Exception) {}

        var urlRes: UrlAnalysisResult? = null
        if (!urlAnalysisJson.isNullOrBlank()) {
            try {
                val obj = JSONObject(urlAnalysisJson)
                urlRes = UrlAnalysisResult(
                    url = obj.optString("url"),
                    domain = obj.optString("domain"),
                    isIpAddress = obj.optBoolean("isIpAddress"),
                    hasPunycode = obj.optBoolean("hasPunycode"),
                    isShortener = obj.optBoolean("isShortener"),
                    suspiciousTld = obj.optBoolean("suspiciousTld"),
                    reputationStatus = obj.optString("reputationStatus"),
                    timestampChecked = obj.optString("timestampChecked"),
                    securityNote = obj.optString("securityNote")
                )
            } catch (_: Exception) {}
        }

        val steps = mutableListOf<String>()
        try {
            val arr = JSONArray(safeNextStepsJson)
            for (i in 0 until arr.length()) {
                steps.add(arr.getString(i))
            }
        } catch (_: Exception) {}

        val stepsTe = mutableListOf<String>()
        try {
            val arr = JSONArray(safeNextStepsTeJson)
            for (i in 0 until arr.length()) {
                stepsTe.add(arr.getString(i))
            }
        } catch (_: Exception) {}

        return AnalysisResult(
            id = id,
            originalInput = originalInput,
            inputSnippet = inputSnippet,
            isUrlOnly = isUrlOnly,
            riskLevel = level,
            heuristicScore = heuristicScore,
            summary = summary,
            plainExplanationEn = plainExplanationEn,
            plainExplanationTe = plainExplanationTe,
            flaggedPhrases = flagged,
            ruleSignals = signals,
            urlAnalysis = urlRes,
            safeNextSteps = steps,
            safeNextStepsTe = stepsTe,
            isSample = isSample,
            sampleLabel = sampleLabel,
            analysisEngine = analysisEngine,
            timestamp = timestamp
        )
    }

    companion object {
        fun fromDomainModel(domain: AnalysisResult, storeFullMessage: Boolean): AnalysisEntity {
            val storedText = if (storeFullMessage) domain.originalInput else domain.inputSnippet

            val flaggedArr = JSONArray()
            domain.flaggedPhrases.forEach {
                val obj = JSONObject().apply {
                    put("phrase", it.phrase)
                    put("category", it.category)
                    put("whyItMatters", it.whyItMatters)
                    put("safeNextStep", it.safeNextStep)
                    put("whyItMattersTe", it.whyItMattersTe)
                    put("safeNextStepTe", it.safeNextStepTe)
                }
                flaggedArr.put(obj)
            }

            val signalsArr = JSONArray()
            domain.ruleSignals.forEach {
                val obj = JSONObject().apply {
                    put("category", it.category)
                    put("matchedKeyword", it.matchedKeyword)
                    put("severity", it.severity)
                    put("explanation", it.explanation)
                }
                signalsArr.put(obj)
            }

            val urlJson = domain.urlAnalysis?.let {
                JSONObject().apply {
                    put("url", it.url)
                    put("domain", it.domain)
                    put("isIpAddress", it.isIpAddress)
                    put("hasPunycode", it.hasPunycode)
                    put("isShortener", it.isShortener)
                    put("suspiciousTld", it.suspiciousTld)
                    put("reputationStatus", it.reputationStatus)
                    put("timestampChecked", it.timestampChecked)
                    put("securityNote", it.securityNote)
                }.toString()
            }

            val stepsArr = JSONArray(domain.safeNextSteps)
            val stepsTeArr = JSONArray(domain.safeNextStepsTe)

            return AnalysisEntity(
                id = domain.id,
                originalInput = storedText,
                inputSnippet = domain.inputSnippet,
                isUrlOnly = domain.isUrlOnly,
                riskLevel = domain.riskLevel.name,
                heuristicScore = domain.heuristicScore,
                summary = domain.summary,
                plainExplanationEn = domain.plainExplanationEn,
                plainExplanationTe = domain.plainExplanationTe,
                flaggedPhrasesJson = flaggedArr.toString(),
                ruleSignalsJson = signalsArr.toString(),
                urlAnalysisJson = urlJson,
                safeNextStepsJson = stepsArr.toString(),
                safeNextStepsTeJson = stepsTeArr.toString(),
                isSample = domain.isSample,
                sampleLabel = domain.sampleLabel,
                analysisEngine = domain.analysisEngine,
                timestamp = domain.timestamp
            )
        }
    }
}
