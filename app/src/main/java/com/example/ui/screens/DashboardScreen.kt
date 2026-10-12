package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ContentPasteGo
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnalysisResult
import com.example.data.model.RiskLevel
import com.example.data.samples.SampleScams
import com.example.ui.components.ThreeDCard
import com.example.ui.theme.BlackCarbon
import com.example.ui.theme.BlackElevated
import com.example.ui.theme.BlackObsidian
import com.example.ui.theme.BlackSurface
import com.example.ui.theme.RiskHighRed
import com.example.ui.theme.RiskLowTeal
import com.example.ui.theme.RiskMediumAmber
import com.example.ui.theme.SilverBorder
import com.example.ui.theme.SilverBorderGleam
import com.example.ui.theme.SilverChrome
import com.example.ui.theme.SilverDark
import com.example.ui.theme.SilverHighlight
import com.example.ui.theme.SilverMetallic
import com.example.ui.theme.SilverPlatinum
import com.example.ui.theme.SilverPure
import com.example.ui.theme.SilverSteel
import com.example.ui.theme.TealSubtle
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ScamShieldViewModel

@Composable
fun DashboardScreen(
    viewModel: ScamShieldViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val totalCount by viewModel.totalAnalysesCount.collectAsState()
    val highRiskCount by viewModel.highRiskCount.collectAsState()
    val history by viewModel.rawHistory.collectAsState()
    val context = LocalContext.current
    val composeClipboard = LocalClipboardManager.current

    val isTelugu = uiState.selectedLanguage == "TE"
    var inputMode by remember { mutableStateOf("MESSAGE") } // "MESSAGE", "URL", "OCR"
    var showDirectPasteModal by remember { mutableStateOf(false) }
    var modalInputDraft by remember { mutableStateOf("") }
    var pasteSuccessFeedback by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.processScreenshot(context, uri)
        }
    }

    // Ultimate Multi-Tier Clipboard Paste Handler
    fun performSmartPaste() {
        var retrievedText: String? = null

        // 1. Check Compose ClipboardManager
        try {
            val composeClip = composeClipboard.getText()?.text
            if (!composeClip.isNullOrBlank()) {
                retrievedText = composeClip.trim()
            }
        } catch (_: Exception) {}

        // 2. Check Android OS ClipboardManager
        if (retrievedText.isNullOrBlank()) {
            try {
                val androidClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                if (androidClipboard != null && androidClipboard.hasPrimaryClip()) {
                    val clip = androidClipboard.primaryClip
                    if (clip != null && clip.itemCount > 0) {
                        val item = clip.getItemAt(0)
                        val coerced = item.coerceToText(context)?.toString()
                        if (!coerced.isNullOrBlank()) {
                            retrievedText = coerced.trim()
                        }
                    }
                }
            } catch (_: Exception) {}
        }

        if (!retrievedText.isNullOrBlank()) {
            viewModel.setInputText(retrievedText)
            pasteSuccessFeedback = if (isTelugu)
                "✓ క్లిప్‌బోర్డ్ నుండి ${retrievedText.length} అక్షరాలు పేస్ట్ చేయబడ్డాయి"
            else
                "✓ Pasted ${retrievedText.length} characters from clipboard"
            viewModel.showToast(if (isTelugu) "క్లిప్‌బోర్డ్ నుండి అతికించబడింది." else "Pasted from clipboard successfully.")
        } else {
            // If device clipboard is empty (e.g. streaming emulator without host sync), open Direct Paste Modal!
            modalInputDraft = uiState.inputText
            showDirectPasteModal = true
        }
    }

    // Interactive Direct Paste Modal (Guarantees 100% paste functionality in any browser/emulator environment)
    if (showDirectPasteModal) {
        AlertDialog(
            onDismissRequest = { showDirectPasteModal = false },
            containerColor = BlackCarbon,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.border(1.dp, SilverBorderGleam, RoundedCornerShape(12.dp)),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Brush.linearGradient(listOf(SilverPure, SilverMetallic)), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPasteGo,
                            contentDescription = null,
                            tint = BlackObsidian,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isTelugu) "టెక్స్ట్ పేస్ట్ చేయండి (Paste Console)" else "Direct Paste Console",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SilverPure
                        )
                        Text(
                            text = if (isTelugu) "క్రింద పేస్ట్ చేయండి లేదా నమూనాను ఎంచుకోండి" else "Paste with Ctrl+V or tap an instant sample below",
                            style = MaterialTheme.typography.labelSmall,
                            color = SilverChrome
                        )
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isTelugu)
                            "మీ కంప్యూటర్ నుండి టెక్స్ట్‌ను ఇక్కడ నేరుగా పేస్ట్ చేయవచ్చు (Ctrl+V) లేదా 1-ట్యాప్ నమూనాలను ఎంచుకోవచ్చు:"
                        else
                            "Paste copied text directly in the box below, or pick an authentic real-world scam scenario to test:",
                        style = MaterialTheme.typography.bodySmall,
                        color = SilverMetallic
                    )

                    OutlinedTextField(
                        value = modalInputDraft,
                        onValueChange = { modalInputDraft = it },
                        placeholder = {
                            Text(
                                text = if (isTelugu) "ఇక్కడ పేస్ట్ చేయండి (Ctrl+V)..." else "Paste here (press Ctrl+V or right-click)...",
                                color = SilverSteel,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("modal_paste_input"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SilverPure,
                            unfocusedTextColor = SilverHighlight,
                            focusedContainerColor = BlackObsidian,
                            unfocusedContainerColor = BlackObsidian,
                            focusedBorderColor = SilverPure,
                            unfocusedBorderColor = SilverBorder
                        )
                    )

                    Text(
                        text = if (isTelugu) "నమూనా మోసాలు (1-ట్యాప్ టెస్ట్):" else "Instant Benchmark Scenarios:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = SilverHighlight
                    )

                    // 1-Tap Injection Buttons
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = {
                                modalInputDraft = "Dear Customer, Your SBI Bank account has been BLOCKED due to incomplete PAN KYC. Update immediately to prevent permanent deactivation: http://sbi-pan-kyc.top/verify"
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SilverBorder)
                        ) {
                            Text("🏦 1-Tap: SBI KYC Account Suspension", color = SilverHighlight, fontSize = 11.5.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                modalInputDraft = "ప్రియమైన వినియోగదారుడా, మీ గత నెల విద్యుత్ బిల్లు చెల్లించనందున ఈ రాత్రి 9:30 గంటలకు విద్యుత్ సరఫరా నిలిపివేయబడుతుంది. వెంటనే సంప్రదించండి: 9876543210 లేదా http://tg-bijli-pay.xyz/bill లో చెల్లించండి."
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SilverBorder)
                        ) {
                            Text("⚡ 1-Tap: కరెంట్ బిల్లు కటాఫ్ (Telugu)", color = SilverHighlight, fontSize = 11.5.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                modalInputDraft = "CONGRATULATIONS! Your mobile number won Rs 25,00,000 in KBC Lucky Draw 2026. Send processing fee Rs 450 to UPI: lotteryhelp@upi to claim prize."
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SilverBorder)
                        ) {
                            Text("🎁 1-Tap: Rs 25 Lakh Lottery Claim", color = SilverHighlight, fontSize = 11.5.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (modalInputDraft.isNotBlank()) {
                            viewModel.setInputText(modalInputDraft.trim())
                            pasteSuccessFeedback = if (isTelugu) "✓ టెక్స్ట్ విజయవంతంగా లోడ్ అయింది" else "✓ Text Loaded Successfully"
                            showDirectPasteModal = false
                            viewModel.showToast(if (isTelugu) "టెక్స్ట్ లోడ్ చేయబడింది." else "Text pasted successfully.")
                        } else {
                            showDirectPasteModal = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SilverPure),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isTelugu) "లోడ్ చేసి విశ్లేషించు" else "Apply & Analyze",
                        color = BlackObsidian,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDirectPasteModal = false }) {
                    Text(if (isTelugu) "రద్దు చేయి" else "Cancel", color = SilverSteel)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen")
            .background(BlackObsidian)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // 3D Header Bar with Luminous Platinum Shield & Bilingual Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 3D Embossed Shield Emblem
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(8.dp, RoundedCornerShape(10.dp), ambientColor = Color(0x80FFFFFF), spotColor = Color(0x40FFFFFF))
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(SilverPure, SilverMetallic, BlackSurface)
                                )
                            )
                            .border(1.5.dp, Brush.verticalGradient(listOf(SilverPure, SilverDark)), RoundedCornerShape(10.dp))
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(BlackCarbon, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "ScamShield 3D",
                                tint = SilverPure,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ScamShield AI",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = SilverPure,
                            letterSpacing = 0.5.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(TealSubtle)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isTelugu) "క్లిక్ చేసే ముందు ఆలోచించండి" else "Think Before You Click.",
                                style = MaterialTheme.typography.labelSmall,
                                color = SilverChrome,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // 3D Bilingual Toggle Switch (EN / తెలుగు)
                Box(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(BlackCarbon)
                        .border(1.dp, SilverBorder, RoundedCornerShape(8.dp))
                        .padding(3.dp)
                ) {
                    Row {
                        Box(
                            modifier = Modifier
                                .clickable { viewModel.toggleLanguage("EN") }
                                .background(
                                    if (!isTelugu) Brush.verticalGradient(listOf(SilverPure, SilverMetallic)) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "EN",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = if (!isTelugu) BlackObsidian else SilverSteel
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clickable { viewModel.toggleLanguage("TE") }
                                .background(
                                    if (isTelugu) Brush.verticalGradient(listOf(SilverPure, SilverMetallic)) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "తెలుగు",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = if (isTelugu) BlackObsidian else SilverSteel
                            )
                        }
                    }
                }
            }
        }

        // 3D Engine Radar Telemetry Banner
        item {
            ThreeDCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TealSubtle)
                                .shadow(4.dp, CircleShape, spotColor = TealSubtle)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isTelugu) "భద్రతా వ్యవస్థ: జెమిని 3.5 + గూగుల్ సేఫ్ బ్రౌజింగ్ + ML Kit" else "Security Radar: Gemini LLM + Google Safe Browsing + ML Kit",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = SilverHighlight
                        )
                    }

                    Text(
                        text = "3D TITANIUM",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                        color = SilverSteel
                    )
                }
            }
        }

        // 3D Metallic Quick Stats Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard3D(
                    title = if (isTelugu) "స్కాన్ చేసినవి" else "Scanned",
                    value = "$totalCount",
                    valueColor = SilverPure,
                    modifier = Modifier.weight(1f)
                )
                MetricCard3D(
                    title = if (isTelugu) "ముప్పులు" else "Threats Caught",
                    value = "$highRiskCount",
                    valueColor = RiskHighRed,
                    modifier = Modifier.weight(1f)
                )
                MetricCard3D(
                    title = if (isTelugu) "సేఫ్ బ్రౌజింగ్" else "Safe Browsing",
                    value = "Google v4",
                    valueColor = TealSubtle,
                    modifier = Modifier.weight(1.2f)
                )
            }
        }

        // 1-Tap Direct Scam Injections Carousel (Never fail to test scams!)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTelugu) "1-ట్యాప్ నమూనాలు (వెంటనే పరీక్షించండి):" else "1-Tap Quick Injections (Instant Test):",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        fontWeight = FontWeight.Bold,
                        color = SilverChrome
                    )
                    Text(
                        text = if (isTelugu) "క్లిక్ చేసి అతికించండి" else "Tap to Paste",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = SilverSteel
                    )
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        QuickChip3D(
                            label = if (isTelugu) "🏦 ఎస్‌బీఐ కేవైసీ మోసం" else "🏦 SBI KYC Smishing",
                            onClick = {
                                viewModel.setInputText("Dear Customer, Your SBI account KYC has expired. Account will be blocked within 24 hours. Update immediately: http://sbi-pan-kyc.top/verify. Do not ignore.")
                                pasteSuccessFeedback = "✓ Loaded SBI KYC Scam"
                                viewModel.showToast("Sample SBI KYC message loaded.")
                            }
                        )
                    }
                    item {
                        QuickChip3D(
                            label = if (isTelugu) "⚡ కరెంట్ బిల్లు కటాఫ్ (తెలుగు)" else "⚡ Telugu Electricity Cutoff",
                            onClick = {
                                viewModel.setInputText("ప్రియమైన వినియోగదారుడా, మీ గత నెల విద్యుత్ బిల్లు చెల్లించనందున ఈ రాత్రి 9:30 గంటలకు విద్యుత్ సరఫరా నిలిపివేయబడుతుంది. వెంటనే సంప్రదించండి: 9876543210 లేదా http://tg-bijli-pay.xyz/bill లో చెల్లించండి.")
                                pasteSuccessFeedback = "✓ తెలుగు విద్యుత్ మోసం లోడ్ చేయబడింది"
                                viewModel.showToast("తెలుగు విద్యుత్ మోసం లోడ్ చేయబడింది. 'మోసాన్ని విశ్లేషించండి' నొక్కండి.")
                            }
                        )
                    }
                    item {
                        QuickChip3D(
                            label = if (isTelugu) "🎁 రూ. 25లక్షల లాటరీ" else "🎁 Rs 25L KBC Lottery",
                            onClick = {
                                viewModel.setInputText("CONGRATULATIONS! Your mobile number won Rs 25,00,000 in KBC Lucky Draw 2026. Send processing fee Rs 450 to UPI: lotteryhelp@upi and claim prize.")
                                pasteSuccessFeedback = "✓ Loaded Lottery Scam"
                                viewModel.showToast("Sample Lottery message loaded.")
                            }
                        )
                    }
                    item {
                        QuickChip3D(
                            label = if (isTelugu) "📦 స్పీడ్‌పోస్ట్ ఫీజు" else "📦 SpeedPost Redelivery",
                            onClick = {
                                viewModel.setInputText("IndiaPost Notice: Package delivery failed due to missing street address. Pay redelivery charge Rs 25 to avoid parcel return: http://indiapost-redelivery.xyz/track")
                                pasteSuccessFeedback = "✓ Loaded Delivery Scam"
                                viewModel.showToast("Sample Delivery message loaded.")
                            }
                        )
                    }
                    item {
                        QuickChip3D(
                            label = if (isTelugu) "✅ సురక్షిత అమెజాన్ ఆర్డర్" else "✅ Safe Amazon Notice",
                            onClick = {
                                viewModel.setInputText("Your Amazon order #402-91823 has been dispatched. Expected delivery by Friday. Track in your Amazon official mobile app.")
                                pasteSuccessFeedback = "✓ Loaded Safe Notice"
                                viewModel.showToast("Sample Legitimate update loaded.")
                            }
                        )
                    }
                }
            }
        }

        // PRIMARY 3D INPUT & ANALYSIS CONSOLE
        item {
            ThreeDCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Top Mode Selector Row and Robust Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 3D Segmented Input Mode Tabs
                        Row(
                            modifier = Modifier
                                .background(BlackObsidian, RoundedCornerShape(8.dp))
                                .border(1.dp, SilverBorder, RoundedCornerShape(8.dp))
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clickable { inputMode = "MESSAGE" }
                                    .background(
                                        if (inputMode == "MESSAGE") Brush.verticalGradient(listOf(SilverPure, SilverMetallic)) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 9.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Message,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = if (inputMode == "MESSAGE") BlackObsidian else SilverChrome
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isTelugu) "టెక్స్ట్ / SMS" else "Text / SMS",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = if (inputMode == "MESSAGE") BlackObsidian else SilverChrome,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clickable { inputMode = "URL" }
                                    .background(
                                        if (inputMode == "URL") Brush.verticalGradient(listOf(SilverPure, SilverMetallic)) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 9.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Link,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = if (inputMode == "URL") BlackObsidian else SilverChrome
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isTelugu) "వెబ్ లింక్ (URL)" else "Web URL",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = if (inputMode == "URL") BlackObsidian else SilverChrome,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // HIGH-PRIORITY ACTION BUTTONS (PASTE + SCREENSHOT OCR)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            // Screenshot OCR 3D Button
                            Box(
                                modifier = Modifier
                                    .shadow(4.dp, RoundedCornerShape(6.dp))
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                    .background(BlackObsidian)
                                    .border(1.dp, SilverBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .testTag("ocr_screenshot_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = "Scan Screenshot",
                                        tint = SilverHighlight,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isTelugu) "📸 స్క్రీన్‌షాట్" else "📸 OCR",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                        color = SilverHighlight
                                    )
                                }
                            }

                            // PRIMARY 3D METALLIC PASTE BUTTON (Fixed & Guaranteed!)
                            Box(
                                modifier = Modifier
                                    .shadow(6.dp, RoundedCornerShape(6.dp), ambientColor = Color(0x60FFFFFF), spotColor = Color(0x30FFFFFF))
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { performSmartPaste() }
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(SilverPure, SilverMetallic, SilverChrome)
                                        )
                                    )
                                    .border(1.dp, Color.White, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 11.dp, vertical = 6.dp)
                                    .testTag("paste_clipboard_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ContentPaste,
                                        contentDescription = "Paste",
                                        tint = BlackObsidian,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (isTelugu) "పేస్ట్" else "PASTE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = BlackObsidian
                                    )
                                }
                            }

                            if (uiState.inputText.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        viewModel.setInputText("")
                                        pasteSuccessFeedback = null
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = SilverSteel,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Transient Paste Confirmation Glow Badge
                    AnimatedVisibility(
                        visible = pasteSuccessFeedback != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F291E), RoundedCornerShape(6.dp))
                                .border(1.dp, TealSubtle.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = TealSubtle,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = pasteSuccessFeedback ?: "",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TealSubtle, fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }

                    // 3D Inset Text Input Field
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(3.dp, RoundedCornerShape(8.dp), ambientColor = Color.Black, spotColor = Color.Black)
                            .background(BlackObsidian, RoundedCornerShape(8.dp))
                            .border(1.dp, SilverBorder, RoundedCornerShape(8.dp))
                    ) {
                        OutlinedTextField(
                            value = uiState.inputText,
                            onValueChange = {
                                viewModel.setInputText(it)
                                pasteSuccessFeedback = null
                            },
                            placeholder = {
                                Text(
                                    text = if (inputMode == "URL")
                                        if (isTelugu) "లింక్‌ను ఇక్కడ టైప్ చేయండి లేదా పేస్ట్ చేయండి (ఉదా: http://sbi-kyc.top)..." else "Paste or type link (e.g. http://sbi-kyc.top or bit.ly/claim)..."
                                    else if (isTelugu)
                                        "అనుమానాస్పద ఎస్‌ఎంఎస్, వాట్సాప్ సందేశం లేదా యూపీఐ అభ్యర్థనను ఇక్కడ టైప్ చేయండి లేదా పేస్ట్ చేయండి..."
                                    else
                                        "Paste or type suspicious SMS, WhatsApp message, UPI request, or email snippet...",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                    color = SilverSteel
                                )
                            },
                            trailingIcon = {
                                // Inline Fast-Paste / Keyboard Assistant Button
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            modalInputDraft = uiState.inputText
                                            showDirectPasteModal = true
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Keyboard,
                                            contentDescription = "Paste Assistant",
                                            tint = SilverChrome,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(135.dp)
                                .testTag("message_input_field"),
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (uiState.inputText.isNotBlank()) {
                                        viewModel.analyzeMessage()
                                    }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = SilverPure,
                                unfocusedTextColor = SilverHighlight,
                                focusedContainerColor = BlackObsidian,
                                unfocusedContainerColor = BlackObsidian,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                    }

                    // Action Controls: Masking Guarantee & 3D Tactile Analyze Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SilverChrome,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isTelugu) "ఓటీపీలు & పిన్‌లు ఆటో-మాస్క్ చేయబడతాయి" else "Sensitive OTPs/PINs Auto-Masked",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = SilverChrome
                            )
                        }

                        // 3D Tactile Analyze Threat Button
                        Button(
                            onClick = { viewModel.analyzeMessage() },
                            enabled = !uiState.isAnalyzing && uiState.inputText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SilverPure,
                                disabledContainerColor = SilverDark.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .shadow(8.dp, RoundedCornerShape(8.dp), ambientColor = Color(0x60FFFFFF), spotColor = Color(0x40FFFFFF))
                                .border(1.dp, if (uiState.inputText.isNotBlank()) Color.White else SilverBorder, RoundedCornerShape(8.dp))
                                .testTag("analyze_button")
                        ) {
                            if (uiState.isAnalyzing) {
                                CircularProgressIndicator(
                                    color = BlackObsidian,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isTelugu) "విశ్లేషిస్తోంది..." else "Analyzing...",
                                    color = BlackObsidian,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = if (uiState.inputText.isNotBlank()) BlackObsidian else SilverSteel,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isTelugu) "మోసాన్ని విశ్లేషించండి" else "Analyze Threat",
                                    color = if (uiState.inputText.isNotBlank()) BlackObsidian else SilverSteel,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3D Forensic Security Intelligence Tip Card
        item {
            ThreeDCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = SilverHighlight,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isTelugu) "సైబర్ సెక్యూరిటీ సూత్రం:" else "Forensic Intelligence Rule:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = SilverHighlight
                        )
                    }
                    Text(
                        text = if (isTelugu)
                            "కరెంట్ బిల్లు కట్టలేదని, బ్యాంకు ఖాతా నిలిపివేయబడిందని వచ్చే మెసేజ్‌లలోని లింక్‌లను ఎప్పుడూ తెరవకండి. అధికారిక పోర్టల్స్‌లో మాత్రమే చెల్లించండి."
                        else
                            "Electricity cutoff threats and instant bank suspension SMS messages are common smishing campaigns. Always verify directly on official provider portals.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SilverMetallic
                    )
                }
            }
        }

        // 3D Threat Case Studies Carousel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTelugu) "పరిశోధనా నమూనాలు (Benchmarks)" else "Full Threat Case Studies",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SilverPure
                    )
                    Text(
                        text = "5 Scenarios",
                        style = MaterialTheme.typography.labelSmall,
                        color = SilverSteel
                    )
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(SampleScams.ALL_SAMPLES) { sample ->
                        DossierCard3D(
                            sample = sample,
                            isTelugu = isTelugu,
                            onClick = { viewModel.loadSample(sample) }
                        )
                    }
                }
            }
        }

        // Recent Analyses (if any)
        if (history.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isTelugu) "ఇటీవలి విశ్లేషణలు" else "Recent Analyses",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SilverPure
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                viewModel.navigateTo(AppScreen.HISTORY)
                            }
                        ) {
                            Text(
                                text = if (isTelugu) "అన్నీ చూడండి (${history.size})" else "View All (${history.size})",
                                style = MaterialTheme.typography.labelSmall,
                                color = SilverHighlight
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = SilverHighlight,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    history.take(4).forEach { entity ->
                        RecentRow3D(
                            entity = entity,
                            onClick = { viewModel.viewResult(entity.toDomainModel()) }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun MetricCard3D(
    title: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    ThreeDCard(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    letterSpacing = 0.5.sp
                ),
                color = SilverSteel
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = valueColor
            )
        }
    }
}

@Composable
private fun QuickChip3D(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .shadow(4.dp, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .background(BlackCarbon)
            .border(
                1.dp,
                Brush.verticalGradient(listOf(SilverDark, Color.Black)),
                RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
            color = SilverHighlight
        )
    }
}

@Composable
private fun DossierCard3D(
    sample: AnalysisResult,
    isTelugu: Boolean,
    onClick: () -> Unit
) {
    val (badgeColor, riskText) = when (sample.riskLevel) {
        RiskLevel.HIGH -> RiskHighRed to (if (isTelugu) "అధిక ప్రమాదం" else "HIGH RISK")
        RiskLevel.MEDIUM -> RiskMediumAmber to (if (isTelugu) "మధ్యస్థ ప్రమాదం" else "MEDIUM RISK")
        RiskLevel.LOW -> RiskLowTeal to (if (isTelugu) "తక్కువ ప్రమాదం" else "LOW RISK")
        RiskLevel.UNCERTAIN -> SilverSteel to (if (isTelugu) "సందేహాస్పదం" else "UNCERTAIN")
    }

    ThreeDCard(
        modifier = Modifier
            .width(240.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        elevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .border(0.5.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = riskText,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = badgeColor,
                        fontWeight = FontWeight.Black
                    )
                }

                Text(
                    text = "${sample.heuristicScore}/100",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = badgeColor
                )
            }

            Text(
                text = sample.sampleLabel ?: sample.summary,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
                color = SilverPure
            )

            Text(
                text = sample.inputSnippet,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                maxLines = 2,
                color = SilverChrome
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (isTelugu) "సాక్ష్యాలను చూడండి" else "Inspect Forensic Dossier",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = SilverHighlight,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = SilverHighlight,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
    }
}

@Composable
private fun RecentRow3D(
    entity: com.example.data.local.AnalysisEntity,
    onClick: () -> Unit
) {
    val riskColor = when (entity.riskLevel.uppercase()) {
        "HIGH" -> RiskHighRed
        "MEDIUM" -> RiskMediumAmber
        "LOW" -> RiskLowTeal
        else -> SilverSteel
    }

    ThreeDCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entity.inputSnippet,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    color = SilverPure
                )
                Text(
                    text = "Score: ${entity.heuristicScore}/100 • ${entity.summary.take(45)}...",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = SilverChrome
                )
            }

            Box(
                modifier = Modifier
                    .background(riskColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                    .border(0.5.dp, riskColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = entity.riskLevel,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = riskColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
