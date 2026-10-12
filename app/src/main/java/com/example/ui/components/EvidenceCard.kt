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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.WarningAmber
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
import com.example.data.model.FlaggedPhrase
import com.example.ui.theme.BlackCarbon
import com.example.ui.theme.BlackObsidian
import com.example.ui.theme.RiskHighRed
import com.example.ui.theme.SilverBorder
import com.example.ui.theme.SilverChrome
import com.example.ui.theme.SilverHighlight
import com.example.ui.theme.SilverMetallic
import com.example.ui.theme.SilverPure
import com.example.ui.theme.SilverSteel
import com.example.ui.theme.TealSubtle

@Composable
fun EvidenceCard(
    flagged: FlaggedPhrase,
    selectedLanguage: String,
    modifier: Modifier = Modifier
) {
    val isTelugu = selectedLanguage == "TE"
    val whyText = if (isTelugu && flagged.whyItMattersTe.isNotBlank()) flagged.whyItMattersTe else flagged.whyItMatters
    val safeStepText = if (isTelugu && flagged.safeNextStepTe.isNotBlank()) flagged.safeNextStepTe else flagged.safeNextStep

    ThreeDCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("evidence_card"),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Category Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(RiskHighRed.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .border(1.dp, RiskHighRed.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = flagged.category.uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp),
                        color = RiskHighRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = RiskHighRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Forensic Evidence",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = SilverSteel
                    )
                }
            }

            // Quoted Phrase in 3D Inset Surface
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BlackObsidian, RoundedCornerShape(6.dp))
                    .border(1.dp, SilverBorder, RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "\"${flagged.phrase}\"",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp
                    ),
                    color = SilverPure
                )
            }

            // Why It Matters
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = if (isTelugu) "ఇది ఎందుకు ప్రమాదకరం:" else "WHY IT MATTERS:",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = SilverChrome
                )
                Text(
                    text = whyText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SilverMetallic
                )
            }

            // Safe Next Step
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BlackCarbon, RoundedCornerShape(6.dp))
                    .border(0.5.dp, TealSubtle.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = TealSubtle,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = if (isTelugu) "సురక్షిత చర్య:" else "SAFE ACTION:",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = TealSubtle
                        )
                        Text(
                            text = safeStepText,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = SilverHighlight
                        )
                    }
                }
            }
        }
    }
}
