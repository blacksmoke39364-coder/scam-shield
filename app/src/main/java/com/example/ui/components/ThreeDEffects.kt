package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BlackCarbon
import com.example.ui.theme.BlackElevated
import com.example.ui.theme.BlackObsidian
import com.example.ui.theme.SilverBorder
import com.example.ui.theme.SilverBorderGleam

/**
 * Renders a Premium 3D Metallic Card container with specular top highlight,
 * layered depth shadow, and bevel rim.
 */
@Composable
fun ThreeDCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
    elevation: Dp = 6.dp,
    borderGleamColor: Color = SilverBorderGleam,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color(0x60000000),
                spotColor = Color(0x90000000)
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF161F2E),
                        Color(0xFF0F141F),
                        Color(0xFF080C14)
                    )
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        Color(0x80FFFFFF),
                        borderGleamColor.copy(alpha = 0.5f),
                        SilverBorder.copy(alpha = 0.3f)
                    )
                ),
                shape
            )
    ) {
        // Specular top highlight glow
        Box(
            modifier = Modifier
                .matchParentSize()
                .border(
                    0.5.dp,
                    Brush.verticalGradient(
                        listOf(
                            Color(0x25FFFFFF),
                            Color.Transparent
                        )
                    ),
                    shape
                )
        )
        content()
    }
}

/**
 * Renders a 3D Raised Metallic Button container with tactile bevel and specular sheen.
 */
@Composable
fun ThreeDButtonSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    isPrimary: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val backgroundBrush = if (isPrimary) {
        Brush.verticalGradient(
            listOf(
                Color(0xFFFFFFFF),
                Color(0xFFE2E8F0),
                Color(0xFFCBD5E1),
                Color(0xFF94A3B8)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFF1F293D),
                Color(0xFF141C2B),
                Color(0xFF0D121D)
            )
        )
    }

    val topBorderColor = if (isPrimary) Color(0xFFFFFFFF) else Color(0x60FFFFFF)
    val bottomBorderColor = if (isPrimary) Color(0xFF64748B) else Color(0xFF000000)

    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = shape,
                ambientColor = Color(0x50000000),
                spotColor = Color(0x80000000)
            )
            .clip(shape)
            .background(backgroundBrush)
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(topBorderColor, bottomBorderColor)
                ),
                shape
            )
    ) {
        content()
    }
}
