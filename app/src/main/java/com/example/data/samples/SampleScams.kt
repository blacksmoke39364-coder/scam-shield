package com.example.data.samples

import com.example.data.model.AnalysisResult
import com.example.data.model.FlaggedPhrase
import com.example.data.model.RiskLevel
import com.example.data.model.RuleSignal
import com.example.data.model.UrlAnalysisResult

object SampleScams {

    val SAMPLE_KYC = AnalysisResult(
        id = "sample-kyc-01",
        originalInput = "Dear Customer, Your SBI account KYC has expired. Your account and debit card will be blocked within 24 hours. Click here to verify and update KYC: http://sbi-netbanking-kyc.top/login or call 9876543210 immediately.",
        inputSnippet = "Dear Customer, Your SBI account KYC has expired. Your account will be blocked...",
        isUrlOnly = false,
        riskLevel = RiskLevel.HIGH,
        heuristicScore = 94,
        summary = "Aggressive bank impersonation attempting credential theft via fake domain and manufactured 24-hour urgency deadline.",
        plainExplanationEn = "This message pretends to be from State Bank of India claiming your KYC has expired and threatens account blocking within 24 hours. Legitimate banks never send unverified HTTP links or third-party '.top' domains to update KYC.",
        plainExplanationTe = "ఈ సందేశం ఎస్‌బీఐ బ్యాంక్ నుండి వచ్చినట్లు నటిస్తోంది. 24 గంటల్లో అకౌంట్ బ్లాక్ అవుతుందని బెదిరిస్తూ నకిలీ లింక్ పంపించారు. అసలైన బ్యాంకులు ఎప్పుడూ ఇలాంటి అనధికారిక లింకులను పంపవు.",
        flaggedPhrases = listOf(
            FlaggedPhrase(
                phrase = "will be blocked within 24 hours",
                category = "Manufactured Urgency",
                whyItMatters = "Scammers create artificial panic deadlines to force victims into acting before they have time to verify with their branch.",
                safeNextStep = "Never rush. Banks give formal written notices or in-branch updates rather than instant 24-hour SMS bans.",
                whyItMattersTe = "ఆందోళన కలిగించి ఆలోచించే సమయం ఇవ్వకుండా మోసగాళ్లు ఇలా తప్పుడు గడువు పెడతారు.",
                safeNextStepTe = "కంగారు పడకండి. మీ బ్యాంక్ బ్రాంచ్‌ను నేరుగా సంప్రదించండి."
            ),
            FlaggedPhrase(
                phrase = "http://sbi-netbanking-kyc.top/login",
                category = "Phishing URL Trait",
                whyItMatters = "The link uses an unencrypted HTTP protocol and an untrusted '.top' top-level domain instead of the authentic 'sbi.co.in'.",
                safeNextStep = "Never open this link. Only use official banking apps downloaded from Google Play or type 'onlinesbi.sbi' manually.",
                whyItMattersTe = "ఇది నకిలీ వెబ్‌సైట్ (.top డొమైన్). అసలైన బ్యాంక్ వెబ్‌సైట్ కాదు.",
                safeNextStepTe = "ఈ లింక్‌పై క్లిక్ చేయకండి. అధికారిక యాప్‌ను మాత్రమే వాడండి."
            ),
            FlaggedPhrase(
                phrase = "call 9876543210 immediately",
                category = "Fake Customer Support",
                whyItMatters = "The phone number is a private mobile number, not an official bank helpline or toll-free number.",
                safeNextStep = "Only call the customer care number printed on the back of your physical debit card.",
                whyItMattersTe = "ఇది వ్యక్తిగత మొబైల్ నంబర్, అధికారిక బ్యాంక్ టోల్‌ఫ్రీ నంబర్ కాదు.",
                safeNextStepTe = "మీ ఏటీఎం కార్డు వెనుక ఉన్న అధికారిక నంబర్‌కు మాత్రమే కాల్ చేయండి."
            )
        ),
        ruleSignals = listOf(
            RuleSignal("Impersonation", "SBI", "High", "Impersonates major public sector banking institution"),
            RuleSignal("Urgency", "within 24 hours / immediately", "High", "Time-pressured coercion designed to bypass critical thinking"),
            RuleSignal("Phishing Domain", "sbi-netbanking-kyc.top", "High", "Suspicious spoofed hostname matching banking brand name")
        ),
        urlAnalysis = UrlAnalysisResult(
            url = "http://sbi-netbanking-kyc.top/login",
            domain = "sbi-netbanking-kyc.top",
            isIpAddress = false,
            hasPunycode = false,
            isShortener = false,
            suspiciousTld = true,
            reputationStatus = "Flagged suspicious by heuristic signature (Unverified Host)",
            timestampChecked = "2026-10-12 06:45 UTC",
            securityNote = "No detection on raw registries never means safe. Domain was registered recently with high entropy."
        ),
        safeNextSteps = listOf(
            "Do not click the link or call the number provided.",
            "Open your official YONO SBI or bank app independently to check account health.",
            "If in doubt, call the official number printed on the back of your debit card.",
            "Report the sender number to the National Cyber Crime portal (1930) or Chakshu portal."
        ),
        safeNextStepsTe = listOf(
            "సందేశంలోని లింక్‌పై ఎట్టి పరిస్థితుల్లోనూ క్లిక్ చేయవద్దు.",
            "బ్యాంకు ఖాతా స్థితిని చూడటానికి అధికారిక యాప్‌ని మాత్రమే తెరవండి.",
            "మీ కార్డు వెనుక ఉన్న అధికారిక బ్యాంక్ కస్టమర్ కేర్ నంబర్‌కు కాల్ చేయండి.",
            "సైబర్ క్రైమ్ హెల్ప్‌లైన్ నంబర్ 1930 లేదా Chakshu పోర్టల్‌లో ఫిర్యాదు చేయండి."
        ),
        isSample = true,
        sampleLabel = "Sample 1: Bank KYC & Account Block Threat",
        analysisEngine = "Sample Benchmark (Verified Threat)",
        timestamp = 1760250000000L
    )

