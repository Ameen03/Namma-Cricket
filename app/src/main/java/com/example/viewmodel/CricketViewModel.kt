package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.CricketRepository
import com.example.util.LocalizationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class CricketUiState(
    val liveMatch: Match = CricketRepository.initialLiveMatch,
    val tournaments: List<Tournament> = CricketRepository.sampleTournaments,
    val selectedTournamentId: String = "TRN_2026_01",
    val players: List<Player> = CricketRepository.samplePlayers,
    val selectedPlayerId: String? = "PL_02",
    val highlights: List<HighlightClip> = CricketRepository.sampleHighlights,
    val notifications: List<NotificationItem> = CricketRepository.sampleNotifications,
    val userProfile: UserProfile = UserProfile(),
    val wormData: List<WormRunPoint> = CricketRepository.sampleWormData,
    val viewerRetention: List<ViewerRetentionPoint> = CricketRepository.sampleViewerRetention,
    val liveChatMessages: List<LiveChatMessage> = CricketRepository.initialLiveChat,
    val isSimulationActive: Boolean = false,
    val currentNavigationTab: NavTab = NavTab.HOME,
    val selectedMatchDetailTab: Int = 0, // 0: Live, 1: Scorecard, 2: Commentary, 3: Analytics, 4: Stream
    val showPaymentModal: Boolean = false,
    val showCreateTournamentModal: Boolean = false,
    val showTwoFactorModal: Boolean = false,
    val showShareDialog: Boolean = false,
    val shareContentText: String = "",
    val cloudSyncStatus: String = "Synced with Cloud (Encrypted)",
    val cloudStorageUsedMb: Double = 340.5,
    val cloudStorageLimitMb: Double = 1024.0,
    val exportDocumentContent: String? = null,
    val statusMessage: String? = null,
    val showSplashScreen: Boolean = true
)

enum class NavTab {
    HOME, LIVE_MATCH, TOURNAMENTS, LEADERBOARD, SCORER, PLAYERS, PROFILE
}

class CricketViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CricketUiState())
    val uiState: StateFlow<CricketUiState> = _uiState.asStateFlow()

    private var simulationJob: Job? = null

    fun selectNavigationTab(tab: NavTab) {
        _uiState.update { it.copy(currentNavigationTab = tab) }
    }

    fun selectMatchDetailTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedMatchDetailTab = tabIndex) }
    }

    fun selectPlayer(playerId: String) {
        _uiState.update { it.copy(selectedPlayerId = playerId) }
    }

    fun selectTournament(tournamentId: String) {
        _uiState.update { it.copy(selectedTournamentId = tournamentId) }
    }

    fun dismissSplashScreen() {
        _uiState.update { it.copy(showSplashScreen = false) }
    }

    fun replaySplashScreen() {
        _uiState.update { it.copy(showSplashScreen = true) }
    }

    // --- SCORING ACTIONS ---

    fun recordBall(
        runs: Int,
        isWicket: Boolean = false,
        wicketType: String = "",
        isWide: Boolean = false,
        isNoBall: Boolean = false,
        isBye: Boolean = false,
        isLegBye: Boolean = false,
        wagonAngle: Float = Random.nextFloat() * 360f
    ) {
        val currentMatch = _uiState.value.liveMatch
        val currentInnings = currentMatch.currentInnings
        val isLegalDelivery = !isWide && !isNoBall

        val newLegalBalls = if (isLegalDelivery) currentInnings.legalBalls + 1 else currentInnings.legalBalls
        val extraRuns = (if (isWide) 1 else 0) + (if (isNoBall) 1 else 0)
        val totalBallRuns = runs + extraRuns
        val newTotalRuns = currentInnings.totalRuns + totalBallRuns
        val newWickets = if (isWicket) currentInnings.wickets + 1 else currentInnings.wickets

        // Current over & ball calculation
        val overNum = currentInnings.legalBalls / 6
        val ballInOver = (currentInnings.legalBalls % 6) + (if (isLegalDelivery) 1 else 0)
        val overBallStr = "$overNum.$ballInOver"

        val striker = currentMatch.teamB.squad.find { it.id == currentMatch.currentStrikerId }
            ?: currentMatch.currentBattingTeam.squad.firstOrNull()
            ?: Player("PL_STRIKER", "Striker", PlayerRole.BATTER, "", "", 0, 0, 0, 0.0, 0.0, 0, 0.0, "", 0, 0)
        val bowler = currentMatch.teamA.squad.find { it.id == currentMatch.currentBowlerId }
            ?: currentMatch.currentBowlingTeam.squad.firstOrNull()
            ?: Player("PL_BOWLER", "Bowler", PlayerRole.BOWLER, "", "", 0, 0, 0, 0.0, 0.0, 0, 0.0, "", 0, 0)

        val shotDescription = when {
            isWicket -> "OUT! $wicketType! The bowler breaks through!"
            runs == 6 -> "MASSIVE SIX! Smoked into the upper deck!"
            runs == 4 -> "CRACKING FOUR! Pierces the infield gap with exquisite timing!"
            runs == 1 -> "Quick single driven smoothly into the outfield."
            runs == 2 -> "Pushed softly into the deep, brilliant running for two."
            isWide -> "Wide ball called by the umpire as it swings down the leg side."
            isNoBall -> "NO BALL! Overstepping the crease! Free hit coming up!"
            else -> "Dot ball. Defended solidly back down the pitch."
        }

        val badge = when {
            isWicket -> CommentaryBadge.WICKET
            runs == 6 -> CommentaryBadge.SIX
            runs == 4 -> CommentaryBadge.FOUR
            isWide || isNoBall -> CommentaryBadge.EXTRA
            else -> CommentaryBadge.NORMAL
        }

        val ballEvent = BallEvent(
            id = "B_${System.currentTimeMillis()}",
            overNumber = overNum,
            ballNumber = ballInOver,
            runs = runs,
            isWicket = isWicket,
            wicketType = wicketType,
            dismissedPlayerName = if (isWicket) striker.name else "",
            isWide = isWide,
            isNoBall = isNoBall,
            isBye = isBye,
            isLegBye = isLegBye,
            bowlerName = bowler.name,
            batsmanName = striker.name,
            wagonAngleDeg = wagonAngle,
            shotType = if (runs >= 4) "Cover Drive / Loft" else "Defensive / Push",
            commentaryText = shotDescription
        )

        val newCommentaryItem = CommentaryEntry(
            id = "COMM_${System.currentTimeMillis()}",
            overBall = overBallStr,
            title = if (isWicket) "WICKET! ${striker.name}" else if (runs == 6) "SIX RUNS!" else if (runs == 4) "FOUR RUNS!" else "$runs Run${if (runs > 1) "s" else ""}",
            description = "${bowler.name} to ${striker.name}: $shotDescription",
            badgeType = badge,
            timestamp = "Just now"
        )

        // Update batsman stats
        val updatedBatsmen = currentInnings.batsmanStats.map { bat ->
            if (bat.playerId == striker.id) {
                bat.copy(
                    runs = bat.runs + (if (!isWide) runs else 0),
                    balls = bat.balls + (if (!isWide) 1 else 0),
                    fours = bat.fours + (if (runs == 4) 1 else 0),
                    sixes = bat.sixes + (if (runs == 6) 1 else 0),
                    isOut = bat.isOut || isWicket,
                    dismissalInfo = if (isWicket) "b ${bowler.name}" else bat.dismissalInfo
                )
            } else bat
        }

        // Update bowler stats
        val updatedBowlers = currentInnings.bowlerStats.map { bowl ->
            if (bowl.playerId == bowler.id) {
                bowl.copy(
                    legalBalls = bowl.legalBalls + (if (isLegalDelivery) 1 else 0),
                    runsConceded = bowl.runsConceded + totalBallRuns,
                    wickets = bowl.wickets + (if (isWicket) 1 else 0),
                    wides = bowl.wides + (if (isWide) 1 else 0),
                    noBalls = bowl.noBalls + (if (isNoBall) 1 else 0)
                )
            } else bowl
        }

        val updatedInnings = currentInnings.copy(
            totalRuns = newTotalRuns,
            wickets = newWickets,
            legalBalls = newLegalBalls,
            extras = currentInnings.extras.copy(
                wides = currentInnings.extras.wides + (if (isWide) 1 else 0),
                noBalls = currentInnings.extras.noBalls + (if (isNoBall) 1 else 0)
            ),
            batsmanStats = updatedBatsmen,
            bowlerStats = updatedBowlers,
            ballHistory = listOf(ballEvent) + currentInnings.ballHistory,
            commentary = listOf(newCommentaryItem) + currentInnings.commentary
        )

        val updatedInningsList = currentMatch.inningsList.toMutableList().apply {
            set(currentMatch.currentBattingTeamIndex, updatedInnings)
        }

        // Rotate strike if odd runs, or rotate at over boundary
        var nextStrikerId = currentMatch.currentStrikerId
        var nextNonStrikerId = currentMatch.currentNonStrikerId
        if (runs % 2 != 0) {
            val tmp = nextStrikerId
            nextStrikerId = nextNonStrikerId
            nextNonStrikerId = tmp
        }

        val isOverEnd = isLegalDelivery && (newLegalBalls % 6 == 0)
        if (isOverEnd) {
            val tmp = nextStrikerId
            nextStrikerId = nextNonStrikerId
            nextNonStrikerId = tmp
        }

        val targetRuns = currentMatch.inningsList.first().totalRuns + 1
        val runsNeeded = targetRuns - newTotalRuns
        val ballsRemaining = (currentMatch.oversLimit * 6) - newLegalBalls

        val resultSummary = when {
            runsNeeded <= 0 -> "${currentMatch.currentBattingTeam.name} won by ${10 - newWickets} wickets!"
            ballsRemaining <= 0 -> "${currentMatch.currentBowlingTeam.name} won by ${runsNeeded - 1} runs!"
            else -> "${currentMatch.currentBattingTeam.shortName} needs $runsNeeded runs in $ballsRemaining balls (Target: $targetRuns)"
        }

        val updatedMatch = currentMatch.copy(
            inningsList = updatedInningsList,
            currentStrikerId = nextStrikerId,
            currentNonStrikerId = nextNonStrikerId,
            resultSummary = resultSummary
        )

        // Trigger notification if milestone / wicket
        val newNotifications = _uiState.value.notifications.toMutableList()
        if (isWicket) {
            newNotifications.add(
                0,
                NotificationItem(
                    id = "NOTIF_${System.currentTimeMillis()}",
                    title = "⚡ WICKET! ${currentMatch.currentBattingTeam.shortName} ${newTotalRuns}/$newWickets",
                    message = "${bowler.name} strikes! $wicketType dismissal. $resultSummary",
                    timestamp = "Just now",
                    type = NotificationType.WICKET_ALERT
                )
            )
        } else if (runs == 6) {
            newNotifications.add(
                0,
                NotificationItem(
                    id = "NOTIF_${System.currentTimeMillis()}",
                    title = "💥 HUGE SIX by ${striker.name}!",
                    message = "Towering maximum into the stands! Score: ${newTotalRuns}/$newWickets in ${updatedInnings.oversFormatted} ov.",
                    timestamp = "Just now",
                    type = NotificationType.MILESTONE_ALERT
                )
            )
        }

        _uiState.update {
            it.copy(
                liveMatch = updatedMatch,
                notifications = newNotifications,
                statusMessage = "Recorded ball: $runs runs ${if (isWicket) "(Wicket)" else ""}"
            )
        }
    }

    fun swapStrike() {
        val currentMatch = _uiState.value.liveMatch
        val temp = currentMatch.currentStrikerId
        _uiState.update {
            it.copy(
                liveMatch = currentMatch.copy(
                    currentStrikerId = currentMatch.currentNonStrikerId,
                    currentNonStrikerId = temp
                ),
                statusMessage = "Swapped strike between batsmen"
            )
        }
    }

    fun undoLastBall() {
        val currentMatch = _uiState.value.liveMatch
        val currentInnings = currentMatch.currentInnings
        if (currentInnings.ballHistory.isEmpty()) {
            _uiState.update { it.copy(statusMessage = "No balls to undo in this innings") }
            return
        }

        val lastBall = currentInnings.ballHistory.first()
        val isLegal = !lastBall.isWide && !lastBall.isNoBall
        val restoredLegalBalls = if (isLegal) (currentInnings.legalBalls - 1).coerceAtLeast(0) else currentInnings.legalBalls
        val totalRunsOfLastBall = lastBall.runs + (if (lastBall.isWide || lastBall.isNoBall) 1 else 0)
        val restoredTotalRuns = (currentInnings.totalRuns - totalRunsOfLastBall).coerceAtLeast(0)
        val restoredWickets = if (lastBall.isWicket) (currentInnings.wickets - 1).coerceAtLeast(0) else currentInnings.wickets

        val updatedInnings = currentInnings.copy(
            totalRuns = restoredTotalRuns,
            wickets = restoredWickets,
            legalBalls = restoredLegalBalls,
            ballHistory = currentInnings.ballHistory.drop(1),
            commentary = currentInnings.commentary.drop(1)
        )

        val updatedInningsList = currentMatch.inningsList.toMutableList().apply {
            set(currentMatch.currentBattingTeamIndex, updatedInnings)
        }

        _uiState.update {
            it.copy(
                liveMatch = currentMatch.copy(inningsList = updatedInningsList),
                statusMessage = "Last ball undone successfully"
            )
        }
    }

    // --- LIVE SIMULATION TOGGLE ---

    fun toggleLiveSimulation() {
        val isCurrentlyActive = _uiState.value.isSimulationActive
        if (isCurrentlyActive) {
            simulationJob?.cancel()
            simulationJob = null
            _uiState.update { it.copy(isSimulationActive = false, statusMessage = "Live Simulation paused") }
        } else {
            _uiState.update { it.copy(isSimulationActive = true, statusMessage = "Live Match Simulation started") }
            simulationJob = viewModelScope.launch {
                while (_uiState.value.isSimulationActive) {
                    delay(3800)
                    val roll = Random.nextInt(100)
                    when {
                        roll < 35 -> recordBall(runs = 1)
                        roll < 55 -> recordBall(runs = 0)
                        roll < 72 -> recordBall(runs = 2)
                        roll < 85 -> recordBall(runs = 4)
                        roll < 93 -> recordBall(runs = 6)
                        roll < 97 -> recordBall(runs = 0, isWicket = true, wicketType = "Caught")
                        else -> recordBall(runs = 0, isWide = true)
                    }
                }
            }
        }
    }

    // --- LIVE STREAM & FAN REACTIONS ---

    fun changeCameraAngle(angle: String) {
        val updated = _uiState.value.liveMatch.copy(currentCameraAngle = angle)
        _uiState.update { it.copy(liveMatch = updated, statusMessage = "Switched camera angle to $angle") }
    }

    fun sendLiveChatMessage(text: String) {
        if (text.isBlank()) return
        val newMsg = LiveChatMessage(
            id = "CHAT_${System.currentTimeMillis()}",
            senderName = _uiState.value.userProfile.name,
            message = text,
            timestamp = "Just now"
        )
        _uiState.update {
            it.copy(
                liveChatMessages = it.liveChatMessages + newMsg,
                statusMessage = "Message sent to live match stream"
            )
        }
    }

    fun triggerCrowdReaction(emoji: String) {
        _uiState.update {
            it.copy(statusMessage = "Crowd reaction sent: $emoji")
        }
    }

    fun voteFanPoll(isTeamA: Boolean) {
        val current = _uiState.value.liveMatch
        val updated = if (isTeamA) {
            current.copy(fanPollTeamAVotes = current.fanPollTeamAVotes + 1)
        } else {
            current.copy(fanPollTeamBVotes = current.fanPollTeamBVotes + 1)
        }
        _uiState.update { it.copy(liveMatch = updated, statusMessage = "Vote registered! Thank you for participating.") }
    }

    // --- TOURNAMENT & PAYMENT GATEWAY ---

    fun openPaymentModal() {
        _uiState.update { it.copy(showPaymentModal = true) }
    }

    fun closePaymentModal() {
        _uiState.update { it.copy(showPaymentModal = false) }
    }

    fun processRegistrationPayment(teamName: String, captainName: String, amount: Double, method: String) {
        val payment = RegistrationPayment(
            transactionId = "TXN_CRIC_${Random.nextInt(100000, 999999)}",
            tournamentId = _uiState.value.selectedTournamentId,
            teamName = teamName,
            captainName = captainName,
            amount = amount,
            paymentMethod = method,
            status = "SUCCESS - VERIFIED"
        )
        val notif = NotificationItem(
            id = "NOTIF_${System.currentTimeMillis()}",
            title = "Payment Confirmed: $teamName",
            message = "Registration fee of $$amount paid via $method. Transaction ID: ${payment.transactionId}",
            timestamp = "Just now",
            type = NotificationType.TOURNAMENT_ANNOUNCEMENT
        )
        _uiState.update {
            it.copy(
                showPaymentModal = false,
                notifications = listOf(notif) + it.notifications,
                statusMessage = "Registration fee of $$amount successfully processed!"
            )
        }
    }

    fun openCreateTournamentModal() {
        _uiState.update { it.copy(showCreateTournamentModal = true) }
    }

    fun closeCreateTournamentModal() {
        _uiState.update { it.copy(showCreateTournamentModal = false) }
    }

    fun createTournament(name: String, organizer: String, location: String, fee: Double, prizePool: String) {
        val newTournament = Tournament(
            id = "TRN_${System.currentTimeMillis()}",
            name = name,
            edition = "Inaugural Edition 2026",
            organizer = organizer,
            location = location,
            startDate = "Nov 15, 2026",
            endDate = "Dec 10, 2026",
            format = TournamentFormat.LEAGUE_KNOCKOUT,
            teamsCount = 8,
            totalMatches = 28,
            registrationFee = fee,
            prizePool = prizePool,
            status = TournamentStatus.UPCOMING,
            standings = listOf(
                StandingRow(1, "TM_01", "Team Alpha", "ALP", 0, 0, 0, 0, 0.0, 0),
                StandingRow(2, "TM_02", "Team Beta", "BET", 0, 0, 0, 0, 0.0, 0)
            )
        )
        _uiState.update {
            it.copy(
                tournaments = listOf(newTournament) + it.tournaments,
                showCreateTournamentModal = false,
                statusMessage = "Tournament '$name' created successfully!"
            )
        }
    }

    fun addSponsorToTournament(tournamentId: String, sponsorName: String, category: String, amount: Double) {
        val updatedTournaments = _uiState.value.tournaments.map { t ->
            if (t.id == tournamentId) {
                val newSponsor = Sponsor("SP_${System.currentTimeMillis()}", sponsorName, category, amount)
                t.copy(sponsors = t.sponsors + newSponsor)
            } else t
        }
        _uiState.update {
            it.copy(
                tournaments = updatedTournaments,
                statusMessage = "Sponsor '$sponsorName' added to tournament!"
            )
        }
    }

    // --- CLOUD STORAGE & HIGHLIGHTS ---

    fun uploadHighlightClip(title: String, category: String) {
        val newClip = HighlightClip(
            id = "HL_${System.currentTimeMillis()}",
            title = title,
            matchName = "${_uiState.value.liveMatch.teamA.shortName} vs ${_uiState.value.liveMatch.teamB.shortName}",
            durationSec = Random.nextInt(25, 60),
            viewsCount = 120,
            likesCount = 18,
            category = category,
            timestamp = "Just uploaded"
        )
        val newStorage = (_uiState.value.cloudStorageUsedMb + 18.5).coerceAtMost(_uiState.value.cloudStorageLimitMb)
        _uiState.update {
            it.copy(
                highlights = listOf(newClip) + it.highlights,
                cloudStorageUsedMb = newStorage,
                statusMessage = "Video highlight uploaded to cloud storage!"
            )
        }
    }

    fun likeHighlightClip(clipId: String) {
        val updated = _uiState.value.highlights.map { clip ->
            if (clip.id == clipId) clip.copy(likesCount = clip.likesCount + 1) else clip
        }
        _uiState.update { it.copy(highlights = updated) }
    }

    // --- SOCIAL SHARING ---

    fun openShareDialog(text: String) {
        _uiState.update { it.copy(showShareDialog = true, shareContentText = text) }
    }

    fun closeShareDialog() {
        _uiState.update { it.copy(showShareDialog = false) }
    }

    fun shareMatchScore() {
        val match = _uiState.value.liveMatch
        val text = "🏏 LIVE CRICKET UPDATE: ${match.tournamentName}\n" +
                "${match.teamA.name}: ${match.inningsList.first().totalRuns}/${match.inningsList.first().wickets} (${match.inningsList.first().oversFormatted} ov)\n" +
                "${match.teamB.name}: ${match.currentInnings.totalRuns}/${match.currentInnings.wickets} (${match.currentInnings.oversFormatted} ov)\n" +
                "${match.resultSummary}\n" +
                "Follow live ball-by-ball on CricHeroes!"
        openShareDialog(text)
    }

    fun sharePlayerCard(player: Player) {
        val text = "🌟 NAMMA CRICKET PLAYER PROFILE: ${player.name}\n" +
                "Role: ${player.role.name} | Matches: ${player.matches}\n" +
                "Runs: ${player.runs} (HS: ${player.highestScore}) | Avg: ${player.battingAverage} | SR: ${player.strikeRate}\n" +
                "Wickets: ${player.wickets} | Economy: ${player.economy}\n" +
                "MVP Points: ${player.mvpPoints} ⭐\n" +
                "Download Namma Cricket to track your grassroots cricket stats!"
        openShareDialog(text)
    }

    fun shareTournamentLeaderboard(tournament: Tournament) {
        val standingsText = buildString {
            appendLine("🏆 ${tournament.name.uppercase()} - OFFICIAL LEADERBOARD")
            appendLine("Location: ${tournament.location} • Edition: ${tournament.edition}")
            appendLine("==========================================")
            appendLine("#   TEAM            P   W   L   NRR      PTS")
            tournament.standings.forEach { row ->
                appendLine(String.format("%-2d  %-15s %-3d %-3d %-3d %-+8.3f %-3d", row.rank, row.shortName, row.matchesPlayed, row.won, row.lost, row.netRunRate, row.points))
            }
            appendLine("==========================================")
            appendLine("⚡ Top 4 teams qualify for Playoffs")
            appendLine("Track live ball-by-ball on Namma Cricket!")
        }
        openShareDialog(standingsText)
    }

    // --- PROFILE & ACCESSIBILITY & 2FA ---

    fun setLanguage(language: AppLanguage) {
        val updated = _uiState.value.userProfile.copy(selectedLanguage = language)
        _uiState.update { it.copy(userProfile = updated, statusMessage = "Language set to ${language.displayName}") }
    }

    fun toggleTheme(isDark: Boolean) {
        val updated = _uiState.value.userProfile.copy(darkTheme = isDark)
        _uiState.update { it.copy(userProfile = updated) }
    }

    fun toggleHighContrast(isHigh: Boolean) {
        val updated = _uiState.value.userProfile.copy(highContrast = isHigh)
        _uiState.update { it.copy(userProfile = updated) }
    }

    fun toggleOfflineMode(enabled: Boolean) {
        val updated = _uiState.value.userProfile.copy(offlineModeEnabled = enabled)
        val syncMsg = if (enabled) "Offline Mode Enabled (Cached Locally)" else "Online - Cloud Sync Active"
        _uiState.update { it.copy(userProfile = updated, cloudSyncStatus = syncMsg, statusMessage = syncMsg) }
    }

    fun openTwoFactorModal() {
        _uiState.update { it.copy(showTwoFactorModal = true) }
    }

    fun closeTwoFactorModal() {
        _uiState.update { it.copy(showTwoFactorModal = false) }
    }

    fun verifyTwoFactorPin(enteredPin: String): Boolean {
        return if (enteredPin == _uiState.value.userProfile.twoFactorPin) {
            val updated = _uiState.value.userProfile.copy(twoFactorEnabled = true)
            _uiState.update {
                it.copy(
                    userProfile = updated,
                    showTwoFactorModal = false,
                    statusMessage = "Multi-Factor Authentication verified successfully!"
                )
            }
            true
        } else {
            _uiState.update { it.copy(statusMessage = "Incorrect 2FA PIN. Please try again.") }
            false
        }
    }

    // --- DOCUMENT EXPORT (PDF/CSV) ---

    fun exportScorecardDocument() {
        val match = _uiState.value.liveMatch
        val inn1 = match.inningsList.first()
        val inn2 = match.currentInnings

        val document = buildString {
            appendLine("==========================================")
            appendLine("CRICHEROES OFFICIAL MATCH SCORECARD")
            appendLine("Tournament: ${match.tournamentName}")
            appendLine("Venue: ${match.venue} | Date: ${match.matchDate}")
            appendLine("Toss: ${match.tossDetails}")
            appendLine("Result: ${match.resultSummary}")
            appendLine("==========================================")
            appendLine("\n1ST INNINGS: ${match.teamA.name}")
            appendLine("Total: ${inn1.totalRuns}/${inn1.wickets} in ${inn1.oversFormatted} overs (Extras: ${inn1.extras.total})")
            appendLine("------------------------------------------")
            appendLine("Batsman               R    B    4s  6s   SR")
            inn1.batsmanStats.forEach { b ->
                appendLine(String.format("%-20s %-4d %-4d %-3d %-3d %.1f", b.name, b.runs, b.balls, b.fours, b.sixes, b.strikeRate))
            }
            appendLine("\nBowling               O    M    R    W   ECON")
            inn1.bowlerStats.forEach { bw ->
                appendLine(String.format("%-20s %-4s %-4d %-4d %-3d %.1f", bw.name, bw.oversFormatted, bw.maidens, bw.runsConceded, bw.wickets, bw.economy))
            }
            appendLine("\n==========================================")
            appendLine("2ND INNINGS: ${match.teamB.name}")
            appendLine("Total: ${inn2.totalRuns}/${inn2.wickets} in ${inn2.oversFormatted} overs (Extras: ${inn2.extras.total})")
            appendLine("------------------------------------------")
            appendLine("Batsman               R    B    4s  6s   SR")
            inn2.batsmanStats.forEach { b ->
                appendLine(String.format("%-20s %-4d %-4d %-3d %-3d %.1f", b.name, b.runs, b.balls, b.fours, b.sixes, b.strikeRate))
            }
            appendLine("\nBowling               O    M    R    W   ECON")
            inn2.bowlerStats.forEach { bw ->
                appendLine(String.format("%-20s %-4s %-4d %-4d %-3d %.1f", bw.name, bw.oversFormatted, bw.maidens, bw.runsConceded, bw.wickets, bw.economy))
            }
            appendLine("==========================================")
            appendLine("Generated by CricHeroes - Grassroots Cricket Platform")
        }

        _uiState.update {
            it.copy(
                exportDocumentContent = document,
                statusMessage = "Scorecard exported to Document Preview & PDF format"
            )
        }
    }

    fun dismissExportDocument() {
        _uiState.update { it.copy(exportDocumentContent = null) }
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }
}
