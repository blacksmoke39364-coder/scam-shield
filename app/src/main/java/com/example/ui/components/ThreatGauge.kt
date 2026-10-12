package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RiskLevel
import com.example.ui.theme.BlackCarbon
import com.example.ui.theme.BlackObsidian
import com.example.ui.theme.RiskHighRed
import com.example.ui.theme.RiskLowTeal
import com.example.ui.theme.RiskMediumAmber
import com.example.ui.theme.RiskUncertainSlate
import com.example.ui.theme.SilverBorder
import com.example.ui.theme.SilverChrome
import com.example.ui.theme.SilverMetallic
import com.example.ui.theme.SilverPure
import com.example.ui.theme.SilverSteel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ThreatGauge(
    score: Int,
    riskLevel: RiskLevel,
    modifier: Modifier = Modifier
) {
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(durationMillis = 900),
        label = "threatScore"
    )

    val gaugeColor = when (riskLevel) {
        RiskLevel.HIGH -> RiskHighRed
        RiskLevel.MEDIUM -> RiskMediumAmber
        RiskLevel.LOW -> RiskLowTeal
        RiskLevel.UNCERTAIN -> RiskUncertainSlate
    }

    Box(
        modifier = modifier.size(180.dp, 125.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(170.dp, 115.dp)) {
            val strokeWidth = 14.dp.toPx()
            val arcSize = Size(size.width - strokeWidth, size.height * 1.6f)
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

            // 3D Outer Track Shadow Arc
            drawArc(
                color = Color(0xFF030508),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth + 4.dp.toPx(), cap = StrokeCap.Round)
            )

            // 3D Metallic Chrome Bezel Background Track (180 degrees)
            drawArc(
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFF2D3748),
                        Color(0xFF1A202C),
                        Color(0xFF0F172A)
                    )
                ),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Segmented active threat sweep
            val sweep = (animatedScore / 100f).coerceIn(0.02f, 1f) * 180f

            drawArc(
                brush = Brush.linearGradient(
                    listOf(
                        SilverMetallic,
                        gaugeColor,
                        gaugeColor
                    )
                ),
                startAngle = 180f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 3D Metallic Specular Highlight Rim on Sweep
            drawArc(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0x90FFFFFF),
                        Color.Transparent
                    )
                ),
                startAngle = 180f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw indicator chrome pin at the end of sweep
            val angleRad = Math.toRadians((180 + sweep).toDouble())
            val centerX = size.width / 2
            val centerY = arcSize.height / 2 + topLeft.y
            val radius = (arcSize.width) / 2
            val tickX = centerX + (radius * cos(angleRad)).toFloat()
            val tickY = centerY + (radius * sin(angleRad)).toFloat()

            // Pin outer silver ring
            drawCircle(
                color = SilverPure,
                radius = 7.dp.toPx(),
                center = Offset(tickX, tickY)
            )
            // Pin inner gauge color jewel
            drawCircle(
                color = gaugeColor,
                radius = 4.5.dp.toPx(),
                center = Offset(tickX, tickY)
            )
        }

        // Center Score Text with 3D Titanium Typography
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(
                text = "${animatedScore.toInt()}",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                ),
                color = SilverPure
            )
            Text(
                text = "THREAT INDEX / 100",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = SilverSteel
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
