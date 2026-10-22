package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.util.LocalizationHelper
import com.example.viewmodel.CricketViewModel
import com.example.viewmodel.NavTab

class MainActivity : ComponentActivity() {

    private val cricketViewModel: CricketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by cricketViewModel.uiState.collectAsStateWithLifecycle()
            val snackbarHostState = remember { SnackbarHostState() }

            // Trigger snackbar on status messages
            LaunchedEffect(uiState.statusMessage) {
                uiState.statusMessage?.let { msg ->
                    snackbarHostState.showSnackbar(
                        message = msg,
                        duration = SnackbarDuration.Short
                    )
                    cricketViewModel.clearStatusMessage()
                }
            }

            // Back handler support
            BackHandler(enabled = uiState.currentNavigationTab != NavTab.HOME) {
                if (uiState.currentNavigationTab == NavTab.LEADERBOARD) {
                    cricketViewModel.selectNavigationTab(NavTab.TOURNAMENTS)
                } else {
                    cricketViewModel.selectNavigationTab(NavTab.HOME)
                }
            }

            MyApplicationTheme(
                darkTheme = uiState.userProfile.darkTheme,
                highContrast = uiState.userProfile.highContrast
            ) {
                if (uiState.showSplashScreen) {
                    NammaCricketSplashScreen(
                        onSplashFinished = { cricketViewModel.dismissSplashScreen() }
                    )
                } else {
                    val lang = uiState.userProfile.selectedLanguage

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("main_scaffold"),
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        NavigationBar(
                            containerColor = CricketNavySurface,
                            contentColor = CricketGreenLight,
                            windowInsets = WindowInsets.navigationBars,
                            modifier = Modifier.testTag("main_navigation_bar")
                        ) {
                            NavigationBarItem(
                                selected = (uiState.currentNavigationTab == NavTab.HOME),
                                onClick = { cricketViewModel.selectNavigationTab(NavTab.HOME) },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text(LocalizationHelper.getString(lang, "home"), fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = CricketNavy,
                                    indicatorColor = CricketGreenLight,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                )
                            )

                            NavigationBarItem(
                                selected = (uiState.currentNavigationTab == NavTab.LIVE_MATCH),
                                onClick = { cricketViewModel.selectNavigationTab(NavTab.LIVE_MATCH) },
                                icon = { Icon(Icons.Default.SportsCricket, contentDescription = "Live Match") },
                                label = { Text(LocalizationHelper.getString(lang, "live_scores"), fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = CricketNavy,
                                    indicatorColor = CricketGreenLight,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                )
                            )

                            NavigationBarItem(
                                selected = (uiState.currentNavigationTab == NavTab.SCORER),
                                onClick = { cricketViewModel.selectNavigationTab(NavTab.SCORER) },
                                icon = { Icon(Icons.Default.EditNote, contentDescription = "Scorer") },
                                label = { Text(LocalizationHelper.getString(lang, "scoring"), fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = CricketNavy,
                                    indicatorColor = CricketGreenLight,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                )
                            )

                            NavigationBarItem(
                                selected = (uiState.currentNavigationTab == NavTab.TOURNAMENTS),
                                onClick = { cricketViewModel.selectNavigationTab(NavTab.TOURNAMENTS) },
                                icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Tournaments") },
                                label = { Text(LocalizationHelper.getString(lang, "tournaments"), fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = CricketNavy,
                                    indicatorColor = CricketGreenLight,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                )
                            )

                            NavigationBarItem(
                                selected = (uiState.currentNavigationTab == NavTab.PLAYERS),
                                onClick = { cricketViewModel.selectNavigationTab(NavTab.PLAYERS) },
                                icon = { Icon(Icons.Default.Person, contentDescription = "Players") },
                                label = { Text(LocalizationHelper.getString(lang, "players"), fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = CricketNavy,
                                    indicatorColor = CricketGreenLight,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                )
                            )

                            NavigationBarItem(
                                selected = (uiState.currentNavigationTab == NavTab.PROFILE),
                                onClick = { cricketViewModel.selectNavigationTab(NavTab.PROFILE) },
                                icon = { Icon(Icons.Default.Settings, contentDescription = "Profile") },
                                label = { Text(LocalizationHelper.getString(lang, "profile"), fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = CricketNavy,
                                    indicatorColor = CricketGreenLight,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                )
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        when (uiState.currentNavigationTab) {
                            NavTab.HOME -> {
                                HomeScreen(
                                    uiState = uiState,
                                    onNavigate = { tab -> cricketViewModel.selectNavigationTab(tab) },
                                    onOpenMatchDetail = { tabIndex ->
                                        cricketViewModel.selectMatchDetailTab(tabIndex)
                                        cricketViewModel.selectNavigationTab(NavTab.LIVE_MATCH)
                                    },
                                    onShareMatch = { cricketViewModel.shareMatchScore() },
                                    onVoteFanPoll = { isTeamA -> cricketViewModel.voteFanPoll(isTeamA) },
                                    onOpenPayment = { cricketViewModel.openPaymentModal() }
                                )
                            }
                            NavTab.LIVE_MATCH -> {
                                MatchDetailScreen(
                                    uiState = uiState,
                                    selectedTab = uiState.selectedMatchDetailTab,
                                    onTabSelect = { tabIndex -> cricketViewModel.selectMatchDetailTab(tabIndex) },
                                    onShareMatch = { cricketViewModel.shareMatchScore() },
                                    onExportPdf = { cricketViewModel.exportScorecardDocument() },
                                    onCameraAngleSelect = { angle -> cricketViewModel.changeCameraAngle(angle) },
                                    onSendChatMessage = { msg -> cricketViewModel.sendLiveChatMessage(msg) },
                                    onCrowdCheer = { cheer -> cricketViewModel.triggerCrowdReaction(cheer) }
                                )
                            }
                            NavTab.SCORER -> {
                                LiveScoringScreen(
                                    uiState = uiState,
                                    onRecordBall = { runs, isWicket, wType, isWide, isNoBall, isBye, isLegBye ->
                                        cricketViewModel.recordBall(
                                            runs = runs,
                                            isWicket = isWicket,
                                            wicketType = wType,
                                            isWide = isWide,
                                            isNoBall = isNoBall,
                                            isBye = isBye,
                                            isLegBye = isLegBye
                                        )
                                    },
                                    onSwapStrike = { cricketViewModel.swapStrike() },
                                    onUndoBall = { cricketViewModel.undoLastBall() },
                                    onToggleSimulation = { cricketViewModel.toggleLiveSimulation() }
                                )
                            }
                            NavTab.TOURNAMENTS -> {
                                TournamentsScreen(
                                    uiState = uiState,
                                    onSelectTournament = { tId -> cricketViewModel.selectTournament(tId) },
                                    onOpenPaymentModal = { cricketViewModel.openPaymentModal() },
                                    onOpenCreateTournamentModal = { cricketViewModel.openCreateTournamentModal() },
                                    onExportRulebook = { cricketViewModel.exportScorecardDocument() },
                                    onAddSponsor = { tId, name, cat, amt ->
                                        cricketViewModel.addSponsorToTournament(tId, name, cat, amt)
                                    },
                                    onOpenLeaderboard = { cricketViewModel.selectNavigationTab(NavTab.LEADERBOARD) }
                                )
                            }
                            NavTab.LEADERBOARD -> {
                                TournamentLeaderboardScreen(
                                    uiState = uiState,
                                    onSelectTournament = { tId -> cricketViewModel.selectTournament(tId) },
                                    onShareLeaderboard = { tourney -> cricketViewModel.shareTournamentLeaderboard(tourney) },
                                    onNavigateBack = { cricketViewModel.selectNavigationTab(NavTab.TOURNAMENTS) }
                                )
                            }
                            NavTab.PLAYERS -> {
                                PlayersScreen(
                                    uiState = uiState,
                                    onSelectPlayer = { pId -> cricketViewModel.selectPlayer(pId) },
                                    onSharePlayer = { player -> cricketViewModel.sharePlayerCard(player) }
                                )
                            }
                            NavTab.PROFILE -> {
                                ProfileSettingsScreen(
                                    uiState = uiState,
                                    onSetLanguage = { langChoice -> cricketViewModel.setLanguage(langChoice) },
                                    onToggleTheme = { isDark -> cricketViewModel.toggleTheme(isDark) },
                                    onToggleHighContrast = { isHigh -> cricketViewModel.toggleHighContrast(isHigh) },
                                    onToggleOfflineMode = { enabled -> cricketViewModel.toggleOfflineMode(enabled) },
                                    onOpenTwoFactorModal = { cricketViewModel.openTwoFactorModal() },
                                    onExportScorecard = { cricketViewModel.exportScorecardDocument() },
                                    onUploadHighlight = { title, cat -> cricketViewModel.uploadHighlightClip(title, cat) },
                                    onReplaySplash = { cricketViewModel.replaySplashScreen() }
                                )
                            }
                        }
                    }

                    // Dialogs
                    if (uiState.showPaymentModal) {
                        val currTourney = uiState.tournaments.find { it.id == uiState.selectedTournamentId }
                            ?: uiState.tournaments.first()
                        PaymentGatewayModal(
                            tournamentName = currTourney.name,
                            registrationFee = currTourney.registrationFee,
                            onDismiss = { cricketViewModel.closePaymentModal() },
                            onPaymentSuccess = { teamName, captainName, amount, method ->
                                cricketViewModel.processRegistrationPayment(teamName, captainName, amount, method)
                            }
                        )
                    }

                    if (uiState.showCreateTournamentModal) {
                        CreateTournamentModal(
                            onDismiss = { cricketViewModel.closeCreateTournamentModal() },
                            onCreate = { name, organizer, loc, fee, prize ->
                                cricketViewModel.createTournament(name, organizer, loc, fee, prize)
                            }
                        )
                    }

                    if (uiState.showTwoFactorModal) {
                        TwoFactorAuthModal(
                            onDismiss = { cricketViewModel.closeTwoFactorModal() },
                            onVerify = { pin -> cricketViewModel.verifyTwoFactorPin(pin) }
                        )
                    }

                    if (uiState.showShareDialog) {
                        SocialShareModal(
                            shareText = uiState.shareContentText,
                            onDismiss = { cricketViewModel.closeShareDialog() }
                        )
                    }

                    uiState.exportDocumentContent?.let { docContent ->
                        DocumentPreviewModal(
                            documentText = docContent,
                            onDismiss = { cricketViewModel.dismissExportDocument() }
                        )
                    }
                }
                }
            }
        }
    }
}
