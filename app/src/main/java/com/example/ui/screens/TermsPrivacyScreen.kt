package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ThreeDCard
import com.example.ui.theme.BlackCarbon
import com.example.ui.theme.BlackObsidian
import com.example.ui.theme.RiskMediumAmber
import com.example.ui.theme.SilverBorder
import com.example.ui.theme.SilverChrome
import com.example.ui.theme.SilverHighlight
import com.example.ui.theme.SilverMetallic
import com.example.ui.theme.SilverPure
import com.example.ui.theme.SilverSteel
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ScamShieldViewModel

@Composable
fun TermsPrivacyScreen(
    viewModel: ScamShieldViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(AppScreen.SETTINGS_PROFILE)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("terms_privacy_screen")
            .background(BlackObsidian)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.SETTINGS_PROFILE) },
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(4.dp, RoundedCornerShape(6.dp))
                        .background(BlackCarbon, RoundedCornerShape(6.dp))
                        .border(1.dp, SilverBorder, RoundedCornerShape(6.dp))
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
                        text = "Terms & Privacy Policy",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SilverPure
                    )
                    Text(
                        text = "Real data flows, legal disclaimers, and support contacts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SilverChrome
                    )
                }
            }
        }

        // Legal Notice Alert
        item {
            ThreeDCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = RiskMediumAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "PRE-PRODUCTION POLICY NOTICE",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = RiskMediumAmber
                        )
                        Text(
                            text = "This document accurately reflects the current application architecture and external API transmissions. It serves as an engineering draft requiring formal legal counsel before enterprise publication.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SilverMetallic
                        )
                    }
                }
            }
        }

        // Section: Terms of Service
        item {
            ThreeDCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = null,
                            tint = SilverPure,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "1. Terms of Service & Risk Disclaimers",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SilverPure
                        )
                    }

                    Text(
                        text = "ScamShield AI provides heuristic evaluations and linguistic analysis to help users identify deceptive SMS messages, emails, UPI fraud demands, and URLs. All scores, risk classifications, and forensic flags are automated risk estimates derived from pre-trained models and heuristic algorithms.\n\nA rating of 'LOW RISK' is NOT a guarantee of authenticity or safety. Attackers rapidly evolve tactics, register zero-day domains, and impersonate legitimate contacts. Users must always verify transactions independently with official providers before taking action.\n\nScamShield AI does not act as a financial advisor, law enforcement agency, or legal authority.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SilverMetallic,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Section: Privacy Policy & Data Architecture
        item {
            ThreeDCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = SilverPure,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2. Real Data Architecture & Transmission",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SilverPure
                        )
                    }

                    Text(
                        text = "We adhere to strict privacy-by-design principles:\n\n• Client-Side Redaction: Before any text is evaluated externally, on-device regex engines scrub known 16-digit payment card numbers, 4-8 digit OTPs, and sensitive PINs.\n\n• External APIs: When live analysis is performed, the scrubbed message text is transmitted over TLS to Gemini API endpoints, and extracted URLs are verified against Google Safe Browsing API v4. We never execute links or load external webpage scripts.\n\n• Local Storage: By default, only heuristic scores, categories, and brief summaries are stored locally in the on-device SQLite database. Full message storage is strictly opt-in and disabled by default.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SilverMetallic,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Section: Contact & Support
        item {
            ThreeDCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Mail,
                            contentDescription = null,
                            tint = SilverPure,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3. Engineering Contact & Support",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SilverPure
                        )
                    }

                    Text(
                        text = "For security vulnerabilities, false positive dispute reports, or enterprise inquiries:\n\n• Security Team: security@scamshield.ai\n• Support Portal: support@scamshield.ai\n• Engineering Team: ScamShield AI Core Team\n\nVersion: 1.0.0-Titanium (3D Silver Edition)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SilverMetallic,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