    val SAMPLE_PRIZE = AnalysisResult(
        id = "sample-prize-02",
        originalInput = "CONGRATULATIONS! Your mobile number has won Rs 25,00,000 in Kaun Banega Crorepati (KBC) Sim Lucky Draw Season 2026. To claim your prize money in your bank, send Rs 1,500 registration and GST processing fee via GooglePay or PhonePe to Mr. Rajesh Kumar at 9988776655 immediately. Do not disclose to anyone.",
        inputSnippet = "CONGRATULATIONS! Your mobile number has won Rs 25,00,000 in KBC Lucky Draw...",
        isUrlOnly = false,
        riskLevel = RiskLevel.HIGH,
        heuristicScore = 98,
        summary = "Classic advance-fee lottery fraud demanding upfront money under the guise of taxes/registration fees.",
        plainExplanationEn = "Legitimate lotteries and contests never require winners to pay an advance fee, tax, or registration money to private UPI accounts. Once you send Rs 1,500, the fraudsters will demand larger sums or disappear.",
        plainExplanationTe = "లాటరీలో రూ. 25 లక్షలు గెలుచుకున్నారని చెప్పి, ఆ డబ్బు విడుదల కావాలంటే ముందుగా రూ. 1,500 ఫీజు కట్టాలని అడుగుతున్నారు. ఇది పూర్తి మోసం. ఏ చట్టబద్ధమైన సంస్థ కూడా గెలుచుకున్న బహుమతి కోసం ముందుగా డబ్బు అడగదు.",
        flaggedPhrases = listOf(
            FlaggedPhrase(
                phrase = "won Rs 25,00,000 in Kaun Banega Crorepati (KBC) Sim Lucky Draw",
                category = "Fake Prize / Lottery Lure",
                whyItMatters = "You cannot win a lottery or contest you never purchased a ticket for or formally entered.",
                safeNextStep = "Disregard any claims of unsolicited prize money or lucky draws.",
                whyItMattersTe = "మీరు పాల్గొనని పోటీలో లేదా కొనని లాటరీలో మీరు గెలవడం అసాధ్యం.",
                safeNextStepTe = "ఇలాంటి అవాస్తవ బహుమతుల సందేశాలను పట్టించుకోకండి."
            ),
            FlaggedPhrase(
                phrase = "send Rs 1,500 registration and GST processing fee via GooglePay",
                category = "Advance-Fee Fraud Demand",
                whyItMatters = "Taxes on legitimate prize winnings are deducted at source (TDS), never paid in advance to an individual's personal UPI number.",
                safeNextStep = "Never transfer money to claim a prize or refund. This is an upfront loss of funds.",
                whyItMattersTe = "నిజమైన బహుమతులపై పన్నులు మూలం వద్ద కట్ అవుతాయి (TDS). ఎవరి వ్యక్తిగత నంబర్‌కు ముందుగా పంపకూడదు.",
                safeNextStepTe = "డబ్బు పంపకండి. పంపితే ఆ డబ్బు తిరిగి రాదు."
            ),
            FlaggedPhrase(
                phrase = "Do not disclose to anyone",
                category = "Secrecy Coercion",
                whyItMatters = "Fraudsters isolate victims so friends and family cannot warn them about the scam.",
                safeNextStep = "Talk openly with a trusted family member or cyber expert before sending money.",
                whyItMattersTe = "మీ కుటుంబ సభ్యులు మిమ్మల్ని హెచ్చరించకుండా ఉండటానికి రహస్యంగా ఉంచమని చెప్తారు.",
                safeNextStepTe = "ఈ విషయాన్ని మీ కుటుంబ సభ్యులతో పంచుకోండి."
            )
        ),
        ruleSignals = listOf(
            RuleSignal("Lottery / Prize", "won Rs 25,00,000", "High", "Unsolicited large financial prize claim"),
            RuleSignal("Advance Payment", "send Rs 1,500 registration fee", "High", "Demands upfront payment via consumer UPI app"),
            RuleSignal("Isolation / Secrecy", "Do not disclose to anyone", "High", "Psychological manipulation to prevent third-party verification")
        ),
        urlAnalysis = null,
        safeNextSteps = listOf(
            "Do not send any money through GooglePay, PhonePe, or Paytm.",
            "Block the sender number and delete the message.",
            "Warn elderly family members who are frequently targeted by KBC lottery scams.",
            "Report the UPI ID to your payment provider as fraudulent."
        ),
        safeNextStepsTe = listOf(
            "గూగుల్‌పే, ఫోన్‌పే ద్వారా ఎటువంటి డబ్బు పంపవద్దు.",
            "ఈ మొబైల్ నంబర్‌ను వెంటనే బ్లాక్ చేయండి.",
            "ఇలాంటి మోసాల గురించి మీ ఇంట్లోని పెద్దవారికి తెలియజేయండి.",
            "ఈ యూపీఐ ఐడీని పేమెంట్ యాప్‌లో మోసపూరితమైనదిగా రిపోర్ట్ చేయండి."
        ),
        isSample = true,
        sampleLabel = "Sample 2: Fake Lottery & Advance UPI Fee Scam",
        analysisEngine = "Sample Benchmark (Verified Threat)",
        timestamp = 1760250000000L
    )

