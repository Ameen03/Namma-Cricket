package com.example.data.model

data class Match(
    val id: String,
    val tournamentId: String,
    val tournamentName: String,
    val venue: String,
    val matchDate: String,
    val matchType: String = "T20 Match",
    val oversLimit: Int = 20,
    val teamA: Team,
    val teamB: Team,
    val tossDetails: String,
    val status: MatchStatus,
    val resultSummary: String,
    val currentBattingTeamIndex: Int = 0, // 0 for teamA, 1 for teamB
    val inningsList: List<Innings>,
    val currentStrikerId: String,
    val currentNonStrikerId: String,
    val currentBowlerId: String,
    val concurrentViewers: Int = 12450,
    val isLiveStreaming: Boolean = true,
    val currentCameraAngle: String = "Main Pitch",
    val fanPollTeamAVotes: Int = 1420,
    val fanPollTeamBVotes: Int = 980
) {
    val currentInnings: Innings
        get() = inningsList.getOrNull(currentBattingTeamIndex) ?: inningsList.first()

    val currentBattingTeam: Team
        get() = if (currentBattingTeamIndex == 0) teamA else teamB

    val currentBowlingTeam: Team
        get() = if (currentBattingTeamIndex == 0) teamB else teamA
}

enum class MatchStatus {
    LIVE, UPCOMING, COMPLETED
}

data class Innings(
    val teamId: String,
    val totalRuns: Int,
    val wickets: Int,
    val legalBalls: Int, // e.g. 94 balls = 15.4 overs
    val extras: Extras = Extras(),
    val batsmanStats: List<BattingStat>,
    val bowlerStats: List<BowlingStat>,
    val fallOfWickets: List<FallOfWicket> = emptyList(),
    val ballHistory: List<BallEvent> = emptyList(),
    val commentary: List<CommentaryEntry> = emptyList()
) {
    val oversFormatted: String
        get() = "${legalBalls / 6}.${legalBalls % 6}"

    val currentRunRate: Double
        get() = if (legalBalls == 0) 0.0 else (totalRuns.toDouble() * 6.0) / legalBalls.toDouble()
}

data class Extras(
    val wides: Int = 0,
    val noBalls: Int = 0,
    val byes: Int = 0,
    val legByes: Int = 0
) {
    val total: Int get() = wides + noBalls + byes + legByes
}

data class BattingStat(
    val playerId: String,
    val name: String,
    val runs: Int,
    val balls: Int,
    val fours: Int,
    val sixes: Int,
    val isOut: Boolean = false,
    val dismissalInfo: String = "not out"
) {
    val strikeRate: Double
        get() = if (balls == 0) 0.0 else (runs.toDouble() * 100.0) / balls.toDouble()
}

data class BowlingStat(
    val playerId: String,
    val name: String,
    val legalBalls: Int, // 24 = 4.0 overs
    val maidens: Int,
    val runsConceded: Int,
    val wickets: Int,
    val noBalls: Int = 0,
    val wides: Int = 0
) {
    val oversFormatted: String
        get() = "${legalBalls / 6}.${legalBalls % 6}"

    val economy: Double
        get() = if (legalBalls == 0) 0.0 else (runsConceded.toDouble() * 6.0) / legalBalls.toDouble()
}

data class BallEvent(
    val id: String,
    val overNumber: Int,
    val ballNumber: Int, // 1 to 6
    val runs: Int,
    val isWicket: Boolean = false,
    val wicketType: String = "",
    val dismissedPlayerName: String = "",
    val isWide: Boolean = false,
    val isNoBall: Boolean = false,
    val isBye: Boolean = false,
    val isLegBye: Boolean = false,
    val bowlerName: String,
    val batsmanName: String,
    val wagonAngleDeg: Float = 0f, // 0 to 360 deg on cricket field
    val shotType: String = "Drive",
    val commentaryText: String
)

data class CommentaryEntry(
    val id: String,
    val overBall: String,
    val title: String,
    val description: String,
    val badgeType: CommentaryBadge = CommentaryBadge.NORMAL,
    val timestamp: String = "Just now"
)

enum class CommentaryBadge {
    NORMAL, FOUR, SIX, WICKET, MILESTONE, EXTRA
}

data class FallOfWicket(
    val wicketNumber: Int,
    val runs: Int,
    val playerName: String,
    val over: String
)

data class Team(
    val id: String,
    val name: String,
    val shortName: String,
    val city: String,
    val primaryColorHex: Long = 0xFF0F8A5F,
    val squad: List<Player> = emptyList()
)

data class Player(
    val id: String,
    val name: String,
    val role: PlayerRole,
    val battingStyle: String,
    val bowlingStyle: String,
    val matches: Int,
    val runs: Int,
    val highestScore: Int,
    val battingAverage: Double,
    val strikeRate: Double,
    val wickets: Int,
    val economy: Double,
    val bestBowling: String,
    val catches: Int,
    val mvpPoints: Int,
    val country: String = "India",
    val recentScores: List<Int> = listOf(45, 82, 12, 104, 38)
)

enum class PlayerRole {
    BATTER, BOWLER, ALL_ROUNDER, WICKET_KEEPER
}
