package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.CricketUiState

@Composable
fun MatchDetailScreen(
    uiState: CricketUiState,
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
    onShareMatch: () -> Unit,
    onExportPdf: () -> Unit,
    onCameraAngleSelect: (String) -> Unit,
    onSendChatMessage: (String) -> Unit,
    onCrowdCheer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val match = uiState.liveMatch
    val tabs = listOf("Live", "Scorecard", "Commentary", "Charts", "Live Stream")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("match_detail_screen")
    ) {
        // Sticky Mini Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CricketNavySurface)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${match.teamA.shortName} vs ${match.teamB.shortName}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${match.inningsList.first().totalRuns}/${match.inningsList.first().wickets} vs ${match.currentInnings.totalRuns}/${match.currentInnings.wickets} (${match.currentInnings.oversFormatted} ov)",
                    color = CricketGreenLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onExportPdf,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = "Export PDF", tint = Color.White, modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = onShareMatch,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Tab Selector Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = CricketNavySurface,
            contentColor = CricketGreenLight,
            edgePadding = 12.dp,
            divider = { Divider(color = BorderDark) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = (selectedTab == index),
                    onClick = { onTabSelect(index) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            color = if (selectedTab == index) CricketGreenLight else TextMuted
                        )
                    }
                )
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CricketNavy)
        ) {
            when (selectedTab) {
                0 -> MatchLiveSummaryTab(match = match, onShare = onShareMatch, onExport = onExportPdf)
                1 -> FullScorecardTab(match = match)
                2 -> BallByBallCommentaryTab(commentary = match.currentInnings.commentary)
                3 -> MatchChartsTab(match = match, wormData = uiState.wormData, viewerRetention = uiState.viewerRetention)
                4 -> LiveStreamPlayerTab(
                    match = match,
                    chatMessages = uiState.liveChatMessages,
                    onCameraAngle = onCameraAngleSelect,
                    onSendMessage = onSendChatMessage,
                    onCheer = onCrowdCheer
                )
            }
        }
    }
}

