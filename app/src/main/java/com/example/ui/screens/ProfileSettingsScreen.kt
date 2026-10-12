package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.service.GeminiAnalyzer
import com.example.ui.components.ThreeDCard
import com.example.ui.theme.BlackCarbon
import com.example.ui.theme.BlackElevated
import com.example.ui.theme.BlackObsidian
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.RiskHighRed
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
fun ProfileSettingsScreen(
    viewModel: ScamShieldViewModel,
    modifier: Modifier = Modifier
) {
    val userSettings by viewModel.userSettings.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val totalCount by viewModel.totalAnalysesCount.collectAsState()
    val context = LocalContext.current
    val isTelugu = uiState.selectedLanguage == "TE"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_settings_screen")
            .background(BlackObsidian)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .background(Brush.verticalGradient(listOf(SilverPure, SilverMetallic)), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = BlackObsidian,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isTelugu) "సెట్టింగ్స్ & ప్రొఫైల్" else "Profile & Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SilverPure
                )
            }
            Text(
                text = if (isTelugu) "గోప్యతా నియంత్రణలు, భాష మరియు భద్రతా ఇంజిన్ స్థితి." else "Privacy controls, language preferences, and engine status.",
                style = MaterialTheme.typography.bodySmall,
                color = SilverChrome
            )
        }

        // Section: Language Preferences
        item {
            ThreeDCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = SilverPure,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Primary Explanation Language",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SilverPure
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.updateLanguagePreference("EN") }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = userSettings.preferredLanguage == "EN",
                            onClick = { viewModel.updateLanguagePreference("EN") },
                            colors = RadioButtonDefaults.colors(selectedColor = SilverPure, unselectedColor = SilverSteel)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("English", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = SilverPure)
                            Text("Plain-English forensic analysis and safe actions", style = MaterialTheme.typography.bodySmall, color = SilverChrome)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.updateLanguagePreference("TE") }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = userSettings.preferredLanguage == "TE",
                            onClick = { viewModel.updateLanguagePreference("TE") },
                            colors = RadioButtonDefaults.colors(selectedColor = SilverPure, unselectedColor = SilverSteel)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("తెలుగు (Telugu)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = SilverPure)
                            Text("తెలుగులో సమగ్ర విశ్లేషణ మరియు రక్షణ చర్యలు", style = MaterialTheme.typography.bodySmall, color = SilverChrome)
                        }
                    }
                }
            }
        }

        // Section: Privacy & Data Retention
        item {
            ThreeDCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = SilverPure,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Privacy & Local Data Storage",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SilverPure
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Store Full Message Text in Local History",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = SilverPure
                            )
                            Text(
                                text = if (userSettings.storeFullMessages)
                                    "Active: Full original message content saved on device."
                                else
                                    "Default: Zero full messages stored. Only scores & summaries preserved.",
                                style = MaterialTheme.typography.bodySmall,
                                color = SilverChrome
                            )
                        }

                        Switch(
                            checked = userSettings.storeFullMessages,
                            onCheckedChange = { viewModel.updateStoreFullMessagesPreference(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = BlackObsidian,
                                checkedTrackColor = SilverPure,
                                uncheckedThumbColor = SilverSteel,
                                uncheckedTrackColor = BlackCarbon
                            )
                        )
                    }
                }
            }
        }

        // Section: Data Export & Legal
        item {
            ThreeDCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = SilverPure,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Data Management & Legal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SilverPure
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.exportHistoryToCsv(context) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SilverBorder)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = SilverHighlight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export Analysis Records (CSV)", color = SilverHighlight)
                    }

                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.TERMS_PRIVACY) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SilverBorder)
                    ) {
                        Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = SilverHighlight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Review Terms of Service & Privacy Policy", color = SilverHighlight)
                    }

                    OutlinedButton(
                        onClick = { viewModel.clearAllHistory() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RiskHighRed),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RiskHighRed.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Delete All Local History", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
