package com.example.data.service

import com.example.data.model.AnalysisResult
import com.example.data.model.FlaggedPhrase
import com.example.data.model.RiskLevel
import com.example.data.model.RuleSignal
import com.example.data.model.UrlAnalysisResult
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.regex.Pattern

object RuleEngine {

    private val URL_PATTERN = Pattern.compile(
        "https?://[a-zA-Z0-9.-]+(?:\\.[a-zA-Z]{2,})+(?::\\d+)?(?:/[^\\s]*)?",
        Pattern.CASE_INSENSITIVE
    )

    private val SUSPICIOUS_TLDS = setOf(
        "top", "xyz", "club", "work", "vip", "help", "link", "buzz", "cc", "tk", "ml", "ga", "cf", "gq", "click", "rest", "fit", "live", "icu"
    )

    private val URL_SHORTENERS = setOf(
        "bit.ly", "tinyurl.com", "is.gd", "t.co", "cutt.ly", "rb.gy", "shorturl.at", "ow.ly", "buff.ly", "goo.gl"
    )

    private val SPOOF_KEYWORDS = listOf(
        "sbi", "hdfc", "icici", "axis", "pnb", "bob", "rbi", "paytm", "phonepe", "gpay", "googlepay",
        "indiapost", "speedpost", "electricity", "bsnl", "jio", "airtel", "netflix", "amazon", "kyc",
        "pan-card", "aadhaar", "incometax", "challan", "parivahan"
    )

    /**
     * Redacts sensitive user credentials (OTPs, PINs, 16-digit card numbers)
     * before any downstream processing or external transmission.
     */
    fun redactSensitiveData(input: String): String {
        var scrubbed = input
        // 16-digit card numbers
        scrubbed = scrubbed.replace(Regex("\\b(?:\\d[ -]*?){13,16}\\b"), "[REDACTED_CARD]")
        // Explicit OTP mentions with 4-8 digits
        scrubbed = scrubbed.replace(Regex("(?i)(?:otp|code|pin|password)[:\\s]+(\\d{4,8})"), "OTP: [REDACTED_OTP]")
        // Passwords following credentials words
        scrubbed = scrubbed.replace(Regex("(?i)(?:password|pwd)[:\\s]+([^\\s]+)"), "password: [REDACTED_PWD]")
        return scrubbed
    }

