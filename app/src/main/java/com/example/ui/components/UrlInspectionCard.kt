package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UrlAnalysisResult
import com.example.ui.theme.BlackCarbon
import com.example.ui.theme.BlackObsidian
import com.example.ui.theme.RiskHighRed
import com.example.ui.theme.RiskMediumAmber
import com.example.ui.theme.SilverBorder
import com.example.ui.theme.SilverChrome
import com.example.ui.theme.SilverHighlight
import com.example.ui.theme.SilverMetallic
import com.example.ui.theme.SilverPure
import com.example.ui.theme.SilverSteel
import com.example.ui.theme.TealSubtle

@Composable
fun UrlInspectionCard(
    analysis: UrlAnalysisResult,
    modifier: Modifier = Modifier
) {
    ThreeDCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("url_inspection_card"),
        shape = RoundedCornerShape(8.dp),
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = SilverPure,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GOOGLE SAFE BROWSING & URL FORENSICS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = SilverPure
                    )
                }

                Text(
                    text = "Google API v4 Sandboxed",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = SilverSteel
                )
            }

            // Sanitized Domain
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BlackObsidian, RoundedCornerShape(6.dp))
                    .border(1.dp, SilverBorder, RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = "Extracted Hostname:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = SilverSteel
                    )
                    Text(
                        text = analysis.domain,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = SilverPure
                    )
                }
            }

            // Reputation Status Box
            val isThreatDetected = analysis.reputationStatus.contains("Flagged", ignoreCase = true) ||
                    analysis.reputationStatus.contains("High Risk", ignoreCase = true)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isThreatDetected) RiskHighRed.copy(alpha = 0.15f) else BlackCarbon,
                        RoundedCornerShape(6.dp)
                    )
                    .border(
                        1.dp,
                        if (isThreatDetected) RiskHighRed.copy(alpha = 0.5f) else SilverBorder,
                        RoundedCornerShape(6.dp)
                    )
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = if (isThreatDetected) Icons.Default.Warning else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (isThreatDetected) RiskHighRed else TealSubtle,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = analysis.reputationStatus,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isThreatDetected) RiskHighRed else SilverPure
                        )
                        Text(
                            text = "Checked: ${analysis.timestampChecked}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = SilverSteel
                        )
                    }
                }
            }

            // Technical Traits Grid
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "TECHNICAL URL TRAITS:",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = SilverChrome
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TraitBadge(
                        label = "Raw IP Host",
                        flagged = analysis.isIpAddress,
                        modifier = Modifier.weight(1f)
                    )
                    TraitBadge(
                        label = "Punycode/Homograph",
                        flagged = analysis.hasPunycode,
                        modifier = Modifier.weight(1.3f)
                    )
                    TraitBadge(
                        label = "Shortener",
                        flagged = analysis.isShortener,
                        modifier = Modifier.weight(1f)
                    )
                    TraitBadge(
                        label = "Abuse TLD",
                        flagged = analysis.suspiciousTld,
                        modifier = Modifier.weight(1.1f)
                    )
                }
            }

            // Security Policy Note
            Text(
                text = analysis.securityNote,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = SilverSteel
            )
        }
    }
}

@Composable
private fun TraitBadge(
    label: String,
    flagged: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (flagged) RiskHighRed.copy(alpha = 0.15f) else BlackObsidian
    val borderColor = if (flagged) RiskHighRed.copy(alpha = 0.5f) else SilverBorder
    val textColor = if (flagged) RiskHighRed else SilverChrome

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(4.dp))
            .border(0.5.dp, borderColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$label: ${if (flagged) "YES" else "NO"}",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
            color = textColor,
            maxLines = 1
        )
    }
}