    val SAMPLE_DELIVERY = AnalysisResult(
        id = "sample-delivery-03",
        originalInput = "SpeedPost Notice: Your parcel package #IN-90821-X could not be delivered due to an incorrect address format. A redelivery fee of Rs 25 is required to reschedule. Pay now at http://ind-post-redelivery.help/reschedule within 12 hours or item will be returned to customs sender.",
        inputSnippet = "SpeedPost Notice: Your parcel package #IN-90821-X could not be delivered...",
        isUrlOnly = false,
        riskLevel = RiskLevel.HIGH,
        heuristicScore = 89,
        summary = "Postal service smishing tricking users into entering card details on a clone site under the guise of a trivial delivery charge.",
        plainExplanationEn = "This attack targets victims with small, seemingly negligible charges (Rs 25) so they don't think twice. However, entering your credit/debit card on this phishing link allows attackers to harvest card numbers and OTPs.",
        plainExplanationTe = "మీ కొరియర్ లేదా స్పీడ్‌పోస్ట్ అడ్రస్ తప్పుగా ఉందని, మళ్లీ పంపడానికి కేవలం రూ. 25 కట్టాలని అడుగుతున్నారు. ఇది తక్కువ మొత్తం కావడంతో ప్రజలు అనుమానించకుండా కార్డు వివరాలు ఇచ్చి మోసపోతారు.",
        flaggedPhrases = listOf(
            FlaggedPhrase(
                phrase = "A redelivery fee of Rs 25 is required",
                category = "Micro-Payment Trap",
                whyItMatters = "Scammers ask for small sums (Rs 25) to lower suspicion, aiming to capture full card credentials and unauthorized auto-debit consents.",
                safeNextStep = "Postal services do not demand online micro-fees via unsolicited SMS to resolve address typos.",
                whyItMattersTe = "అనుమానం రాకుండా చిన్న మొత్తం (రూ. 25) అడుగుతారు. కానీ మీ కార్డు వివరాలు దోచేస్తారు.",
                safeNextStepTe = "చిరునామా సరిచేయడానికి పోస్టల్ శాఖ ఆన్‌లైన్ ఫీజులు అడగదు."
            ),
            FlaggedPhrase(
                phrase = "http://ind-post-redelivery.help/reschedule",
                category = "Spoofed Postal Portal",
                whyItMatters = "The real India Post website is 'indiapost.gov.in'. A '.help' domain is a non-governmental spoof.",
                safeNextStep = "Only enter tracking numbers on 'indiapost.gov.in' or through the official India Post app.",
                whyItMattersTe = "భారత పోస్టల్ శాఖ అధికారిక వెబ్‌సైట్ 'indiapost.gov.in'. ఇది నకిలీ .help సైట్.",
                safeNextStepTe = "అధికారిక వెబ్‌సైట్‌లో మాత్రమే ట్రాకింగ్ నంబర్‌ను తనిఖీ చేయండి."
            )
        ),
        ruleSignals = listOf(
            RuleSignal("Courier Smishing", "SpeedPost / Parcel package", "High", "Fictitious courier parcel delivery failure"),
            RuleSignal("Micro-Payment", "fee of Rs 25", "Medium", "Credential harvesting vector disguised as minimal fee"),
            RuleSignal("Spoofed Domain", "ind-post-redelivery.help", "High", "Domain impersonates postal authority")
        ),
        urlAnalysis = UrlAnalysisResult(
            url = "http://ind-post-redelivery.help/reschedule",
            domain = "ind-post-redelivery.help",
            isIpAddress = false,
            hasPunycode = false,
            isShortener = false,
            suspiciousTld = true,
            reputationStatus = "Host flagged by dynamic heuristics: Fake government entity",
            timestampChecked = "2026-10-12 06:45 UTC",
            securityNote = "No detection on search engines never guarantees safety. Non .gov.in domain claiming official postal service."
        ),
        safeNextSteps = listOf(
            "Do not click the link or input card/banking details.",
            "If you actually ordered an item, check your order status directly on the e-commerce app you used.",
            "Visit your local post office with the tracking number if you suspect a real delivery issue.",
            "Mark the message as Spam."
        ),
        safeNextStepsTe = listOf(
            "ఈ లింక్‌ను ఓపెన్ చేయవద్దు మరియు కార్డు వివరాలను నమోదు చేయవద్దు.",
            "మీరు నిజంగా ఏదైనా ఆర్డర్ చేసి ఉంటే, ఆ యాప్‌లో మాత్రమే ఆర్డర్ స్థితిని చూడండి.",
            "అనుమానం ఉంటే స్థానిక పోస్ట్ ఆఫీస్‌ను సంప్రదించండి.",
            "ఈ మెసేజ్‌ను స్పామ్‌గా మార్క్ చేయండి."
        ),
        isSample = true,
        sampleLabel = "Sample 3: Courier Delivery & Fake Micro-Fee Phishing",
        analysisEngine = "Sample Benchmark (Verified Threat)",
        timestamp = 1760250000000L
    )

