package com.example.data.model

data class Tournament(
    val id: String,
    val name: String,
    val edition: String,
    val organizer: String,
    val location: String,
    val startDate: String,
    val endDate: String,
    val format: TournamentFormat,
    val teamsCount: Int,
    val totalMatches: Int,
    val registrationFee: Double,
    val prizePool: String,
    val status: TournamentStatus,
    val bannerUrl: String = "",
    val sponsors: List<Sponsor> = emptyList(),
    val standings: List<StandingRow> = emptyList(),
    val rulesDocumentName: String = "Tournament_Rulebook_2026.pdf"
)

enum class TournamentFormat {
    LEAGUE_KNOCKOUT, ROUND_ROBIN, KNOCKOUT_CUP
}

enum class TournamentStatus {
    UPCOMING, ONGOING, COMPLETED
}

data class StandingRow(
    val rank: Int,
    val teamId: String,
    val teamName: String,
    val shortName: String,
    val matchesPlayed: Int,
    val won: Int,
    val lost: Int,
    val tied: Int = 0,
    val netRunRate: Double,
    val points: Int,
    val noResult: Int = 0,
    val runsFor: Int = 0,
    val oversFor: Double = 0.0,
    val runsAgainst: Int = 0,
    val oversAgainst: Double = 0.0,
    val recentForm: List<String> = listOf("W", "W", "L", "W", "L"),
    val primaryColorHex: Long = 0xFF005BAC
)

data class Sponsor(
    val id: String,
    val name: String,
    val category: String, // e.g. "Title Sponsor", "Beverage Partner", "Apparel"
    val contributionAmount: Double,
    val status: String = "Active"
)

data class RegistrationPayment(
    val transactionId: String,
    val tournamentId: String,
    val teamName: String,
    val captainName: String,
    val amount: Double,
    val paymentMethod: String, // e.g., "UPI", "Credit Card", "Net Banking"
    val status: String = "SUCCESS",
    val timestamp: String = "Just now"
)
