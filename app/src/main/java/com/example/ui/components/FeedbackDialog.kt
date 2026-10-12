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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.BlackCarbon
import com.example.ui.theme.BlackObsidian
import com.example.ui.theme.SilverBorder
import com.example.ui.theme.SilverBorderGleam
import com.example.ui.theme.SilverHighlight
import com.example.ui.theme.SilverMetallic
import com.example.ui.theme.SilverPure
import com.example.ui.theme.SilverSteel

@Composable
fun FeedbackDialog(
    resultId: String,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    val options = listOf(
        "Legitimate message mistakenly flagged as scam (False positive)",
        "Actual scam was marked as low risk (False negative)",
        "Flagged phrase or explanation was inaccurate",
        "Telugu translation needs improvement",
        "Other pattern observation"
    )

    var selectedOption by remember { mutableStateOf(options[0]) }

    Dialog(onDismissRequest = onDismiss) {
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
                .padding(18.dp)
                .testTag("feedback_dialog")
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Report Forensic Anomaly",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SilverPure
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SilverSteel
                        )
                    }
                }

                Text(
                    text = "Your telemetry feedback directly trains our local rule engines and model prompts to eliminate false positives and catch emerging scam patterns:",
                    style = MaterialTheme.typography.bodySmall,
                    color = SilverMetallic
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    options.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedOption = option }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedOption == option,
                                onClick = { selectedOption = option },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = SilverPure,
                                    unselectedColor = SilverSteel
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (selectedOption == option) SilverPure else SilverMetallic
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SilverBorder)
                    ) {
                        Text("Cancel", color = SilverSteel)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSubmit(selectedOption) },
                        colors = ButtonDefaults.buttonColors(containerColor = SilverPure),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.shadow(4.dp, RoundedCornerShape(6.dp))
                    ) {
                        Text("Submit Report", color = BlackObsidian, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
