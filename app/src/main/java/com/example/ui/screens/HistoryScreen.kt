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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AnalysisEntity
import com.example.data.model.RiskLevel
import com.example.ui.components.ThreeDCard
import com.example.ui.theme.BlackCarbon
import com.example.ui.theme.BlackElevated
import com.example.ui.theme.BlackObsidian
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.RiskHighRed
import com.example.ui.theme.RiskLowTeal
import com.example.ui.theme.RiskMediumAmber
import com.example.ui.theme.SilverBorder
import com.example.ui.theme.SilverBorderGleam
import com.example.ui.theme.SilverChrome
import com.example.ui.theme.SilverDark
import com.example.ui.theme.SilverHighlight
import com.example.ui.theme.SilverMetallic
import com.example.ui.theme.SilverPure
import com.example.ui.theme.SilverSteel
import com.example.ui.theme.TealSubtle
import com.example.ui.viewmodel.ScamShieldViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: ScamShieldViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredHistory by viewModel.filteredHistory.collectAsState()
    val totalCount by viewModel.totalAnalysesCount.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val context = LocalContext.current

    val isTelugu = uiState.selectedLanguage == "TE"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("history_screen")
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
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .shadow(4.dp, RoundedCornerShape(8.dp))
                                .background(Brush.verticalGradient(listOf(SilverPure, SilverMetallic)), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = BlackObsidian,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isTelugu) "విశ్లేషణల చరిత్ర" else "Analysis History",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SilverPure
                        )
                    }
                    Text(
                        text = if (isTelugu) "$totalCount విశ్లేషణలు భద్రపరచబడ్డాయి" else "$totalCount analyses recorded locally",
                        style = MaterialTheme.typography.bodySmall,
                        color = SilverChrome
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Bilingual Toggle
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

                    // Export to CSV 3D button
                    OutlinedButton(
                        onClick = { viewModel.exportHistoryToCsv(context) },
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SilverBorder),
                        modifier = Modifier.testTag("export_csv_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = SilverHighlight,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isTelugu) "CSV" else "CSV", fontSize = 11.sp, color = SilverHighlight)
                    }
                }
            }
        }

        // Search bar in 3D Inset Container
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BlackCarbon, RoundedCornerShape(8.dp))
                    .border(1.dp, SilverBorder, RoundedCornerShape(8.dp))
            ) {
                OutlinedTextField(
                    value = uiState.historySearchQuery,
                    onValueChange = { viewModel.setHistorySearchQuery(it) },
                    placeholder = {
                        Text(
                            text = if (isTelugu) "కీవర్డ్ లేదా సంస్థ పేరుతో వెతకండి..." else "Search in history by keyword or entity...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SilverSteel
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = SilverChrome
                        )
                    },
                    trailingIcon = {
                        if (uiState.historySearchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.setHistorySearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = SilverSteel
                                )
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("history_search_input"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SilverPure,
                        unfocusedTextColor = SilverHighlight,
                        focusedContainerColor = BlackCarbon,
                        unfocusedContainerColor = BlackCarbon,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        }

        // Filter chips: All, High Risk, Medium, Low, Uncertain
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChipItem3D(
                        label = if (isTelugu) "అన్నీ (${totalCount})" else "All (${totalCount})",
                        isSelected = uiState.historyFilterRisk == null,
                        onClick = { viewModel.setHistoryRiskFilter(null) }
                    )
                }
                item {
                    FilterChipItem3D(
                        label = if (isTelugu) "అధిక ప్రమాదం" else "High Risk",
                        isSelected = uiState.historyFilterRisk == RiskLevel.HIGH,
                        onClick = { viewModel.setHistoryRiskFilter(RiskLevel.HIGH) },
                        activeColor = RiskHighRed
                    )
                }
                item {
                    FilterChipItem3D(
                        label = if (isTelugu) "మధ్యస్థ ప్రమాదం" else "Medium Risk",
                        isSelected = uiState.historyFilterRisk == RiskLevel.MEDIUM,
                        onClick = { viewModel.setHistoryRiskFilter(RiskLevel.MEDIUM) },
                        activeColor = RiskMediumAmber
                    )
                }
                item {
                    FilterChipItem3D(
                        label = if (isTelugu) "తక్కువ ప్రమాదం" else "Low Risk",
                        isSelected = uiState.historyFilterRisk == RiskLevel.LOW,
                        onClick = { viewModel.setHistoryRiskFilter(RiskLevel.LOW) },
                        activeColor = RiskLowTeal
                    )
                }
                item {
                    FilterChipItem3D(
                        label = if (isTelugu) "సందేహాస్పదం" else "Uncertain",
                        isSelected = uiState.historyFilterRisk == RiskLevel.UNCERTAIN,
                        onClick = { viewModel.setHistoryRiskFilter(RiskLevel.UNCERTAIN) },
                        activeColor = SilverSteel
                    )
                }
            }
        }

        // Privacy indicator in 3D Card
        item {
            ThreeDCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(6.dp),
                elevation = 2.dp
            ) {
                Text(
                    text = if (userSettings.storeFullMessages)
                        "Privacy Setting: Full message text stored locally (Opt-in active)."
                    else
                        "Privacy Setting: Only threat scores & forensic summaries stored.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = SilverChrome,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        // Empty state or History items
        if (filteredHistory.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = SilverSteel,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "No matching analysis entries found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SilverPure
                        )
                        Text(
                            text = "Analyses performed from the Dashboard will appear here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SilverChrome
                        )
                    }
                }
            }
        } else {
            items(filteredHistory, key = { it.id }) { entity ->
                HistoryRowCard3D(
                    entity = entity,
                    onView = { viewModel.viewResult(entity.toDomainModel()) },
                    onDelete = { viewModel.deleteHistoryItem(entity.id) }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { viewModel.clearAllHistory() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RiskHighRed),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RiskHighRed.copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("clear_all_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Clear All History", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun FilterChipItem3D(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    activeColor: Color = SilverPure
) {
    Box(
        modifier = Modifier
            .shadow(3.dp, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .background(
                if (isSelected) activeColor.copy(alpha = 0.2f) else BlackCarbon
            )
            .border(
                1.dp,
                if (isSelected) activeColor else SilverBorder,
                RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) activeColor else SilverChrome
        )
    }
}

@Composable
private fun HistoryRowCard3D(
    entity: AnalysisEntity,
    onView: () -> Unit,
    onDelete: () -> Unit
) {
    val (riskColor, riskBg) = when (entity.riskLevel.uppercase()) {
        "HIGH" -> RiskHighRed to RiskHighRed.copy(alpha = 0.15f)
        "MEDIUM" -> RiskMediumAmber to RiskMediumAmber.copy(alpha = 0.15f)
        "LOW" -> RiskLowTeal to RiskLowTeal.copy(alpha = 0.15f)
        else -> SilverSteel to SilverSteel.copy(alpha = 0.15f)
    }

    val dateFormatted = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(entity.timestamp))

    ThreeDCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onView),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(riskBg, RoundedCornerShape(4.dp))
                            .border(0.5.dp, riskColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = entity.riskLevel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = riskColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Score: ${entity.heuristicScore}/100",
                        style = MaterialTheme.typography.labelSmall,
                        color = SilverChrome
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dateFormatted,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = SilverSteel
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = SilverSteel,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = entity.summary,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 2,
                color = SilverPure
            )

            Text(
                text = entity.inputSnippet,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                color = SilverChrome
            )
        }
    }
}
