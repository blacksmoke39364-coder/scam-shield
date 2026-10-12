package com.example.data.samples

import com.example.data.model.ScamPattern

object LearnPatterns {

    val PATTERNS = listOf(
        ScamPattern(
            id = "pattern-kyc",
            title = "Bank KYC Expiry & Immediate Account Suspension",
            targetChannel = "SMS / WhatsApp",
            summary = "Attacker sends an alarming text claiming your account or card will be blocked within 12-24 hours unless you click a link or call a number.",
            psychologicalLever = "Panic & Manufactured Urgency (Fear of losing access to money)",
            redFlags = listOf(
                "Tight artificial deadline (e.g. 'within 24 hours', 'tonight at 9:30 PM')",
                "Non-banking link domain (.top, .xyz, bit.ly, or raw IP address)",
                "Sender is a standard 10-digit mobile number, not an official bank header (e.g., AD-SBIINB)"
            ),
            realWorldExample = "Dear customer, your SBI account KYC has expired. Your account will be blocked within 24 hours. Update KYC at http://sbi-kyc-verify.top or call 9876543210.",
            safeResponse = "Never click links or call mobile numbers in SMS. Open your official banking app directly or visit your branch. Real banks never block accounts with instant 24-hour SMS threats.",
            teluguTip = "బ్యాంకు ఎప్పుడూ ఎస్‌ఎంఎస్‌లలో లింకులు పంపి కేవైసీ అప్‌డేట్ చేయమని అడగదు. అధికారిక బ్యాంక్ యాప్‌ను మాత్రమే వాడండి."
        ),
        ScamPattern(
            id = "pattern-electricity",
            title = "Electricity Disconnection at 9:30 PM",
            targetChannel = "SMS / WhatsApp",
            summary = "Attacker claims your previous month's power bill is unpaid and the electricity officer will cut power tonight unless you call a specific mobile number.",
            psychologicalLever = "Immediate Disruption Fear (Losing home electricity)",
            redFlags = listOf(
                "Threatens disconnection tonight at a specific hour",
                "Asks you to call a private mobile number for 'Electricity Officer'",
                "Instructs you to pay a nominal Rs 10 fee via an APK or screen-sharing app like AnyDesk"
            ),
            realWorldExample = "Dear Consumer, Your electricity power will be disconnected tonight at 9:30 PM from the power office because your previous month bill was not updated. Please immediately contact our electricity officer at 8976543210.",
            safeResponse = "Check your electricity bill status exclusively on the official state DISCOM portal (e.g. TSSPDCL, BESCOM, TANGEDCO) or authorized utility apps. Power boards never dispatch individual WhatsApp threats.",
            teluguTip = "కరెంట్ బిల్లు కట్టలేదని రాత్రి కరెంట్ కట్ చేస్తామని వచ్చే మెసేజ్‌లు నూటికి నూరు శాతం మోసం. మీ రాష్ట్ర విద్యుత్ శాఖ వెబ్‌సైట్‌లో మాత్రమే బిల్లు చూడండి."
        ),
        ScamPattern(
            id = "pattern-task-job",
            title = "Part-Time YouTube/Hotel Review Task Scam",
            targetChannel = "WhatsApp / Telegram",
            summary = "Scammers invite you to earn Rs 3,000 to Rs 8,000 daily by liking YouTube videos or leaving Google hotel reviews. They initially pay Rs 150-500, then demand large deposits to unlock 'VIP prepaid tasks'.",
            psychologicalLever = "Greed & Sunk Cost Trap (Easy money hook)",
            redFlags = listOf(
                "Unsolicited job offer claiming high daily pay for trivial rating clicks",
                "Communication shifts rapidly to an anonymous Telegram group",
                "Requires you to send money to 'recharge' or 'upgrade' your account to withdraw earnings"
            ),
            realWorldExample = "Hello! I am HR from Global Marketing. Earn Rs 2,500 - 5,000 daily by working 30 mins from home simply by giving 5-star ratings to hotels. Message on Telegram @TaskManager to receive your Rs 200 joining bonus.",
            safeResponse = "Legitimate companies never hire via unsolicited WhatsApp blasts or pay for fake reviews. Never transfer money to unlock wages.",
            teluguTip = "వీడియోలు లైక్ చేస్తే రోజూ వేల రూపాయలు ఇస్తామనేది మోసం. మొదట చిన్న మొత్తం ఇచ్చి, తర్వాత పెద్ద మొత్తంలో డిపాజిట్ చేయమని అడుగుతారు."
        ),
        ScamPattern(
            id = "pattern-parcel-fee",
            title = "India Post / Courier Address Typo & Rs 25 Fee Trap",
            targetChannel = "SMS",
            summary = "Attacker informs you a delivery failed due to a missing street number. You are asked to click an unofficial link to pay a trivial Rs 25 redelivery fee.",
            psychologicalLever = "Curiosity & Low Suspicion (Small Rs 25 fee)",
            redFlags = listOf(
                "Demands a small micro-fee (Rs 15 to Rs 35) via a webpage",
                "Web link is not indiapost.gov.in (uses .help, .top, .org, or random letter domains)",
                "Website page asks for your full 16-digit debit card number and CVV"
            ),
            realWorldExample = "SpeedPost Notice: Package #IN-8890 cannot be delivered due to missing house number. Pay Rs 25 redelivery charge at http://india-post-support.help to update address.",
            safeResponse = "India Post does not demand online card payments to correct address typos. Real delivery agents call from local facilities. Never input bank card details on third-party links.",
            teluguTip = "పార్సెల్ ఆగిపోయిందని రూ. 25 కట్టమని అడిగితే నమ్మకండి. ఆ లింక్‌లో కార్డు వివరాలు ఇస్తే మీ ఖాతాలోని మొత్తం ఖాళీ అయిపోతుంది."
        ),
        ScamPattern(
            id = "pattern-digital-arrest",
            title = "Digital Arrest & Fake Police / CBI Video Extortion",
            targetChannel = "Phone / WhatsApp Video",
            summary = "Fraudsters impersonating police, customs, or TRAI claim illegal drugs, contraband, or money laundering was traced to your Aadhaar card or mobile SIM. They place you under 'digital arrest' on video call.",
            psychologicalLever = "Authority Intimidation & Fear of Imprisonment",
            redFlags = listOf(
                "Claims your Aadhaar or mobile SIM is involved in a high-profile criminal case in another state",
                "Demands you stay connected continuously on Skype or WhatsApp video call in a closed room",
                "Instructs you to transfer money to a 'RBI safe government verification account' for auditing"
            ),
            realWorldExample = "This is Officer Sharma from Delhi Crime Branch. A parcel with passports and narcotics has been intercepted in your name. You are under Digital Arrest. Transfer your savings to the verification escrow account immediately to prove your innocence.",
            safeResponse = "There is NO legal concept of 'Digital Arrest' anywhere in Indian or international law. Police never conduct trials or bail procedures over Skype or WhatsApp video. Disconnect immediately and dial 1930.",
            teluguTip = "చట్టంలో 'డిజిటల్ అరెస్ట్' అనేదే లేదు. పోలీసులు వాట్సాప్ లేదా స్కైప్ వీడియో కాల్స్‌లో విచారణ చేయరు లేదా డబ్బు బదిలీ చేయమని అడగరు. వెంటనే 1930కి ఫిర్యాదు చేయండి."
        ),
        ScamPattern(
            id = "pattern-apk-challan",
            title = "Fake Traffic Challan or RTO Sideloaded APK Malware",
            targetChannel = "WhatsApp / SMS",
            summary = "Scammers send a message warning of an unpaid e-challan or driving license violation with an attached '.apk' file disguised as 'Parivahan_Challan_Update.apk'.",
            psychologicalLever = "Fear of Penalty & Routine Compliance",
            redFlags = listOf(
                "Contains an Android installer file (.apk extension)",
                "Asks user to download and install an app from outside the Google Play Store",
                "Requests permissions for SMS reading, Accessibility, and Notification access upon install"
            ),
            realWorldExample = "Traffic Police Alert: Your vehicle registration has an overdue e-challan fine of Rs 1,000. Download and install http://echallan-parivahan.in/ChallanPay.apk to pay fine with 50% discount.",
            safeResponse = "Never install APK files sent through messaging apps. Official traffic challans are paid exclusively on https://echallan.parivahan.gov.in or official state police portals.",
            teluguTip = "ఏపీకే (.apk) ఫైల్స్ ఎప్పుడూ ఇన్‌స్టాల్ చేయవద్దు. ఇవి మీ ఫోన్‌లోని బ్యాంక్ ఓటీపీలను దొంగిలించే ప్రమాదకరమైన వైరస్‌లు."
        )
    )
}
