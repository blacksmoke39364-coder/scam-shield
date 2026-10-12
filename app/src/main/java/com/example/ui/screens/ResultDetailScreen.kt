package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RiskLevel
import com.example.ui.components.EvidenceCard
import com.example.ui.components.FeedbackDialog
import com.example.ui.components.ThreatGauge
import com.example.ui.components.ThreeDCard
import com.example.ui.components.UrlInspectionCard
import com.example.ui.theme.BlackCarbon
import com.example.ui.theme.BlackObsidian
import com.example.ui.theme.BlackSurface
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.RiskHighBgDark
import com.example.ui.theme.RiskHighRed
import com.example.ui.theme.RiskLowBgDark
import com.example.ui.theme.RiskLowTeal
import com.example.ui.theme.RiskMediumAmber
import com.example.ui.theme.RiskMediumBgDark
import com.example.ui.theme.RiskUncertainBgDark
import com.example.ui.theme.RiskUncertainSlate
import com.example.ui.theme.SilverBorder
import com.example.ui.theme.SilverBorderGleam
import com.example.ui.theme.SilverChrome
import com.example.ui.theme.SilverHighlight
import com.example.ui.theme.SilverMetallic
import com.example.ui.theme.SilverPure
import com.example.ui.theme.SilverSteel
import com.example.ui.theme.TealSubtle
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ScamShieldViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResultDetailScreen(
    viewModel: ScamShieldViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val result = uiState.currentResult ?: return
    val context = LocalContext.current
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showShareModal by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.navigateTo(AppScreen.DASHBOARD)
    }

    val currentLang = uiState.selectedLanguage
    val isTelugu = currentLang == "TE"

    if (showFeedbackDialog) {
        FeedbackDialog(
            resultId = result.id,
            onDismiss = { showFeedbackDialog = false },
            onSubmit = { reason ->
                viewModel.submitFeedback(result.id, reason)
                showFeedbackDialog = false
            }
        )
    }

    // 3D Share Options Modal with Silver & Black styling
    if (showShareModal) {
        AlertDialog(
            onDismissRequest = { showShareModal = false },
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
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = BlackObsidian,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isTelugu) "హెచ్చరికను షేర్ చేయండి" else "Share Scam Warning",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SilverPure
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isTelugu)
                            "మీ స్నేహితులు మరియు కుటుంబ సభ్యులను రక్షించడానికి క్రింది ఎంపికలను ఉపయోగించండి:"
                        else
                            "Help protect your contacts from falling victim to this threat:",
                        style = MaterialTheme.typography.bodySmall,
                        color = SilverMetallic
                    )

                    // System Share Sheet (ShareCompat)
                    Button(
                        onClick = {
                            showShareModal = false
                            viewModel.shareReport(context, result, currentLang)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SilverPure),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = BlackObsidian, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (isTelugu) "సిస్టమ్ షేర్ విండో (అన్ని యాప్‌లు)" else "System Share Sheet (All Apps)",
                            color = BlackObsidian,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Direct WhatsApp Share
                    OutlinedButton(
                        onClick = {
                            showShareModal = false
                            viewModel.shareReportToWhatsApp(context, result, currentLang)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SilverBorder)
                    ) {
                        Text(if (isTelugu) "💬 వాట్సాప్ ద్వారా పంపండి" else "💬 Share to WhatsApp", color = SilverHighlight)
                    }

                    // Direct SMS Share
                    OutlinedButton(
                        onClick = {
                            showShareModal = false
                            viewModel.shareReportToSms(context, result, currentLang)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SilverBorder)
                    ) {
                        Text(if (isTelugu) "✉️ ఎస్‌ఎంఎస్ ద్వారా పంపండి" else "✉️ Share via SMS", color = SilverHighlight)
                    }

                    // Copy Full Report Text
                    OutlinedButton(
                        onClick = {
                            showShareModal = false
                            viewModel.copyReportToClipboard(context, result, currentLang)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SilverBorder)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = SilverHighlight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isTelugu) "📋 రిపోర్ట్‌ను కాపీ చేయండి" else "📋 Copy Report Text", color = SilverHighlight)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showShareModal = false }) {
                    Text(if (isTelugu) "రద్దు చేయి" else "Close", color = SilverSteel)
                }
            }
        )
    }

    val (riskColor, riskBg) = when (result.riskLevel) {
        RiskLevel.HIGH -> RiskHighRed to RiskHighBgDark
        RiskLevel.MEDIUM -> RiskMediumAmber to RiskMediumBgDark
        RiskLevel.LOW -> RiskLowTeal to RiskLowBgDark
        RiskLevel.UNCERTAIN -> RiskUncertainSlate to RiskUncertainBgDark
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("result_detail_screen")
            .background(BlackObsidian)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Navigation Top Bar with 3D Chrome accents
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(4.dp, RoundedCornerShape(6.dp))
                            .background(BlackCarbon, RoundedCornerShape(6.dp))
                            .border(1.dp, SilverBorder, RoundedCornerShape(6.dp))
                            .testTag("back_to_dashboard_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SilverPure
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isTelugu) "విశ్లేషణ నివేదిక" else "Threat Forensic Dossier",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SilverPure
                        )
                        Text(
                            text = if (result.isSample)
                                (if (isTelugu) "నమూనా పరిశోధన" else "Benchmark Case Study")
                            else
                                (if (isTelugu) "లైవ్ సెన్సార్ నిర్ధారణ" else "Live Engine Verification"),
                            style = MaterialTheme.typography.labelSmall,
                            color = SilverChrome
                        )
                    }
                }

                // 3D Bilingual Toggle Switch
                Box(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(BlackCarbon)
                        .border(1.dp, SilverBorder, RoundedCornerShape(8.dp))
                        .padding(2.dp)
                ) {
                    Row {
                        Box(
                            modifier = Modifier
                                .clickable { viewModel.toggleLanguage("EN") }
                                .background(
                                    if (!isTelugu) Brush.verticalGradient(listOf(SilverPure, SilverMetallic)) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "EN",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
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
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "తెలుగు",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isTelugu) BlackObsidian else SilverSteel
                            )
                        }
                    }
                }
            }
        }

        // Hero 3D Threat Dial Card
        item {
            ThreeDCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = 8.dp,
                borderGleamColor = riskColor
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(riskColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .border(1.dp, riskColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = result.riskLevel.getDisplayName(currentLang).uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black,
                                color = riskColor
                            )
                        }

                        Text(
                            text = SimpleDateFormat("MMM d, HH:mm", Locale.US).format(Date(result.timestamp)),
                            style = MaterialTheme.typography.labelSmall,
                            color = SilverSteel
                        )
                    }

                    // 3D Threat Gauge Visualizer
                    ThreatGauge(
                        score = result.heuristicScore,
                        riskLevel = result.riskLevel
                    )

                    Text(
                        text = result.summary,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = SilverPure,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Text(
                        text = if (isTelugu)
                            "సాంకేతిక ఆధారాలు, భాషా సంకేతాల ఆధారంగా రూపొందించిన సూచిక. తక్కువ ప్రమాదం అంటే సంపూర్ణ భరోసా కాదు."
                        else
                            "Heuristic indicator derived from combined lexical, syntactic, and registry signals. Low risk is never a guarantee.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = SilverSteel,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // 3D SHARE SUITE
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { showShareModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SilverPure),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(8.dp, RoundedCornerShape(8.dp), ambientColor = Color(0x60FFFFFF), spotColor = Color(0x30FFFFFF))
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                        .testTag("share_report_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = BlackObsidian,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTelugu) "ఈ మోసం హెచ్చరికను షేర్ చేయండి (WhatsApp / SMS)" else "Share Scam Alert (WhatsApp / SMS)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = BlackObsidian
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Direct WhatsApp Share Button
                    OutlinedButton(
                        onClick = { viewModel.shareReportToWhatsApp(context, result, currentLang) },
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SilverBorder),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("whatsapp_share_button")
                    ) {
                        Text(if (isTelugu) "వాట్సాప్" else "WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SilverHighlight)
                    }

                    // Copy Plaintext
                    OutlinedButton(
                        onClick = { viewModel.copyReportToClipboard(context, result, currentLang) },
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SilverBorder),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = SilverHighlight,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isTelugu) "కాపీ చేయి" else "Copy Text", fontSize = 12.sp, color = SilverHighlight)
                    }

                    // Report Inaccuracy
                    OutlinedButton(
                        onClick = { showFeedbackDialog = true },
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SilverBorder),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("report_incorrect_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Feedback,
                            contentDescription = null,
                            tint = SilverChrome,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isTelugu) "నివేదించు" else "Report Issue", fontSize = 12.sp, color = SilverChrome)
                    }
                }
            }
        }

        // 3D Plain Explanation Section with Telugu & English
        item {
            ThreeDCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                elevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isTelugu) "మోసం వివరాల విశ్లేషణ" else "Plain-Language Explanation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SilverPure
                        )
                        Text(
                            text = if (isTelugu) "తెలుగు అనువాదం" else "English Analysis",
                            style = MaterialTheme.typography.labelSmall,
                            color = SilverHighlight
                        )
                    }

                    Text(
                        text = if (isTelugu) result.plainExplanationTe else result.plainExplanationEn,
                        style = MaterialTheme.typography.bodyLarge,
                        color = SilverMetallic,
                        lineHeight = 22.sp
                    )

                    // Scanned input snippet
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BlackObsidian, RoundedCornerShape(6.dp))
                            .border(1.dp, SilverBorder, RoundedCornerShape(6.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "RAW SCANNED CONTENT (OTPs & Credentials Sanitized):",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = SilverSteel
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = result.originalInput,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.5.sp
                                ),
                                color = SilverHighlight
                            )
                        }
                    }
                }
            }
        }

        // Flagged Evidence Section
        if (result.flaggedPhrases.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTelugu) "గుర్తించిన ఆధారాలు & సంకేతాలు (${result.flaggedPhrases.size})" else "Flagged Forensic Evidence (${result.flaggedPhrases.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SilverPure
                    )
                    Text(
                        text = "Traceable Quotes",
                        style = MaterialTheme.typography.labelSmall,
                        color = SilverSteel
                    )
                }
            }

            items(result.flaggedPhrases) { phrase ->
                EvidenceCard(
                    flagged = phrase,
                    selectedLanguage = currentLang
                )
            }
        }

        // Sandboxed URL Inspection Card
        if (result.urlAnalysis != null) {
            item {
                UrlInspectionCard(analysis = result.urlAnalysis)
            }
        }

        // Safe Action Checklist
        item {
            ThreeDCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                elevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = TealSubtle,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isTelugu) "తీసుకోవలసిన తక్షణ రక్షణ చర్యలు:" else "Immediate Protective Actions:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SilverPure
                        )
                    }

                    val steps = if (isTelugu) result.safeNextStepsTe else result.safeNextSteps
                    steps.forEachIndexed { i, step ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(TealSubtle.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .border(0.5.dp, TealSubtle.copy(alpha = 0.5f), RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${i + 1}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TealSubtle
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyMedium,
                                color = SilverMetallic
                            )
                        }
                    }
                }
            }
        }

        // Check Another CTA
        item {
            OutlinedButton(
                onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SilverBorderGleam),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("check_another_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = SilverHighlight,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isTelugu) "మరొక సందేశాన్ని తనిఖీ చేయండి" else "Inspect Another Message or URL",
                    fontWeight = FontWeight.SemiBold,
                    color = SilverHighlight
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
