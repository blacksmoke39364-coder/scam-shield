package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.BlackCarbon
import com.example.ui.theme.BlackElevated
import com.example.ui.theme.BlackObsidian
import com.example.ui.theme.SilverBorder
import com.example.ui.theme.SilverBorderGleam
import com.example.ui.theme.SilverChrome
import com.example.ui.theme.SilverHighlight
import com.example.ui.theme.SilverMetallic
import com.example.ui.theme.SilverPure
import com.example.ui.theme.SilverSteel

@Composable
fun OnboardingTermsDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onAccept: () -> Unit
) {
    var acknowledgedEstimate by remember { mutableStateOf(false) }
    var acknowledgedDataFlow by remember { mutableStateOf(false) }
    var selectedLang by remember { mutableStateOf(currentLanguage) }

    Dialog(onDismissRequest = { /* mandatory on first launch */ }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(12.dp))
                .background(BlackCarbon, RoundedCornerShape(12.dp))
                .border(
                    1.dp,
                    Brush.verticalGradient(listOf(SilverPure, SilverBorderGleam, SilverBorder)),
                    RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
                .testTag("onboarding_terms_dialog")
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Brush.linearGradient(listOf(SilverPure, SilverMetallic)), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = BlackObsidian,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ScamShield AI Setup",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SilverPure
                        )
                        Text(
                            text = "Think Before You Click • v1.0.0 Titanium",
                            style = MaterialTheme.typography.labelSmall,
                            color = SilverChrome
                        )
                    }
                }

                // 1. Language Choice in 3D Box
                ThreeDCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = SilverPure,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Select Operating Language / భాషను ఎంచుకోండి:",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = SilverPure
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLang = "EN"
                                    onLanguageSelected("EN")
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedLang == "EN",
                                onClick = {
                                    selectedLang = "EN"
                                    onLanguageSelected("EN")
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = SilverPure, unselectedColor = SilverSteel)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("English (Primary)", style = MaterialTheme.typography.bodyMedium, color = SilverPure)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLang = "TE"
                                    onLanguageSelected("TE")
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedLang == "TE",
                                onClick = {
                                    selectedLang = "TE"
                                    onLanguageSelected("TE")
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = SilverPure, unselectedColor = SilverSteel)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("తెలుగు (Telugu Forensic Support)", style = MaterialTheme.typography.bodyMedium, color = SilverPure)
                        }
                    }
                }

                // 2. Risk Estimates Acknowledgment
                ThreeDCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = SilverPure,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Important Operational Disclaimers:",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = SilverPure
                            )
                        }

                        Text(
                            text = "• All forensic scores and risk classifications are automated heuristic estimates.\n• A 'LOW RISK' rating is not a guarantee of safety. Zero-day threats occur daily.\n• Redacted message content is transmitted to Gemini LLM and Google Safe Browsing APIs to analyze technical indicators.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SilverMetallic,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Checkboxes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { acknowledgedEstimate = !acknowledgedEstimate },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = acknowledgedEstimate,
                        onCheckedChange = { acknowledgedEstimate = it },
                        colors = CheckboxDefaults.colors(checkedColor = SilverPure, checkmarkColor = BlackObsidian, uncheckedColor = SilverSteel)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "I understand that results are risk indicators, not guarantees of legitimacy.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SilverPure
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { acknowledgedDataFlow = !acknowledgedDataFlow },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = acknowledgedDataFlow,
                        onCheckedChange = { acknowledgedDataFlow = it },
                        colors = CheckboxDefaults.colors(checkedColor = SilverPure, checkmarkColor = BlackObsidian, uncheckedColor = SilverSteel)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "I accept the Terms of Service & Privacy Policy (v1.0.0).",
                        style = MaterialTheme.typography.bodySmall,
                        color = SilverPure
                    )
                }

                // Accept 3D Button
                Button(
                    onClick = onAccept,
                    enabled = acknowledgedEstimate && acknowledgedDataFlow,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SilverPure,
                        disabledContainerColor = SilverBorder
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(6.dp, RoundedCornerShape(8.dp), ambientColor = Color(0x60FFFFFF))
                        .testTag("accept_terms_button")
                ) {
                    Text(
                        text = "Get Started with ScamShield AI",
                        color = BlackObsidian,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
