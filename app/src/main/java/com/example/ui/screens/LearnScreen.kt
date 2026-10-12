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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScamPattern
import com.example.data.samples.LearnPatterns
import com.example.ui.components.ThreeDCard
import com.example.ui.theme.BlackCarbon
import com.example.ui.theme.BlackElevated
import com.example.ui.theme.BlackObsidian
import com.example.ui.theme.RiskHighRed
import com.example.ui.theme.RiskMediumAmber
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

@Composable
fun LearnScreen(
    viewModel: ScamShieldViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isTelugu = uiState.selectedLanguage == "TE"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("learn_screen")
            .background(BlackObsidian)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // 3D Header with Silver Gleam
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .shadow(4.dp, RoundedCornerShape(8.dp))
                                .background(Brush.verticalGradient(listOf(SilverPure, SilverMetallic)), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = BlackObsidian,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isTelugu) "మోసాల గుర్తింపు (Learn)" else "Pattern Library",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SilverPure
                        )
                    }

                    Text(
                        text = if (isTelugu)
                            "మోసగాళ్ల ఎత్తుగడలు మరియు వాస్తవ నమూనాల విశ్లేషణ."
                        else
                            "Deconstructed anatomy of common fraud campaigns.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SilverChrome
                    )
                }

                // 3D Bilingual Toggle Switch
                Box(
                    modifier = Modifier
                        .shadow(3.dp, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(BlackCarbon)
                        .border(1.dp, SilverBorder, RoundedCornerShape(8.dp))
                        .padding(2.dp)
                ) {
                    Row {
                        Box(
                            modifier = Modifier
                                .clickable { viewModel.toggleLanguage("EN") }
                                .background(if (!isTelugu) Brush.verticalGradient(listOf(SilverPure, SilverMetallic)) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)), RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text("EN", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (!isTelugu) BlackObsidian else SilverSteel)
                        }
                        Box(
                            modifier = Modifier
                                .clickable { viewModel.toggleLanguage("TE") }
                                .background(if (isTelugu) Brush.verticalGradient(listOf(SilverPure, SilverMetallic)) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)), RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text("తెలుగు", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (isTelugu) BlackObsidian else SilverSteel)
                        }
                    }
                }
            }
        }

        items(LearnPatterns.PATTERNS) { pattern ->
            PatternCard3D(
                pattern = pattern,
                isTelugu = isTelugu,
                onTryExample = {
                    viewModel.setInputText(pattern.realWorldExample)
                    viewModel.navigateTo(AppScreen.DASHBOARD)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun PatternCard3D(
    pattern: ScamPattern,
    isTelugu: Boolean,
    onTryExample: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ThreeDCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(BlackObsidian, RoundedCornerShape(4.dp))
                        .border(0.5.dp, SilverBorder, RoundedCornerShape(4.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = pattern.targetChannel.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = SilverHighlight,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { expanded = !expanded }
                        .padding(4.dp)
                ) {
                    Text(
                        text = if (expanded) "Show Less" else "Deep Dive",
                        style = MaterialTheme.typography.labelSmall,
                        color = SilverPure,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text = pattern.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SilverPure
            )

            Text(
                text = pattern.description,
                style = MaterialTheme.typography.bodyMedium,
                color = SilverMetallic
            )

            // Real-World Attack Snippet in 3D Inset Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BlackObsidian, RoundedCornerShape(6.dp))
                    .border(1.dp, SilverBorder, RoundedCornerShape(6.dp))
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "REAL-WORLD FRAUD SMISHING SAMPLE:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = SilverSteel
                    )
                    Text(
                        text = pattern.realWorldExample,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.5.sp
                        ),
                        color = SilverPure
                    )
                }
            }

            // Expanded Breakdown
            if (expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Psychological Lever
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = RiskMediumAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "PSYCHOLOGICAL TRIGGER USED:",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                color = RiskMediumAmber
                            )
                            Text(
                                text = pattern.psychologicalLever,
                                style = MaterialTheme.typography.bodySmall,
                                color = SilverMetallic
                            )
                        }
                    }

                    // Key Red Flags
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = "CRITICAL RED FLAGS:",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = RiskHighRed
                        )
                        pattern.redFlags.forEach { flag ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = RiskHighRed,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = flag,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SilverMetallic
                                )
                            }
                        }
                    }

                    // Safe Countermeasures
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = "SAFE COUNTERMEASURES:",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = TealSubtle
                        )
                        pattern.safeCountermeasures.forEach { measure ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = TealSubtle,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = measure,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SilverMetallic
                                )
                            }
                        }
                    }
                }
            }

            // Test This Pattern in Scanner 3D Button
            OutlinedButton(
                onClick = onTryExample,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SilverBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = SilverHighlight,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isTelugu) "ఈ నమూనాను స్కానర్‌లో పరీక్షించండి" else "Load This Pattern Into Scanner",
                    fontSize = 12.sp,
                    color = SilverHighlight,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
