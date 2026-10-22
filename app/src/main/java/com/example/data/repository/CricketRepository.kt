package com.example.data.repository

import com.example.data.model.*

object CricketRepository {

    val samplePlayers = listOf(
        Player(
            id = "PL_01",
            name = "Rohit Varma",
            role = PlayerRole.BATTER,
            battingStyle = "Right-hand bat",
            bowlingStyle = "Right-arm offbreak",
            matches = 42,
            runs = 1680,
            highestScore = 118,
            battingAverage = 44.2,
            strikeRate = 148.6,
            wickets = 4,
            economy = 8.1,
            bestBowling = "2/18",
            catches = 22,
            mvpPoints = 640,
            recentScores = listOf(68, 45, 112, 18, 54)
        ),
        Player(
            id = "PL_02",
            name = "Virat Kulkarni",
            role = PlayerRole.BATTER,
            battingStyle = "Right-hand bat",
            bowlingStyle = "Right-arm medium",
            matches = 50,
            runs = 2150,
            highestScore = 124,
            battingAverage = 51.1,
            strikeRate = 139.4,
            wickets = 6,
            economy = 7.8,
            bestBowling = "2/14",
            catches = 35,
            mvpPoints = 810,
            recentScores = listOf(84, 52, 91, 10, 77)
        ),
        Player(
            id = "PL_03",
            name = "Jasprit Khan",
            role = PlayerRole.BOWLER,
            battingStyle = "Right-hand bat",
            bowlingStyle = "Right-arm fast yorker",
            matches = 38,
            runs = 112,
            highestScore = 24,
            battingAverage = 12.4,
            strikeRate = 115.0,
            wickets = 68,
            economy = 6.4,
            bestBowling = "5/14",
            catches = 14,
            mvpPoints = 730,
            recentScores = listOf(4, 12, 0, 8, 16)
        ),
        Player(
            id = "PL_04",
            name = "Hardik Patel",
            role = PlayerRole.ALL_ROUNDER,
            battingStyle = "Right-hand bat",
            bowlingStyle = "Right-arm fast medium",
            matches = 40,
            runs = 1140,
            highestScore = 88,
            battingAverage = 35.6,
            strikeRate = 162.5,
            wickets = 44,
            economy = 8.4,
            bestBowling = "4/22",
            catches = 28,
            mvpPoints = 790,
            recentScores = listOf(34, 51, 19, 42, 60)
        ),
        Player(
            id = "PL_05",
            name = "Rashid Qureshi",
            role = PlayerRole.BOWLER,
            battingStyle = "Right-hand bat",
            bowlingStyle = "Right-arm legbreak googly",
            matches = 44,
            runs = 340,
            highestScore = 48,
            battingAverage = 18.8,
            strikeRate = 136.0,
            wickets = 72,
            economy = 6.2,
            bestBowling = "5/11",
            catches = 19,
            mvpPoints = 760,
            recentScores = listOf(14, 2, 28, 0, 7)
        ),
        Player(
            id = "PL_06",
            name = "Kishan Yadav",
            role = PlayerRole.WICKET_KEEPER,
            battingStyle = "Left-hand bat",
            bowlingStyle = "None",
            matches = 34,
            runs = 980,
            highestScore = 94,
            battingAverage = 32.6,
            strikeRate = 142.0,
            wickets = 0,
            economy = 0.0,
            bestBowling = "None",
            catches = 38,
            mvpPoints = 510,
            recentScores = listOf(29, 62, 14, 5, 48)
        )
    )

    val teamMumbai = Team(
        id = "TM_MUMBAI",
        name = "Mumbai Strikers",
        shortName = "MUM",
        city = "Mumbai",
        primaryColorHex = 0xFF005BAC,
        squad = samplePlayers.take(4)
    )

    val teamDelhi = Team(
        id = "TM_DELHI",
        name = "Delhi Titans",
        shortName = "DEL",
        city = "Delhi",
        primaryColorHex = 0xFFD71920,
        squad = samplePlayers.drop(2)
    )

