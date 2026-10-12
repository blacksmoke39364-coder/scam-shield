package com.example

import com.example.data.model.RiskLevel
import com.example.data.samples.SampleScams
import com.example.data.service.RuleEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testSensitiveDataRedaction() {
        val raw = "Your verification OTP: 849201 for card 4111 2222 3333 4444. Password: MySecretPassword123"
        val scrubbed = RuleEngine.redactSensitiveData(raw)

        assertFalse(scrubbed.contains("849201"))
        assertFalse(scrubbed.contains("4111 2222 3333 4444"))
        assertTrue(scrubbed.contains("[REDACTED_OTP]"))
        assertTrue(scrubbed.contains("[REDACTED_CARD]"))
    }

    @Test
    fun testUrlInspectionHeuristics() {
        val ipUrl = "http://192.168.1.1/login.php"
        val ipResult = RuleEngine.inspectUrl(ipUrl)
        assertTrue(ipResult.isIpAddress)

        val topUrl = "http://sbi-kyc.top/update"
        val topResult = RuleEngine.inspectUrl(topUrl)
        assertTrue(topResult.suspiciousTld)

        val shortUrl = "https://bit.ly/claim-prize"
        val shortResult = RuleEngine.inspectUrl(shortUrl)
        assertTrue(shortResult.isShortener)
    }

    @Test
    fun testKycBankSmishingDetection() {
        val message = "Dear Customer, your SBI account KYC expired. Account will be blocked within 24 hours. Verify at http://sbi-kyc.top"
        val result = RuleEngine.analyze(message)

        assertEquals(RiskLevel.HIGH, result.riskLevel)
        assertTrue(result.heuristicScore >= 65)
        assertTrue(result.flaggedPhrases.isNotEmpty())
        assertTrue(result.safeNextSteps.isNotEmpty())
        assertTrue(result.plainExplanationEn.isNotBlank())
        assertTrue(result.plainExplanationTe.isNotBlank())
    }

    @Test
    fun testTeluguUrgencyDetection() {
        val teluguMessage = "Mee account block aipothundi. Ippude Rs 500 pampandi."
        val result = RuleEngine.analyze(teluguMessage)

        assertTrue(result.heuristicScore >= 35)
        assertTrue(result.flaggedPhrases.isNotEmpty())
    }

    @Test
    fun testLegitimateMessageEvaluation() {
        val legit = "Your Amazon order ending in 4102 has been dispatched. Track in your Amazon mobile app."
        val result = RuleEngine.analyze(legit)

        assertEquals(RiskLevel.LOW, result.riskLevel)
        assertTrue(result.heuristicScore < 30)
    }

    @Test
    fun testAllFiveBenchmarkSamplesExist() {
        assertEquals(5, SampleScams.ALL_SAMPLES.size)

        // 1. Bank KYC
        assertEquals(RiskLevel.HIGH, SampleScams.SAMPLE_KYC.riskLevel)
        // 2. Fake Prize
        assertEquals(RiskLevel.HIGH, SampleScams.SAMPLE_PRIZE.riskLevel)
        // 3. Delivery Fee
        assertEquals(RiskLevel.HIGH, SampleScams.SAMPLE_DELIVERY.riskLevel)
        // 4. Legitimate
        assertEquals(RiskLevel.LOW, SampleScams.SAMPLE_LEGITIMATE.riskLevel)
        // 5. Uncertain
        assertEquals(RiskLevel.UNCERTAIN, SampleScams.SAMPLE_UNCERTAIN.riskLevel)

        SampleScams.ALL_SAMPLES.forEach { sample ->
            assertNotNull(sample.summary)
            assertNotNull(sample.plainExplanationEn)
            assertNotNull(sample.plainExplanationTe)
            assertTrue(sample.safeNextSteps.isNotEmpty())
            assertTrue(sample.safeNextStepsTe.isNotEmpty())
        }
    }

    @Test
    fun testShareReportFormattingContent() {
        val sample = SampleScams.SAMPLE_KYC
        val formattedEn = "Risk Evaluation: ${sample.riskLevel.getDisplayName("EN")} (Score: ${sample.heuristicScore}/100)"
        assertTrue(formattedEn.contains("High Risk"))
        assertTrue(formattedEn.contains("94/100"))

        val formattedTe = "Risk Evaluation: ${sample.riskLevel.getDisplayName("TE")} (Score: ${sample.heuristicScore}/100)"
        assertTrue(formattedTe.contains("అధిక ప్రమాదం"))
    }

    @Test
    fun testSafeBrowsingAndTeluguSupport() {
        val sample = SampleScams.SAMPLE_KYC
        assertTrue(sample.plainExplanationTe.contains("ఎస్‌బీఐ") || sample.plainExplanationTe.contains("బ్యాంక్"))
        assertTrue(sample.safeNextStepsTe.isNotEmpty())

        val urlAnalysis = sample.urlAnalysis
        assertNotNull(urlAnalysis)
        assertTrue(urlAnalysis!!.securityNote.contains("never means safe") || urlAnalysis.reputationStatus.isNotBlank())
    }
}
