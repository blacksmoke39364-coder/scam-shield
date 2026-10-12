package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.OnboardingTermsDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.ResultDetailScreen
import com.example.ui.screens.TermsPrivacyScreen
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.ScamShieldTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ScamShieldViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ScamShieldViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle shared text from other apps (WhatsApp, SMS, Chrome)
        handleIncomingIntent(intent)

        setContent {
            val userSettings by viewModel.userSettings.collectAsState()
            val uiState by viewModel.uiState.collectAsState()

            ScamShieldTheme(darkTheme = userSettings.isDarkMode) {
                val snackbarHostState = remember { SnackbarHostState() }

                // Display toast messages via Snackbar
                LaunchedEffect(uiState.toastMessage) {
                    uiState.toastMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearToast()
                    }
                }

                // Show onboarding terms on first launch if not yet accepted
                if (userSettings.termsAcceptedVersion.isBlank()) {
                    OnboardingTermsDialog(
                        currentLanguage = userSettings.preferredLanguage,
                        onLanguageSelected = { viewModel.updateLanguagePreference(it) },
                        onAccept = { viewModel.acceptTerms() }
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        // Only show bottom navigation on top-level screens
                        if (uiState.currentScreen != AppScreen.RESULT_DETAIL &&
                            uiState.currentScreen != AppScreen.TERMS_PRIVACY
                        ) {
                            ScamShieldBottomNavigation(
                                currentScreen = uiState.currentScreen,
                                onSelectScreen = { viewModel.navigateTo(it) },
                                selectedLanguage = uiState.selectedLanguage
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (uiState.currentScreen) {
                            AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                            AppScreen.RESULT_DETAIL -> ResultDetailScreen(viewModel = viewModel)
                            AppScreen.HISTORY -> HistoryScreen(viewModel = viewModel)
                            AppScreen.LEARN_MODE -> LearnScreen(viewModel = viewModel)
                            AppScreen.SETTINGS_PROFILE -> ProfileSettingsScreen(viewModel = viewModel)
                            AppScreen.TERMS_PRIVACY -> TermsPrivacyScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return
        val sharedText = com.example.ui.util.ShareHelper.extractIncomingText(this)
            ?: intent.getStringExtra(Intent.EXTRA_TEXT)
            ?: intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
            ?: intent.clipData?.getItemAt(0)?.text?.toString()
            ?: intent.dataString

        if (!sharedText.isNullOrBlank()) {
            viewModel.setInputText(sharedText)
            viewModel.navigateTo(AppScreen.DASHBOARD)
            viewModel.showToast("Shared content loaded into ScamShield.")
        }
    }
}

@Composable
fun ScamShieldBottomNavigation(
    currentScreen: AppScreen,
    onSelectScreen: (AppScreen) -> Unit,
    selectedLanguage: String
) {
    val isTelugu = selectedLanguage == "TE"

    NavigationBar(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .border(
                1.dp,
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(com.example.ui.theme.SilverBorderGleam.copy(alpha = 0.5f), com.example.ui.theme.SilverBorder)
                )
            )
            .testTag("bottom_nav_bar"),
        containerColor = com.example.ui.theme.BlackCarbon,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == AppScreen.DASHBOARD,
            onClick = { onSelectScreen(AppScreen.DASHBOARD) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Shield"
                )
            },
            label = {
                Text(
                    text = if (isTelugu) "స్కానర్" else "Scanner",
                    fontWeight = if (currentScreen == AppScreen.DASHBOARD) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = com.example.ui.theme.SilverPure,
                selectedTextColor = com.example.ui.theme.SilverPure,
                unselectedIconColor = com.example.ui.theme.SilverSteel,
                unselectedTextColor = com.example.ui.theme.SilverSteel,
                indicatorColor = com.example.ui.theme.SilverBorderGleam.copy(alpha = 0.4f)
            ),
            modifier = Modifier.testTag("nav_item_dashboard")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.HISTORY,
            onClick = { onSelectScreen(AppScreen.HISTORY) },
            icon = {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History"
                )
            },
            label = {
                Text(
                    text = if (isTelugu) "చరిత్ర" else "History",
                    fontWeight = if (currentScreen == AppScreen.HISTORY) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = com.example.ui.theme.SilverPure,
                selectedTextColor = com.example.ui.theme.SilverPure,
                unselectedIconColor = com.example.ui.theme.SilverSteel,
                unselectedTextColor = com.example.ui.theme.SilverSteel,
                indicatorColor = com.example.ui.theme.SilverBorderGleam.copy(alpha = 0.4f)
            ),
            modifier = Modifier.testTag("nav_item_history")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.LEARN_MODE,
            onClick = { onSelectScreen(AppScreen.LEARN_MODE) },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = "Learn"
                )
            },
            label = {
                Text(
                    text = if (isTelugu) "నేర్చుకోండి" else "Learn",
                    fontWeight = if (currentScreen == AppScreen.LEARN_MODE) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = com.example.ui.theme.SilverPure,
                selectedTextColor = com.example.ui.theme.SilverPure,
                unselectedIconColor = com.example.ui.theme.SilverSteel,
                unselectedTextColor = com.example.ui.theme.SilverSteel,
                indicatorColor = com.example.ui.theme.SilverBorderGleam.copy(alpha = 0.4f)
            ),
            modifier = Modifier.testTag("nav_item_learn")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.SETTINGS_PROFILE,
            onClick = { onSelectScreen(AppScreen.SETTINGS_PROFILE) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile"
                )
            },
            label = {
                Text(
                    text = if (isTelugu) "సెట్టింగ్స్" else "Profile",
                    fontWeight = if (currentScreen == AppScreen.SETTINGS_PROFILE) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = com.example.ui.theme.SilverPure,
                selectedTextColor = com.example.ui.theme.SilverPure,
                unselectedIconColor = com.example.ui.theme.SilverSteel,
                unselectedTextColor = com.example.ui.theme.SilverSteel,
                indicatorColor = com.example.ui.theme.SilverBorderGleam.copy(alpha = 0.4f)
            ),
            modifier = Modifier.testTag("nav_item_profile")
        )
    }
}