    /**
     * Inspects URL structure and traits without making any network requests or opening links.
     */
    fun inspectUrl(rawUrl: String): UrlAnalysisResult {
        val normalized = if (!rawUrl.startsWith("http://") && !rawUrl.startsWith("https://")) {
            "http://$rawUrl"
        } else rawUrl

        var domain = ""
        var isIp = false
        var hasPunycode = false
        var isShortener = false
        var suspiciousTld = false

        try {
            val uri = URI(normalized)
            val host = uri.host?.lowercase(Locale.ROOT) ?: ""
            domain = host

            // Check IP address format
            isIp = host.matches(Regex("^(\\d{1,3}\\.){3}\\d{1,3}$"))

            // Check Punycode (homograph attack)
            hasPunycode = host.contains("xn--")

            // Check URL shortener
            isShortener = URL_SHORTENERS.any { host.contains(it) }

            // Check Suspicious TLD
            val parts = host.split(".")
            if (parts.size >= 2) {
                val tld = parts.last()
                if (SUSPICIOUS_TLDS.contains(tld)) {
                    suspiciousTld = true
                }
            }
        } catch (_: Exception) {
            domain = rawUrl.take(30)
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm 'UTC'", Locale.US)
        val timestamp = dateFormat.format(Date())

        val status = when {
            isIp -> "High Risk: Host points directly to raw IP address instead of domain name"
            hasPunycode -> "High Risk: Punycode detected (homograph character spoofing technique)"
            suspiciousTld -> "Elevated Risk: Host utilizes unverified / high-abuse top-level domain"
            isShortener -> "Caution: Shortened URL obscures actual destination destination endpoint"
            !normalized.startsWith("https://") -> "Medium Risk: Insecure unencrypted HTTP protocol"
            else -> "Reputation Check: No public blacklist signature triggered at this time"
        }

        val note = "Inspection completed locally. Note: 'No detection' never means safe. Attackers cycle new domains daily."

        return UrlAnalysisResult(
            url = rawUrl,
            domain = domain,
            isIpAddress = isIp,
            hasPunycode = hasPunycode,
            isShortener = isShortener,
            suspiciousTld = suspiciousTld,
            reputationStatus = status,
            timestampChecked = timestamp,
            securityNote = note
        )
    }

    /**
     * Executes local rule-based analysis on the text or URL.
     */
    fun analyze(rawInput: String): AnalysisResult {
        val scrubbed = redactSensitiveData(rawInput.trim())
        val isUrlOnly = rawInput.trim().matches(Regex("^https?://[^\\s]+$")) || rawInput.trim().matches(Regex("^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/[^\\s]*)?$"))

        val detectedUrls = mutableListOf<String>()
        val urlMatcher = URL_PATTERN.matcher(rawInput)
        while (urlMatcher.find()) {
            detectedUrls.add(urlMatcher.group())
        }

        val urlAnalysisResult = if (detectedUrls.isNotEmpty()) {
            inspectUrl(detectedUrls.first())
        } else if (isUrlOnly) {
            inspectUrl(rawInput.trim())
        } else null

        val ruleSignals = mutableListOf<RuleSignal>()
        val flaggedPhrases = mutableListOf<FlaggedPhrase>()
        var baseScore = 0

        val lower = scrubbed.lowercase(Locale.ROOT)

        // 1. URGENCY & PANIC SIGNALS (English + Telugu + Transliterated)
        val urgencyKeywords = listOf(
            "within 24 hours" to "24-hour ultimatum",
            "tonight" to "Immediate evening cutoff",
            "immediately" to "Pressure to act right away",
            "urgent action" to "Manufactured urgency",
            "will be blocked" to "Threat of service disconnection",
            "disconnected" to "Utility/service cutoff threat",
            "account suspended" to "Suspension fear lever",
            "legal action" to "Threat of legal repercussions",
            "arrest warrant" to "Police/arrest intimidation",
            "digital arrest" to "Fake law enforcement threat",
            "నిలిపివేయబడుతుంది" to "Telugu: Service disconnection threat",
            "బ్లాక్ చేయబడుతుంది" to "Telugu: Account freeze threat",
            "వెంటనే" to "Telugu: Urgency (Immediately)",
            "ఈ రాత్రి" to "Telugu: Tonight cutoff ultimatum",
            "ippude" to "Urgency in Telugu (Right now)",
            "aipotundi" to "Imminent loss in Telugu",
            "block aipothundi" to "Account freeze threat in Telugu"
        )
        for ((keyword, label) in urgencyKeywords) {
            if (lower.contains(keyword)) {
                baseScore += 18
                ruleSignals.add(RuleSignal("Manufactured Urgency", keyword, "High", "Coercive time limit ($label) designed to prevent independent verification."))
                flaggedPhrases.add(
                    FlaggedPhrase(
                        phrase = extractSurroundingPhrase(scrubbed, keyword),
                        category = "Manufactured Urgency",
                        whyItMatters = "Scammers enforce tight deadlines so you panic and act before confirming with your official service provider.",
                        safeNextStep = "Pause and do not rush. Legitimate institutions give formal advance notice before taking account actions.",
                        whyItMattersTe = "ఆలోచించే సమయం ఇవ్వకుండా భయపెట్టి వెంటనే తప్పు నిర్ణయం తీసుకునేలా మోసగాళ్లు చేస్తారు.",
                        safeNextStepTe = "కంగారు పడకండి. ముందుగా అధికారిక కస్టమర్ కేర్‌ను సంప్రదించి నిర్ధారించుకోండి."
                    )
                )
                break
            }
        }

        // 2. CREDENTIAL & OTP HARVESTING
        val credentialKeywords = listOf(
            "share otp" to "Direct OTP request",
            "send pin" to "PIN request",
            "enter password" to "Password request",
            "update kyc" to "KYC update lure",
            "verify kyc" to "KYC verification prompt",
            "pan update" to "PAN card update request",
            "aadhaar update" to "Aadhaar credential request",
            "cvv" to "Card security code demand",
            "ఓటీపీ" to "Telugu: OTP request",
            "పాస్‌వర్డ్" to "Telugu: Password request",
            "ఖాతా" to "Telugu: Bank account update",
            "కేవైసీ" to "Telugu: KYC update lure",
            "otp cheppandi" to "Telugu: Asking to share OTP",
            "pin enter cheyandi" to "Telugu: Prompt to enter PIN"
        )
        for ((keyword, label) in credentialKeywords) {
            if (lower.contains(keyword)) {
                baseScore += 25
                ruleSignals.add(RuleSignal("Credential Harvesting", keyword, "High", "Attempts to capture security credentials ($label)."))
                flaggedPhrases.add(
                    FlaggedPhrase(
                        phrase = extractSurroundingPhrase(scrubbed, keyword),
                        category = "Credential Harvesting",
                        whyItMatters = "No bank, telecom company, or government officer will ever ask you to disclose OTPs, PINs, or password links via message.",
                        safeNextStep = "Never share OTPs or click unverified links to verify KYC. Keep your passwords confidential.",
                        whyItMattersTe = "ఏ అధికారిక సంస్థ లేదా బ్యాంక్ కూడా ఓటీపీ లేదా పాస్‌వర్డ్ అడగదు.",
                        safeNextStepTe = "ఎవరికీ మీ ఓటీపీ లేదా పిన్ నంబర్ చెప్పకండి."
                    )
                )
                break
            }
        }

        // 3. ADVANCE PAYMENT & FINANCIAL DEMANDS
        val paymentKeywords = listOf(
            "pay rs" to "Fee demand",
            "send rs" to "Direct money request",
            "registration fee" to "Advance registration charge",
            "processing fee" to "Upfront processing payment",
            "customs fee" to "Border/customs fee request",
            "redelivery fee" to "Courier delivery charge",
            "googlepay" to "Consumer UPI demand",
            "phonepe" to "Direct payment app transfer",
            "paytm" to "Wallet transfer demand",
            "చెల్లించండి" to "Telugu: Payment demand",
            "డబ్బు పంపండి" to "Telugu: Send money demand",
            "రుసుము" to "Telugu: Fee charge demand",
            "విద్యుత్ బిల్లు" to "Telugu: Electricity bill lure",
            "కరెంట్ బిల్లు" to "Telugu: Power bill lure",
            "dabbulu pampandi" to "Telugu: Request to send money",
            "pampandi" to "Telugu: Send money demand",
            "rs " to "Currency payment amount"
        )
        for ((keyword, label) in paymentKeywords) {
            if (lower.contains(keyword)) {
                baseScore += 22
                ruleSignals.add(RuleSignal("Financial Demand", keyword, "High", "Unsolicited payment request ($label) through peer-to-peer or untrusted channels."))
                flaggedPhrases.add(
                    FlaggedPhrase(
                        phrase = extractSurroundingPhrase(scrubbed, keyword),
                        category = "Financial Demand",
                        whyItMatters = "Demanding advance fees or UPI transfers for prizes, delivery, or refunds is standard advance-fee fraud.",
                        safeNextStep = "Do not transfer money. Real refunds and legitimate prizes never require advance payment.",
                        whyItMattersTe = "బహుమతులు లేదా కొరియర్ కోసం ముందుగా డబ్బు లేదా యూపీఐ బదిలీ అడగడం మోసం.",
                        safeNextStepTe = "ఎలాంటి డబ్బు పంపవద్దు."
                    )
                )
                break
            }
        }

        // 4. FAKE PRIZES / LOTTERIES / EARNING PROMISES
        val prizeKeywords = listOf(
            "congratulations" to "Prize celebration hook",
            "you won" to "Unsolicited winning claim",
            "lucky draw" to "Lottery/lucky draw lure",
            "cash prize" to "Cash incentive hook",
            "part time job" to "Work-from-home task scam",
            "telegram task" to "Task rating investment fraud",
            "daily income" to "Unrealistic returns promise",
            "బహుమతి" to "Telugu: Gift / Prize hook",
            "గెలుచుకున్నారు" to "Telugu: Won prize claim",
            "లాటరీ" to "Telugu: Lottery claim",
            "geluchukunnaru" to "Telugu: Won prize claim"
        )
        for ((keyword, label) in prizeKeywords) {
            if (lower.contains(keyword)) {
                baseScore += 20
                ruleSignals.add(RuleSignal("Prize / Task Lure", keyword, "High", "Unsolicited reward or unrealistic earnings ($label)."))
                flaggedPhrases.add(
                    FlaggedPhrase(
                        phrase = extractSurroundingPhrase(scrubbed, keyword),
                        category = "Unsolicited Reward Trap",
                        whyItMatters = "You cannot win a lottery you never entered. High daily pay for easy online tasks is universally fraudulent.",
                        safeNextStep = "Disregard unsolicited winning claims and part-time rating task offers.",
                        whyItMattersTe = "మీరు పాల్గొనని పోటీలలో గెలవడం అసాధ్యం. ఇది ప్రజలను ఆకర్షించే ఎత్తుగడ.",
                        safeNextStepTe = "ఇలాంటి సందేశాలను నమ్మకండి, విస్మరించండి."
                    )
                )
                break
            }
        }

        // 5. IMPERSONATION (Banks, Govt, Utilities, Law Enforcement)
        val impersonationKeywords = listOf(
            "sbi" to "State Bank of India",
            "hdfc" to "HDFC Bank",
            "icici" to "ICICI Bank",
            "electricity" to "State Power Distribution",
            "indiapost" to "India Post postal department",
            "speedpost" to "SpeedPost courier service",
            "cbi" to "Central Bureau of Investigation",
            "cyber crime" to "Police Cyber Crime Unit",
            "trai" to "Telecom Regulatory Authority",
            "income tax" to "Income Tax Department"
        )
        for ((keyword, org) in impersonationKeywords) {
            if (lower.contains(keyword)) {
                baseScore += 12
                ruleSignals.add(RuleSignal("Authority Impersonation", keyword, "Medium", "Claims identity of major public institution or utility ($org)."))
                break
            }
        }

        // 6. MALICIOUS SOFTWARE / REMOTE ACCESS APK
        val appInstallKeywords = listOf(
            ".apk" to "Untrusted Android Package (.apk)",
            "install anydesk" to "AnyDesk screen sharing remote control",
            "install teamviewer" to "TeamViewer remote access tool",
            "quicksupport" to "Screen capture utility",
            "download app from" to "Sideloading app prompt"
        )
        for ((keyword, label) in appInstallKeywords) {
            if (lower.contains(keyword)) {
                baseScore += 30
                ruleSignals.add(RuleSignal("Malicious App Sideloading", keyword, "High", "Prompts installing dangerous remote control software or sideloaded APK ($label)."))
                flaggedPhrases.add(
                    FlaggedPhrase(
                        phrase = extractSurroundingPhrase(scrubbed, keyword),
                        category = "Malicious App / APK Trap",
                        whyItMatters = "Installing APK files from web links or remote tools allows fraudsters to view your phone screen, read incoming OTPs, and control your device.",
                        safeNextStep = "Never install APK files sent in messages. Only install apps from the official Google Play Store.",
                        whyItMattersTe = "ఈ యాప్స్ మీ ఫోన్ స్క్రీన్‌ను మోసగాళ్లకు చూపిస్తాయి మరియు ఓటీపీలను దొంగిలిస్తాయి.",
                        safeNextStepTe = "ఎట్టి పరిస్థితుల్లోనూ ఈ ఏపీకే లేదా యాప్‌ను ఇన్‌స్టాల్ చేయవద్దు."
                    )
                )
                break
            }
        }

        // 7. URL RISK INFLUENCE
        if (urlAnalysisResult != null) {
            if (urlAnalysisResult.isIpAddress) baseScore += 25
            if (urlAnalysisResult.hasPunycode) baseScore += 25
            if (urlAnalysisResult.suspiciousTld) baseScore += 20
            if (urlAnalysisResult.isShortener) baseScore += 15

            // Check if domain matches any brand spoofing
            val spoofMatch = SPOOF_KEYWORDS.firstOrNull { urlAnalysisResult.domain.contains(it) }
            if (spoofMatch != null && (urlAnalysisResult.suspiciousTld || !urlAnalysisResult.domain.endsWith(".gov.in") && !urlAnalysisResult.domain.endsWith(".sbi") && !urlAnalysisResult.domain.endsWith(".bank"))) {
                baseScore += 20
                ruleSignals.add(RuleSignal("Domain Brand Spoofing", spoofMatch, "High", "Domain name '${urlAnalysisResult.domain}' mimics legitimate brand '$spoofMatch' on an unofficial registry."))
            }
        }

        // 8. LEGITIMATE SIGNALS (Reduces score)
        val legitMarkers = listOf(
            "view in your official app" to -20,
            "track your delivery in your amazon app" to -25,
            "track in flipkart app" to -25,
            "no action required" to -10
        )
        for ((marker, delta) in legitMarkers) {
            if (lower.contains(marker)) {
                baseScore += delta
                ruleSignals.add(RuleSignal("Safe Context Indicator", marker, "Low", "Directs user safely to their authenticated first-party app."))
            }
        }

        // Clamp score between 0 and 100
        val finalScore = baseScore.coerceIn(0, 100)

        // Evaluate Risk Level
        val riskLevel = when {
            finalScore >= 65 -> RiskLevel.HIGH
            finalScore in 35..64 -> RiskLevel.MEDIUM
            finalScore in 20..34 -> RiskLevel.UNCERTAIN
            scrubbed.length < 50 && ruleSignals.isEmpty() -> RiskLevel.UNCERTAIN // Insufficient context
            else -> RiskLevel.LOW
        }

        val snippet = scrubbed.take(80) + if (scrubbed.length > 80) "..." else ""

        // Generate explanations based on risk level
        val (summary, explanationEn, explanationTe) = when (riskLevel) {
            RiskLevel.HIGH -> Triple(
                "Multiple critical scam indicators detected including urgency, credential requests, or deceptive links.",
                "This message exhibits strong characteristics commonly seen in financial phishing and smishing scams. It tries to induce panic or greed to make you share sensitive credentials or open unsafe links.",
                "ఈ సందేశంలో స్పష్టమైన మోసపూరిత లక్షణాలు (అత్యవసర ఒత్తిడి, లింకులు లేదా వివరాల అభ్యర్థన) గుర్తించబడ్డాయి. వెంటనే క్లిక్ చేయడం లేదా వివరాలు పంచుకోవడం చేయకండి."
            )
            RiskLevel.MEDIUM -> Triple(
                "Suspicious traits identified. Exercise caution and verify before taking any action.",
                "While not definitively confirmed as a scam, this message uses language patterns or link structures often associated with marketing spam or deceptive campaigns. Proceed carefully.",
                "ఇందులో కొన్ని అనుమానాస్పద అంశాలు ఉన్నాయి. పూర్తిగా నిర్ధారించుకునే వరకు ఎలాంటి వివరాలు ఇవ్వకండి లేదా లింకులు తెరవకండి."
            )
            RiskLevel.UNCERTAIN -> Triple(
                "Insufficient context to determine risk conclusively. Verify sender independently.",
                "This message lacks typical technical threat markers but does not provide verifiable organizational proof. If received from an unknown sender, verify their identity independently before replying.",
                "ఈ సందేశం నుండి మోసపూరిత ఉద్దేశాన్ని కచ్చితంగా నిర్ధారించడానికి తగినంత సమాచారం లేదు. తెలియని నంబర్ అయితే స్పందించే ముందు నిర్ధారించుకోండి."
            )
            RiskLevel.LOW -> Triple(
                "No overt scam indicators detected. Low risk is not a guarantee of legitimacy.",
                "This communication appears to be a standard notification without known phishing or extortion traits. Always remain cautious: low risk does not guarantee 100% safety.",
                "ఇందులో మోసపూరిత సంకేతాలు కనిపించలేదు. అయితే తక్కువ ప్రమాదం అంటే సంపూర్ణ భరోసా కాదు; అప్రమత్తంగా ఉండండి."
            )
        }

        val safeNextSteps = when (riskLevel) {
            RiskLevel.HIGH -> listOf(
                "Do not click any links or call numbers provided in the message.",
                "Never share OTPs, UPI PINs, passwords, or card details.",
                "Contact the official institution directly using the phone number printed on your physical card or official website.",
                "Report the message as spam and block the sender."
            )
            RiskLevel.MEDIUM -> listOf(
                "Do not click on links directly from the SMS or message.",
                "Check the sender's identity through official apps or contact lists.",
                "Search the exact message text online to see if others reported it as spam.",
                "Keep your financial and personal information private."
            )
            RiskLevel.UNCERTAIN -> listOf(
                "Ask yourself if you were expecting this communication.",
                "Contact the purported sender through an independent known phone number.",
                "Do not confirm your identity or address if asked by an unknown contact.",
                "Verify independently before taking any action."
            )
            RiskLevel.LOW -> listOf(
                "Verify that you placed an order or requested this update.",
                "Use official apps rather than external message prompts whenever possible.",
                "Remember that even legitimate-looking messages warrant basic vigilance.",
                "Never share login credentials or OTPs with anyone."
            )
        }

        val safeNextStepsTe = when (riskLevel) {
            RiskLevel.HIGH -> listOf(
                "సందేశంలోని లింక్‌లపై ఎట్టి పరిస్థితుల్లోనూ క్లిక్ చేయవద్దు.",
                "ఓటీపీ, యూపీఐ పిన్, పాస్‌వర్డ్‌లను ఎవరితోనూ పంచుకోవద్దు.",
                "బ్యాంక్ లేదా సంస్థ అధికారిక హెల్ప్‌లైన్ నంబర్‌కు మాత్రమే కాల్ చేయండి.",
                "సందేశాన్ని స్పామ్‌గా మార్క్ చేసి నంబర్‌ను బ్లాక్ చేయండి."
            )
            RiskLevel.MEDIUM -> listOf(
                "మెసేజ్‌లోని లింక్‌లను నేరుగా తెరవకండి.",
                "పంపిన వ్యక్తి లేదా సంస్థను అధికారికంగా సరిచూడండి.",
                "వ్యక్తిగత లేదా బ్యాంకింగ్ వివరాలు తెలియని వారితో పంచుకోవద్దు.",
                "అనుమానం ఉంటే వెంటనే నిపుణులను లేదా కుటుంబ సభ్యులను సంప్రదించండి."
            )
            RiskLevel.UNCERTAIN -> listOf(
                "మీకు నిజంగా ఈ సమాచారం అవసరమా అనేది ఆలోచించండి.",
                "తెలియని నంబర్‌లకు మీ వివరాలు ఇవ్వకండి.",
                "స్వతంత్రంగా ఇతర మార్గాల ద్వారా నిర్ధారించుకోండి.",
                "అనుమానం ఉంటే స్పందించకండి."
            )
            RiskLevel.LOW -> listOf(
                "మీరు నిజంగా ఈ సేవను ఉపయోగించారో లేదో సరిచూసుకోండి.",
                "అధికారిక యాప్‌లను మాత్రమే ఉపయోగించండి.",
                "ఎల్లప్పుడూ అప్రమత్తంగా ఉండండి.",
                "మీ రహస్య సమాచారాన్ని భద్రంగా ఉంచుకోండి."
            )
        }

        return AnalysisResult(
            id = UUID.randomUUID().toString(),
            originalInput = scrubbed,
            inputSnippet = snippet,
            isUrlOnly = isUrlOnly,
            riskLevel = riskLevel,
            heuristicScore = finalScore,
            summary = summary,
            plainExplanationEn = explanationEn,
            plainExplanationTe = explanationTe,
            flaggedPhrases = flaggedPhrases,
            ruleSignals = ruleSignals,
            urlAnalysis = urlAnalysisResult,
            safeNextSteps = safeNextSteps,
            safeNextStepsTe = safeNextStepsTe,
            isSample = false,
            sampleLabel = null,
            analysisEngine = "Local Rule Pipeline + URL Heuristics",
            timestamp = System.currentTimeMillis()
        )
    }

    private fun extractSurroundingPhrase(text: String, keyword: String): String {
        val index = text.lowercase(Locale.ROOT).indexOf(keyword.lowercase(Locale.ROOT))
        if (index == -1) return keyword
        val start = (index - 20).coerceAtLeast(0)
        val end = (index + keyword.length + 20).coerceAtMost(text.length)
        val sub = text.substring(start, end).trim()
        val prefix = if (start > 0) "..." else ""
        val suffix = if (end < text.length) "..." else ""
        return "$prefix$sub$suffix"
    }
}