    val SAMPLE_LEGITIMATE = AnalysisResult(
        id = "sample-legit-04",
        originalInput = "Your Amazon order ending in 4102 has been dispatched and is out for delivery with our delivery associate. Expected by 4:00 PM today. Track your delivery and view OTP directly in your Amazon app.",
        inputSnippet = "Your Amazon order ending in 4102 has been dispatched and is out for delivery...",
        isUrlOnly = false,
        riskLevel = RiskLevel.LOW,
        heuristicScore = 8,
        summary = "Standard legitimate transactional dispatch alert directing the user to their existing official mobile app.",
        plainExplanationEn = "This notification includes expected order traits: references an existing order suffix (4102), does not ask for money or payment updates, contains no external shady links, and directs you to view details inside your official Amazon app.",
        plainExplanationTe = "ఇది సాధారణ మరియు సురక్షితమైన డెలివరీ అప్‌డేట్. ఇందులో ఎటువంటి మోసపూరిత లింకులు లేదా డబ్బు చెల్లింపు డిమాండ్లు లేవు. అధికారిక యాప్‌లోనే చూసుకోమని సూచించారు.",
        flaggedPhrases = emptyList(),
        ruleSignals = listOf(
            RuleSignal("Safe Indicator", "Track directly in your Amazon app", "Low", "Directs user to authenticated first-party application"),
            RuleSignal("No Threat Signals", "Zero financial or credential harvesting triggers", "Low", "No suspicious links or coercive deadlines detected")
        ),
        urlAnalysis = null,
        safeNextSteps = listOf(
            "No action needed if you recently placed this order.",
            "Check your Amazon app under 'Your Orders' to confirm dispatch.",
            "Only give the delivery OTP to the delivery agent in person when receiving the package at your door.",
            "Remember: low risk is not a 100% guarantee; stay vigilant if you did not order anything."
        ),
        safeNextStepsTe = listOf(
            "మీరు ఈ వస్తువును ఆర్డర్ చేసి ఉంటే ఎటువంటి ఆందోళన అవసరం లేదు.",
            "అమెజాన్ యాప్‌లో 'Your Orders' విభాగంలో నిర్ధారించుకోండి.",
            "వస్తువు మీ చేతికి అందిన తర్వాత మాత్రమే డెలివరీ ఏజెంట్‌కు ఓటీపీ ఇవ్వండి.",
            "గుర్తుంచుకోండి: తక్కువ ప్రమాదం అంటే 100% గ్యారెంటీ కాదు; అప్రమత్తంగా ఉండండి."
        ),
        isSample = true,
        sampleLabel = "Sample 4: Legitimate E-Commerce Order Notification",
        analysisEngine = "Sample Benchmark (Verified Safe Notification)",
        timestamp = 1760250000000L
    )