    val teamBangalore = Team(
        id = "TM_BANGALORE",
        name = "Bangalore Royals",
        shortName = "BLR",
        city = "Bengaluru",
        primaryColorHex = 0xFFC70039,
        squad = samplePlayers.take(5)
    )

    val teamChennai = Team(
        id = "TM_CHENNAI",
        name = "Chennai Super Kings",
        shortName = "CSK",
        city = "Chennai",
        primaryColorHex = 0xFFFDB913,
        squad = samplePlayers.reversed()
    )

    val initialLiveMatch: Match = createInitialLiveMatch()

    private fun createInitialLiveMatch(): Match {
        val inn1 = Innings(
            teamId = teamDelhi.id,
            totalRuns = 181,
            wickets = 6,
            legalBalls = 120, // 20.0 overs
            extras = Extras(wides = 6, noBalls = 2, byes = 2, legByes = 1),
            batsmanStats = listOf(
                BattingStat("PL_04", "Hardik Patel", 64, 38, 5, 4, true, "c Varma b Khan"),
                BattingStat("PL_05", "Rashid Qureshi", 38, 22, 3, 2, true, "b Khan"),
                BattingStat("PL_06", "Kishan Yadav", 52, 34, 4, 3, false, "not out")
            ),
            bowlerStats = listOf(
                BowlingStat("PL_01", "Rohit Varma", 24, 0, 42, 1, 0, 2),
                BowlingStat("PL_03", "Jasprit Khan", 24, 1, 28, 4, 1, 1)
            ),
            fallOfWickets = listOf(
                FallOfWicket(1, 28, "Kapil Rao", "3.2"),
                FallOfWicket(2, 64, "Hardik Patel", "8.5"),
                FallOfWicket(3, 132, "Rashid Qureshi", "15.4")
            )
        )

        val initialBallHistory = listOf(
            BallEvent("B_01", 16, 1, 1, false, "", "", false, false, false, false, "Rashid Qureshi", "Rohit Varma", 75f, "Cover Drive", "Rohit drives gracefully through extra cover for a brisk single."),
            BallEvent("B_02", 16, 2, 4, false, "", "", false, false, false, false, "Rashid Qureshi", "Virat Kulkarni", 210f, "Square Cut", "FOUR! Pierces the backward point boundary with razor-sharp timing!"),
            BallEvent("B_03", 16, 3, 6, false, "", "", false, false, false, false, "Rashid Qureshi", "Virat Kulkarni", 330f, "Pull Shot", "SIX! Into the second tier over deep mid-wicket! Towering 96m hit!"),
            BallEvent("B_04", 16, 4, 0, false, "", "", false, false, false, false, "Rashid Qureshi", "Virat Kulkarni", 0f, "Defensive", "Beaten outside off! Good comeback wrong'un from Rashid."),
            BallEvent("B_05", 16, 5, 2, false, "", "", false, false, false, false, "Rashid Qureshi", "Virat Kulkarni", 140f, "Flick", "Pushed through mid-wicket, rapid sprint between wickets for two."),
            BallEvent("B_06", 16, 6, 1, false, "", "", false, false, false, false, "Rashid Qureshi", "Virat Kulkarni", 90f, "Push", "Singled down to long-off to retain strike.")
        )

        val initialCommentary = listOf(
            CommentaryEntry("C_01", "16.6", "Single to Long-Off", "Virat taps down the pitch towards long-off to keep strike for the 18th over.", CommentaryBadge.NORMAL),
            CommentaryEntry("C_02", "16.5", "Two Runs - Quick Running", "Steered softly to deep mid-wicket, aggressive calling and good turn on the crease.", CommentaryBadge.NORMAL),
            CommentaryEntry("C_03", "16.3", "MASSIVE SIX! 96m", "Colossal hit! Sits back and launches the leg-spinner high over cow corner deep into the roaring stands!", CommentaryBadge.SIX),
            CommentaryEntry("C_04", "16.2", "CRACKING FOUR!", "Short and wide, slapped off the backfoot through backward point! Unstoppable boundary.", CommentaryBadge.FOUR),
            CommentaryEntry("C_05", "15.6", "MILESTONE: Fifty for Virat Kulkarni", "Raises his bat! A commanding half-century off just 31 deliveries under pressure.", CommentaryBadge.MILESTONE)
        )

        val inn2 = Innings(
            teamId = teamMumbai.id,
            totalRuns = 152,
            wickets = 3,
            legalBalls = 102, // 17.0 overs
            extras = Extras(wides = 4, noBalls = 1, byes = 1, legByes = 2),
            batsmanStats = listOf(
                BattingStat("PL_01", "Rohit Varma", 48, 30, 5, 2, true, "c Yadav b Patel"),
                BattingStat("PL_02", "Virat Kulkarni", 68, 39, 7, 3, false, "not out"),
                BattingStat("PL_04", "Sanju Samson", 24, 18, 2, 1, true, "b Qureshi"),
                BattingStat("PL_06", "Surya Dev", 10, 6, 1, 0, false, "not out")
            ),
            bowlerStats = listOf(
                BowlingStat("PL_05", "Rashid Qureshi", 24, 0, 36, 1, 0, 1),
                BowlingStat("PL_04", "Hardik Patel", 18, 0, 26, 1, 1, 1),
                BowlingStat("PL_03", "Jasprit Khan", 18, 0, 24, 1, 0, 0)
            ),
            fallOfWickets = listOf(
                FallOfWicket(1, 74, "Rohit Varma", "7.4"),
                FallOfWicket(2, 118, "Sanju Samson", "13.2"),
                FallOfWicket(3, 142, "Ishan Sharma", "15.5")
            ),
            ballHistory = initialBallHistory,
            commentary = initialCommentary
        )

        return Match(
            id = "MATCH_LIVE_01",
            tournamentId = "TRN_2026_01",
            tournamentName = "Champions Premier Trophy 2026",
            venue = "Wankhede Stadium, Mumbai",
            matchDate = "Today, 19:30 IST",
            oversLimit = 20,
            teamA = teamDelhi,
            teamB = teamMumbai,
            tossDetails = "Mumbai Strikers won toss & elected to bowl first",
            status = MatchStatus.LIVE,
            resultSummary = "Mumbai needs 30 runs in 18 balls (Target: 182)",
            currentBattingTeamIndex = 1, // Mumbai batting
            inningsList = listOf(inn1, inn2),
            currentStrikerId = "PL_02",
            currentNonStrikerId = "PL_06",
            currentBowlerId = "PL_03",
            concurrentViewers = 34820,
            isLiveStreaming = true,
            currentCameraAngle = "Bowler Run-Up",
            fanPollTeamAVotes = 1820,
            fanPollTeamBVotes = 2950
        )
    }

