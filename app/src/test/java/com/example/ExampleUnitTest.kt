package com.example

import com.example.data.model.*
import com.example.data.repository.CricketRepository
import com.example.util.LocalizationHelper
import com.example.viewmodel.CricketViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

    private lateinit var viewModel: CricketViewModel

    @Before
    fun setup() {
        viewModel = CricketViewModel()
    }

    @Test
    fun testInitialMatchState() {
        val match = viewModel.uiState.value.liveMatch
        assertNotNull(match)
        assertEquals("Champions Premier Trophy 2026", match.tournamentName)
        assertEquals(MatchStatus.LIVE, match.status)
        assertTrue(match.inningsList.size >= 2)
        val inn2 = match.currentInnings
        assertTrue(inn2.totalRuns > 0)
    }

    @Test
    fun testRecordBall_BoundaryFour() {
        val initialRuns = viewModel.uiState.value.liveMatch.currentInnings.totalRuns
        viewModel.recordBall(runs = 4)
        val updatedRuns = viewModel.uiState.value.liveMatch.currentInnings.totalRuns
        assertEquals(initialRuns + 4, updatedRuns)
    }

    @Test
    fun testRecordBall_Wicket() {
        val initialWickets = viewModel.uiState.value.liveMatch.currentInnings.wickets
        viewModel.recordBall(runs = 0, isWicket = true, wicketType = "Caught")
        val updatedWickets = viewModel.uiState.value.liveMatch.currentInnings.wickets
        assertEquals(initialWickets + 1, updatedWickets)
    }

    @Test
    fun testUndoLastBall() {
        val initialRuns = viewModel.uiState.value.liveMatch.currentInnings.totalRuns
        viewModel.recordBall(runs = 6)
        assertEquals(initialRuns + 6, viewModel.uiState.value.liveMatch.currentInnings.totalRuns)

        viewModel.undoLastBall()
        assertEquals(initialRuns, viewModel.uiState.value.liveMatch.currentInnings.totalRuns)
    }

    @Test
    fun testSwapStrike() {
        val initialStriker = viewModel.uiState.value.liveMatch.currentStrikerId
        val initialNonStriker = viewModel.uiState.value.liveMatch.currentNonStrikerId

        viewModel.swapStrike()

        assertEquals(initialNonStriker, viewModel.uiState.value.liveMatch.currentStrikerId)
        assertEquals(initialStriker, viewModel.uiState.value.liveMatch.currentNonStrikerId)
    }

    @Test
    fun testTwoFactorVerification() {
        val correct = viewModel.verifyTwoFactorPin("7392")
        assertTrue(correct)
        assertTrue(viewModel.uiState.value.userProfile.twoFactorEnabled)

        val wrong = viewModel.verifyTwoFactorPin("0000")
        assertFalse(wrong)
    }

    @Test
    fun testLocalizationSupport() {
        val hindiTitle = LocalizationHelper.getString(AppLanguage.HINDI, "app_title")
        assertEquals("नम्मा क्रिकेट", hindiTitle)

        val spanishScorecard = LocalizationHelper.getString(AppLanguage.SPANISH, "scorecard")
        assertEquals("Marcador Completo", spanishScorecard)

        val urduLive = LocalizationHelper.getString(AppLanguage.URDU, "live_now")
        assertEquals("براہ راست", urduLive)

        val tamilOvers = LocalizationHelper.getString(AppLanguage.TAMIL, "overs")
        assertEquals("ஓவர்கள்", tamilOvers)
    }

    @Test
    fun testPaymentProcessing() {
        val initialNotifs = viewModel.uiState.value.notifications.size
        viewModel.processRegistrationPayment("Thunderbolts CC", "Rohan Mehra", 250.0, "UPI")
        assertEquals(initialNotifs + 1, viewModel.uiState.value.notifications.size)
    }

    @Test
    fun testTournamentLeaderboardSorting() {
        val tourney = viewModel.uiState.value.tournaments.first()
        val standings = tourney.standings
        assertTrue(standings.isNotEmpty())

        val topTeam = standings.maxByOrNull { it.points }
        assertNotNull(topTeam)
        assertEquals("Mumbai Strikers", topTeam?.teamName)
        assertEquals(10, topTeam?.points)
        assertTrue(topTeam!!.netRunRate > 0)
    }
}