    val SAMPLE_UNCERTAIN = AnalysisResult(
        id = "sample-uncertain-05",
        originalInput = "Hi there, are you free this afternoon? Let's connect regarding the update we discussed last week. Let me know when you can talk.",
        inputSnippet = "Hi there, are you free this afternoon? Let's connect regarding the update...",
        isUrlOnly = false,
        riskLevel = RiskLevel.UNCERTAIN,
        heuristicScore = 32,
        summary = "Ambiguous message without obvious technical indicators, but origin cannot be verified without known sender context.",
        plainExplanationEn = "This message contains no links, payment demands, or urgency threats, but lacks sender identity. If this comes from an unknown number, it may be an initial probe ('wrong number' conversation starter) used in relationship or investment scams.",
        plainExplanationTe = "ఈ మెసేజ్‌లో ఎలాంటి లింకులు లేదా డబ్బు డిమాండ్లు లేవు. అయితే పంపిన వ్యక్తి ఎవరో స్పష్టంగా లేదు. తెలియని నంబర్ నుండి వస్తే, పరిచయం పెంచుకుని మోసం చేసే ప్రయత్నం కావొచ్చు. ధృవీకరించుకోండి.",
        flaggedPhrases = listOf(
            FlaggedPhrase(
                phrase = "regarding the update we discussed last week",
                category = "Vague Familiarity Hook",
                whyItMatters = "Scammers often use vague references to non-existent prior conversations to make you reply 'Who is this?' and start an engagement.",
                safeNextStep = "If the sender is not in your contacts, do not share personal details or confirm your identity.",
                whyItMattersTe = "మునుపటి సంభాషణ ఉన్నట్లు నటించి మీతో మాట కలపడానికి ఇలాంటి వాక్యాలు వాడుతుంటారు.",
                safeNextStepTe = "మీకు తెలియని వ్యక్తి అయితే మీ వ్యక్తిగత వివరాలను పంచుకోవద్దు."
            )
        ),
        ruleSignals = listOf(
            RuleSignal("Insufficient Context", "Vague relational reference", "Low", "No verifiable credentials or entity markers present"),
            RuleSignal("Indeterminate Risk", "Absence of explicit indicators", "Medium", "Cannot determine intent without knowing sender relationship")
        ),
        urlAnalysis = null,
        safeNextSteps = listOf(
            "Verify the sender's identity through another trusted channel before replying.",
            "Do not disclose your name, workplace, or schedule to an unknown number.",
            "If they claim to know you but you have no record, block and ignore.",
            "Treat unfamiliar callers with healthy skepticism."
        ),
        safeNextStepsTe = listOf(
            "సందేశం పంపిన వ్యక్తి ఎవరో తెలిసిన తర్వాతే స్పందించండి.",
            "తెలియని నంబర్‌లకు మీ పేరు లేదా వ్యక్తిగత సమాచారాన్ని చెప్పకండి.",
            "అనుమానం ఉంటే నంబర్‌ను బ్లాక్ చేసి విస్మరించండి.",
            "ముందుగా స్వతంత్రంగా నిర్ధారించుకోండి."
        ),
        isSample = true,
        sampleLabel = "Sample 5: Ambiguous / Insufficient Evidence Message",
        analysisEngine = "Sample Benchmark (Uncertain Evaluation)",
        timestamp = 1760250000000L
    )

    val ALL_SAMPLES = listOf(
        SAMPLE_KYC,
        SAMPLE_PRIZE,
        SAMPLE_DELIVERY,
        SAMPLE_LEGITIMATE,
        SAMPLE_UNCERTAIN
    )
}