    val sampleTournaments = listOf(
        Tournament(
            id = "TRN_2026_01",
            name = "Champions Premier Trophy 2026",
            edition = "Season 8",
            organizer = "CricHeroes National Board",
            location = "Mumbai & Delhi, India",
            startDate = "Oct 15, 2026",
            endDate = "Nov 12, 2026",
            format = TournamentFormat.LEAGUE_KNOCKOUT,
            teamsCount = 8,
            totalMatches = 32,
            registrationFee = 250.0,
            prizePool = "$50,000 USD",
            status = TournamentStatus.ONGOING,
            bannerUrl = "cricket_stadium_banner",
            sponsors = listOf(
                Sponsor("SP_01", "Titan Energy Drink", "Title Sponsor", 15000.0),
                Sponsor("SP_02", "Kookaburra Gear", "Official Ball & Kit Partner", 8000.0),
                Sponsor("SP_03", "CloudNine Hosting", "Streaming Technology Partner", 6500.0)
            ),
            standings = listOf(
                StandingRow(1, "TM_MUMBAI", "Mumbai Strikers", "MUM", 6, 5, 1, 0, +1.285, 10, noResult = 0, runsFor = 1140, oversFor = 118.2, runsAgainst = 988, oversAgainst = 120.0, recentForm = listOf("W", "W", "L", "W", "W"), primaryColorHex = 0xFF005BAC),
                StandingRow(2, "TM_DELHI", "Delhi Titans", "DEL", 6, 4, 2, 0, +0.942, 8, noResult = 0, runsFor = 1085, oversFor = 116.4, runsAgainst = 975, oversAgainst = 117.2, recentForm = listOf("W", "L", "W", "W", "W"), primaryColorHex = 0xFFD71920),
                StandingRow(3, "TM_CHENNAI", "Chennai Super Kings", "CSK", 6, 4, 2, 0, +0.410, 8, noResult = 0, runsFor = 1020, oversFor = 119.0, runsAgainst = 970, oversAgainst = 119.0, recentForm = listOf("L", "W", "W", "L", "W"), primaryColorHex = 0xFFFDB913),
                StandingRow(4, "TM_BANGALORE", "Bangalore Royals", "BLR", 6, 3, 3, 0, -0.215, 6, noResult = 0, runsFor = 995, oversFor = 120.0, runsAgainst = 1020, oversAgainst = 118.4, recentForm = listOf("W", "L", "L", "W", "L"), primaryColorHex = 0xFFC70039),
                StandingRow(5, "TM_KOLKATA", "Kolkata Knights", "KKR", 6, 2, 4, 0, -0.890, 4, noResult = 0, runsFor = 940, oversFor = 118.0, runsAgainst = 1045, oversAgainst = 117.0, recentForm = listOf("L", "L", "W", "L", "W"), primaryColorHex = 0xFF3A225D),
                StandingRow(6, "TM_HYDERABAD", "Deccan Eagles", "DEC", 6, 1, 5, 0, -1.350, 2, noResult = 0, runsFor = 890, oversFor = 117.2, runsAgainst = 1052, oversAgainst = 115.0, recentForm = listOf("L", "L", "L", "W", "L"), primaryColorHex = 0xFFFF822A),
                StandingRow(7, "TM_PUNJAB", "Punjab Kings", "PBKS", 6, 1, 5, 0, -1.480, 2, noResult = 0, runsFor = 870, oversFor = 116.0, runsAgainst = 1045, oversAgainst = 114.2, recentForm = listOf("L", "W", "L", "L", "L"), primaryColorHex = 0xFFDD1F2D),
                StandingRow(8, "TM_RAJASTHAN", "Rajasthan Royals", "RR", 6, 1, 5, 0, -1.620, 2, noResult = 0, runsFor = 840, oversFor = 115.0, runsAgainst = 1030, oversAgainst = 113.0, recentForm = listOf("L", "L", "L", "L", "W"), primaryColorHex = 0xFFEA1A85)
            )
        ),
        Tournament(
            id = "TRN_2026_02",
            name = "Corporate Super Bash 2026",
            edition = "Winter Edition",
            organizer = "Metro Sports Club",
            location = "London Oval & Lord's Grounds",
            startDate = "Nov 01, 2026",
            endDate = "Dec 05, 2026",
            format = TournamentFormat.ROUND_ROBIN,
            teamsCount = 12,
            totalMatches = 48,
            registrationFee = 180.0,
            prizePool = "£25,000 GBP",
            status = TournamentStatus.UPCOMING,
            bannerUrl = "cricket_stadium_banner",
            sponsors = listOf(
                Sponsor("SP_04", "Barclays Sports", "Lead Sponsor", 10000.0),
                Sponsor("SP_05", "Gray-Nicolls", "Equipment Sponsor", 5000.0)
            ),
            standings = listOf(
                StandingRow(1, "TM_LONDON", "London Lions", "LON", 0, 0, 0, 0, 0.0, 0),
                StandingRow(2, "TM_SURREY", "Surrey Smashers", "SUR", 0, 0, 0, 0, 0.0, 0)
            )
        )
    )