@Composable
fun MatchLiveSummaryTab(
    match: Match,
    onShare: () -> Unit,
    onExport: () -> Unit
) {
    val inn2 = match.currentInnings

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            LiveMatchHeroCard(
                match = match,
                onCardClick = {},
                onShareClick = onShare,
                onStreamClick = {}
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Current Partnership",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val striker = inn2.batsmanStats.find { it.playerId == match.currentStrikerId }
                    val nonStriker = inn2.batsmanStats.find { it.playerId == match.currentNonStrikerId }
                    val pRuns = (striker?.runs ?: 24) + (nonStriker?.runs ?: 14)
                    val pBalls = (striker?.balls ?: 18) + (nonStriker?.balls ?: 10)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${striker?.name ?: "Striker"} (${striker?.runs ?: 24})", color = Color.White, fontSize = 12.sp)
                        Text(text = "$pRuns runs ($pBalls balls)", color = CricketGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "${nonStriker?.name ?: "Non-Striker"} (${nonStriker?.runs ?: 14})", color = TextMuted, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { 0.65f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = CricketGreenLight,
                        trackColor = CricketNavyElevated
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onShare,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share Score", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onExport,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = CricketNavyElevated),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Scorecard Doc", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FullScorecardTab(match: Match) {
    var selectedInningsIndex by remember { mutableIntStateOf(1) } // Default 2nd innings
    val displayedInnings = match.inningsList[selectedInningsIndex]
    val battingTeam = if (selectedInningsIndex == 0) match.teamA else match.teamB

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("full_scorecard_tab"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Innings Switcher Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CricketNavySurface)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                match.inningsList.forEachIndexed { idx, inn ->
                    val team = if (idx == 0) match.teamA else match.teamB
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedInningsIndex == idx) CricketGreenPrimary else Color.Transparent)
                            .clickable { selectedInningsIndex = idx }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${team.shortName}: ${inn.totalRuns}/${inn.wickets} (${inn.oversFormatted})",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Batting Table
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "BATSMAN", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                        Text(text = "R", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
                        Text(text = "B", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
                        Text(text = "4s", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                        Text(text = "6s", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                        Text(text = "SR", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                    }

                    Divider(color = BorderDark, modifier = Modifier.padding(vertical = 8.dp))

                    displayedInnings.batsmanStats.forEach { bat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(2f)) {
                                Text(text = bat.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = bat.dismissalInfo, color = TextMuted, fontSize = 10.sp)
                            }
                            Text(text = "${bat.runs}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
                            Text(text = "${bat.balls}", color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
                            Text(text = "${bat.fours}", color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                            Text(text = "${bat.sixes}", color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                            Text(text = String.format("%.1f", bat.strikeRate), color = CricketGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                        }
                    }

                    Divider(color = BorderDark, modifier = Modifier.padding(vertical = 8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Extras: ${displayedInnings.extras.total} (w ${displayedInnings.extras.wides}, nb ${displayedInnings.extras.noBalls}, b ${displayedInnings.extras.byes}, lb ${displayedInnings.extras.legByes})", color = TextMuted, fontSize = 11.sp)
                        Text(text = "Total: ${displayedInnings.totalRuns}/${displayedInnings.wickets} (${displayedInnings.oversFormatted} ov)", color = CricketGreenLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Bowling Table
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "BOWLER", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                        Text(text = "O", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
                        Text(text = "M", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                        Text(text = "R", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
                        Text(text = "W", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
                        Text(text = "ECON", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                    }

                    Divider(color = BorderDark, modifier = Modifier.padding(vertical = 8.dp))

                    displayedInnings.bowlerStats.forEach { bowl ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = bowl.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(2f))
                            Text(text = bowl.oversFormatted, color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
                            Text(text = "${bowl.maidens}", color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                            Text(text = "${bowl.runsConceded}", color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
                            Text(text = "${bowl.wickets}", color = CricketRedWicket, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
                            Text(text = String.format("%.1f", bowl.economy), color = CricketGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BallByBallCommentaryTab(commentary: List<CommentaryEntry>) {
    var filterType by remember { mutableStateOf<CommentaryBadge?>(null) }

    val filteredList = remember(commentary, filterType) {
        if (filterType == null) commentary else commentary.filter { it.badgeType == filterType }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = (filterType == null),
                onClick = { filterType = null },
                label = { Text("All Deliveries") }
            )
            FilterChip(
                selected = (filterType == CommentaryBadge.WICKET),
                onClick = { filterType = if (filterType == CommentaryBadge.WICKET) null else CommentaryBadge.WICKET },
                label = { Text("Wickets Only") }
            )
            FilterChip(
                selected = (filterType == CommentaryBadge.SIX || filterType == CommentaryBadge.FOUR),
                onClick = { filterType = if (filterType == CommentaryBadge.SIX) null else CommentaryBadge.SIX },
                label = { Text("Boundaries") }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("commentary_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredList) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Over Ball Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when (item.badgeType) {
                                        CommentaryBadge.WICKET -> CricketRedWicket
                                        CommentaryBadge.SIX -> CricketGold
                                        CommentaryBadge.FOUR -> CricketBlueBoundary
                                        else -> CricketNavyElevated
                                    }
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = item.overBall,
                                color = if (item.badgeType == CommentaryBadge.SIX) CricketNavy else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.title,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = item.timestamp,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.description,
                                color = TextMuted,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchChartsTab(
    match: Match,
    wormData: List<WormRunPoint>,
    viewerRetention: List<ViewerRetentionPoint>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WagonWheelCanvas(balls = match.currentInnings.ballHistory)
        }

        item {
            WormRunComparisonChart(
                points = wormData,
                teamAName = match.teamA.shortName,
                teamBName = match.teamB.shortName
            )
        }

        item {
            ViewerRetentionChart(retentionData = viewerRetention)
        }
    }
}

@Composable
fun LiveStreamPlayerTab(
    match: Match,
    chatMessages: List<LiveChatMessage>,
    onCameraAngle: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onCheer: (String) -> Unit
) {
    var chatInput by remember { mutableStateOf("") }
    val cameraAngles = listOf("Main Pitch", "Bowler Run-Up", "Batsman Close-Up", "Stump Cam")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("live_stream_player_tab")
    ) {
        // Video Broadcast Simulation Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(Color.Black)
        ) {
            // Simulated feed background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF0F3B2A), Color(0xFF04140D))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LiveTv,
                        contentDescription = null,
                        tint = CricketGreenLight,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "CRICHEROES LIVE HD BROADCAST",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Camera: ${match.currentCameraAngle} • 1080p 60fps",
                        color = CricketGold,
                        fontSize = 11.sp
                    )
                }
            }

            // Top HUD Overlay: Live pill & Viewers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CricketRedWicket)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("LIVE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${match.concurrentViewers} watching", color = Color.White, fontSize = 10.sp)
                }
            }

            // Bottom Scoreboard Overlay Ticker
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${match.teamB.shortName} ${match.currentInnings.totalRuns}/${match.currentInnings.wickets} (${match.currentInnings.oversFormatted} ov)",
                        color = CricketGreenLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = match.resultSummary,
                        color = Color.White,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Camera Angle Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CricketNavySurface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            cameraAngles.forEach { angle ->
                val isSelected = match.currentCameraAngle == angle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CricketGreenPrimary else CricketNavyElevated)
                        .clickable { onCameraAngle(angle) }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Text(text = angle, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Live Fan Reactions Row (Cheer bar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CricketNavyElevated)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Cheer:", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            listOf("🏏 Bat", "🔥 Fire", "👏 Clap", "💥 Six", "🎉 Win").forEach { cheer ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CricketNavySurface)
                        .clickable { onCheer(cheer) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = cheer, color = Color.White, fontSize = 11.sp)
                }
            }
        }

        // Live Chat Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(chatMessages) { chat ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (chat.isHighlight) CricketGreenDark.copy(alpha = 0.5f) else Color.Transparent)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(text = "${chat.senderName}: ", color = CricketGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(text = chat.message, color = Color.White, fontSize = 11.sp)
                }
            }
        }

        // Chat Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CricketNavySurface)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = chatInput,
                onValueChange = { chatInput = it },
                placeholder = { Text("Comment on the live match...", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    onSendMessage(chatInput)
                    chatInput = ""
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(CricketGreenPrimary)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}
