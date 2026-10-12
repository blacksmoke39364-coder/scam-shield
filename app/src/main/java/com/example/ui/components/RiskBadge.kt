package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RiskLevel
import com.example.ui.theme.BlackCarbon
import com.example.ui.theme.BlackObsidian
import com.example.ui.theme.RiskHighBgDark
import com.example.ui.theme.RiskHighRed
import com.example.ui.theme.RiskLowBgDark
import com.example.ui.theme.RiskLowTeal
import com.example.ui.theme.RiskMediumAmber
import com.example.ui.theme.RiskMediumBgDark
import com.example.ui.theme.RiskUncertainBgDark
import com.example.ui.theme.RiskUncertainSlate
import com.example.ui.theme.SilverBorder
import com.example.ui.theme.SilverChrome
import com.example.ui.theme.SilverHighlight
import com.example.ui.theme.SilverPure
import com.example.ui.theme.SilverSteel

@Composable
fun RiskBanner(
    riskLevel: RiskLevel,
    heuristicScore: Int,
    selectedLanguage: String,
    modifier: Modifier = Modifier
) {
    val (primaryColor, bgColor, icon, title, subtitle) = when (riskLevel) {
        RiskLevel.HIGH -> {
            val titleText = if (selectedLanguage == "TE") "అధిక ప్రమాదం (మోసం అవకాశం)" else "HIGH RISK DETECTED"
            val subText = if (selectedLanguage == "TE")
                "బలమైన మోసపూరిత సంకేతాలు కనిపించాయి. స్పందించవద్దు."
            else
                "Multiple critical scam indicators identified. Do not respond or click links."
            Tuple5(RiskHighRed, RiskHighBgDark, Icons.Default.Warning, titleText, subText)
        }
        RiskLevel.MEDIUM -> {
            val titleText = if (selectedLanguage == "TE") "మధ్యస్థ ప్రమాదం (జాగ్రత్త)" else "MEDIUM RISK (SUSPICIOUS)"
            val subText = if (selectedLanguage == "TE")
                "అనుమానాస్పద అంశాలు ఉన్నాయి. ధృవీకరించుకోండి."
            else
                "Suspicious traits detected. Proceed with caution and verify independently."
            Tuple5(RiskMediumAmber, RiskMediumBgDark, Icons.Default.Info, titleText, subText)
        }
        RiskLevel.UNCERTAIN -> {
            val titleText = if (selectedLanguage == "TE") "సందేహాస్పదం (ధృవీకరించండి)" else "UNCERTAIN / UNABLE TO VERIFY"
            val subText = if (selectedLanguage == "TE")
                "తగినంత ఆధారాలు లేవు. పంపిన వ్యక్తి ఎవరో తెలుసుకోండి."
            else
                "Insufficient context to determine threat level conclusively. Verify sender."
            Tuple5(RiskUncertainSlate, RiskUncertainBgDark, Icons.AutoMirrored.Filled.Help, titleText, subText)
        }
        RiskLevel.LOW -> {
            val titleText = if (selectedLanguage == "TE") "తక్కువ ప్రమాదం" else "LOW RISK NOTED"
            val subText = if (selectedLanguage == "TE")
                "ప్రత్యక్ష ముప్పు సంకేతాలు లేవు. గమనిక: ఇది 100% గ్యారెంటీ కాదు."
            else
                "No direct scam patterns identified. Note: Low risk is not a guarantee."
            Tuple5(RiskLowTeal, RiskLowBgDark, Icons.Default.CheckCircle, titleText, subText)
        }
    }

    ThreeDCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("risk_banner"),
        shape = RoundedCornerShape(10.dp),
        elevation = 6.dp,
        borderGleamColor = primaryColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(bgColor, RoundedCornerShape(8.dp))
                    .border(1.dp, primaryColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = primaryColor
                    )

                    Box(
                        modifier = Modifier
                            .background(BlackCarbon, RoundedCornerShape(4.dp))
                            .border(0.5.dp, SilverBorder, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$heuristicScore/100",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = SilverPure
                        )
                    }
                }

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = SilverChrome
                )
            }
        }
    }
}

private data class Tuple5<A, B, C, D, E>(
    val a: A,
    val b: B,
    val c: C,
    val d: D,
    val e: E
)