    val sampleHighlights = listOf(
        HighlightClip(
            id = "HL_01",
            title = "Virat Kulkarni 96m Mammoth Six into the Pavilion!",
            matchName = "MUM vs DEL - Super 12",
            durationSec = 42,
            viewsCount = 18290,
            likesCount = 3410,
            category = "Sixes",
            timestamp = "15 mins ago"
        ),
        HighlightClip(
            id = "HL_02",
            title = "Jasprit Khan Unplayable Yorker Shatters Middle Stump!",
            matchName = "MUM vs DEL - Super 12",
            durationSec = 35,
            viewsCount = 29400,
            likesCount = 5820,
            category = "Wickets",
            timestamp = "40 mins ago"
        ),
        HighlightClip(
            id = "HL_03",
            title = "Gravity-Defying Diving Catch at Deep Square Leg",
            matchName = "CSK vs BLR - Match 18",
            durationSec = 28,
            viewsCount = 14300,
            likesCount = 2100,
            category = "Best Moments",
            timestamp = "Yesterday"
        ),
        HighlightClip(
            id = "HL_04",
            title = "Last Ball Thriller: 4 Runs Needed Off 1 Ball!",
            matchName = "KOL vs DEC - Match 14",
            durationSec = 56,
            viewsCount = 42100,
            likesCount = 9200,
            category = "Best Moments",
            timestamp = "2 days ago"
        )
    )

    val sampleNotifications = listOf(
        NotificationItem(
            id = "NOTIF_01",
            title = "FIFTY! Virat Kulkarni on fire!",
            message = "Virat reaches 50* runs off 31 balls with a massive six! Mumbai needs 30 from 18 balls.",
            timestamp = "5m ago",
            type = NotificationType.MILESTONE_ALERT
        ),
        NotificationItem(
            id = "NOTIF_02",
            title = "WICKET ALERT!",
            message = "Rashid Qureshi bowls Sanju Samson! Catch taken at mid-off. Match on knife edge!",
            timestamp = "18m ago",
            type = NotificationType.WICKET_ALERT
        ),
        NotificationItem(
            id = "NOTIF_03",
            title = "Tournament Schedule Update",
            message = "Quarter Finals match timings have been updated for Corporate Super Bash 2026.",
            timestamp = "2h ago",
            type = NotificationType.SCHEDULE_CHANGE
        ),
        NotificationItem(
            id = "NOTIF_04",
            title = "Registration Fee Confirmed",
            message = "Registration payment of $250.00 confirmed for Delhi Titans via Razorpay/Stripe Gateway.",
            timestamp = "1d ago",
            type = NotificationType.TOURNAMENT_ANNOUNCEMENT
        )
    )

    val sampleWormData = listOf(
        WormRunPoint(2, 14, 18),
        WormRunPoint(4, 32, 38),
        WormRunPoint(6, 48, 56),
        WormRunPoint(8, 68, 74),
        WormRunPoint(10, 88, 92),
        WormRunPoint(12, 108, 110),
        WormRunPoint(14, 128, 124),
        WormRunPoint(16, 146, 144),
        WormRunPoint(17, 155, 152),
        WormRunPoint(20, 181, 182)
    )

    val sampleViewerRetention = listOf(
        ViewerRetentionPoint("Over 1-5", 14200),
        ViewerRetentionPoint("Over 6-10", 19500),
        ViewerRetentionPoint("Over 11-15", 26800),
        ViewerRetentionPoint("Over 16-18", 34820),
        ViewerRetentionPoint("Death Overs", 41200)
    )

    val initialLiveChat = listOf(
        LiveChatMessage("MSG_1", "CricketFan_99", "Virat is playing a captain's knock! Unbelievable shots! 🏏🔥", "19:42"),
        LiveChatMessage("MSG_2", "YorkerKing", "Watch out for Bumrah's slower balls in the 19th over!", "19:43"),
        LiveChatMessage("MSG_3", "Rahul D.", "What a stadium atmosphere tonight in Wankhede!", "19:43"),
        LiveChatMessage("MSG_4", "SuperStrikers_Official", "30 needed off 18 balls! Let's bring this home! 🏆", "19:44", isHighlight = true)
    )
}
