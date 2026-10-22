package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.StandingRow
import com.example.data.model.Tournament
import com.example.ui.theme.*
import com.example.viewmodel.CricketUiState

enum class LeaderboardSortBy {
    POINTS, NRR, WINS, RANK
}

@Composable
fun TournamentLeaderboardScreen(
    uiState: CricketUiState,
    onSelectTournament: (String) -> Unit,
    onShareLeaderboard: (Tournament) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTournament = uiState.tournaments.find { it.id == uiState.selectedTournamentId }
        ?: uiState.tournaments.first()

    var sortBy by remember { mutableStateOf(LeaderboardSortBy.POINTS) }
    var expandedTeamId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Sorted and filtered standings list
    val sortedStandings = remember(selectedTournament.standings, sortBy, searchQuery) {
        val filtered = if (searchQuery.isBlank()) {
            selectedTournament.standings
        } else {
            selectedTournament.standings.filter {
                it.teamName.contains(searchQuery, ignoreCase = true) ||
                it.shortName.contains(searchQuery, ignoreCase = true)
            }
        }

        when (sortBy) {
            LeaderboardSortBy.POINTS -> filtered.sortedWith(
                compareByDescending<StandingRow> { it.points }
                    .thenByDescending { it.netRunRate }
            )
            LeaderboardSortBy.NRR -> filtered.sortedByDescending { it.netRunRate }
            LeaderboardSortBy.WINS -> filtered.sortedByDescending { it.won }
            LeaderboardSortBy.RANK -> filtered.sortedBy { it.rank }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CricketNavy)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("tournament_leaderboard_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Sticky Header / Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CricketNavySurface)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Tournament Standings",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Official League Table & Net Run Rates",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                IconButton(
                    onClick = { onShareLeaderboard(selectedTournament) },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CricketGreenPrimary)
                        .testTag("share_leaderboard_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Leaderboard",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Tournament Selector Strip
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(uiState.tournaments) { tourney ->
                    val isSelected = tourney.id == selectedTournament.id
                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectTournament(tourney.id) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) CricketGreenPrimary else CricketNavySurface
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, CricketGreenLight) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else CricketGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = tourney.name,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${tourney.edition} • ${tourney.location}",
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Search & Sorting Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Filter team name or shortcode...", fontSize = 11.sp, color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("leaderboard_search_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = CricketNavyElevated,
                            unfocusedContainerColor = CricketNavyElevated
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sort pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Sort By:", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            SortPill(
                                label = "PTS",
                                isSelected = (sortBy == LeaderboardSortBy.POINTS),
                                onClick = { sortBy = LeaderboardSortBy.POINTS }
                            )
                            SortPill(
                                label = "NRR",
                                isSelected = (sortBy == LeaderboardSortBy.NRR),
                                onClick = { sortBy = LeaderboardSortBy.NRR }
                            )
                            SortPill(
                                label = "Wins",
                                isSelected = (sortBy == LeaderboardSortBy.WINS),
                                onClick = { sortBy = LeaderboardSortBy.WINS }
                            )
                            SortPill(
                                label = "Rank #",
                                isSelected = (sortBy == LeaderboardSortBy.RANK),
                                onClick = { sortBy = LeaderboardSortBy.RANK }
                            )
                        }
                    }
                }
            }
        }

        // Table Header Row (Mobile Optimized Layout)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                colors = CardDefaults.cardColors(containerColor = CricketNavyElevated)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "# TEAM",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.weight(2.3f)
                    )
                    Text(
                        text = "P",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.6f)
                    )
                    Text(
                        text = "W",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.6f)
                    )
                    Text(
                        text = "L",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.6f)
                    )
                    Text(
                        text = "NRR",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1.1f)
                    )
                    Text(
                        text = "PTS",
                        color = CricketGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(0.9f)
                    )
                }
            }
        }

        // Standings Rows
        items(sortedStandings) { row ->
            val isExpanded = (expandedTeamId == row.teamId)
            val isTop2 = row.rank <= 2
            val isTop4 = row.rank in 3..4

            Column(modifier = Modifier.fillMaxWidth()) {
                // Main Table Row Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            expandedTeamId = if (isExpanded) null else row.teamId
                        }
                        .testTag("standing_row_${row.shortName}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isExpanded) CricketNavyElevated else CricketNavySurface
                    ),
                    border = if (isExpanded) androidx.compose.foundation.BorderStroke(1.dp, CricketGreenLight.copy(alpha = 0.6f)) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rank Indicator Bar + Number
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(2.3f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(24.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        when {
                                            isTop2 -> CricketGreenLight
                                            isTop4 -> CricketBlueBoundary
                                            else -> Color(0xFF475569)
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "${row.rank}",
                                color = if (row.rank <= 4) CricketGold else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.width(18.dp)
                            )

                            // Team avatar
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(Color(row.primaryColorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = row.shortName.take(2),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Column {
                                Text(
                                    text = row.teamName,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = row.shortName,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Matches Played
                        Text(
                            text = "${row.matchesPlayed}",
                            color = Color.White,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(0.6f)
                        )

                        // Won
                        Text(
                            text = "${row.won}",
                            color = CricketGreenLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(0.6f)
                        )

                        // Lost
                        Text(
                            text = "${row.lost}",
                            color = TextMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(0.6f)
                        )

                        // NRR (Color coded: green if positive, red if negative)
                        Text(
                            text = String.format("%+.3f", row.netRunRate),
                            color = if (row.netRunRate >= 0) CricketGreenLight else CricketRedWicket,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1.1f)
                        )

                        // Points (PTS) Pill
                        Box(
                            modifier = Modifier
                                .weight(0.9f)
                                .padding(start = 4.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CricketGold)
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${row.points}",
                                    color = CricketNavy,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                // Expandable Details (Form, Over Breakdown, Qualification analysis)
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                    exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF132238))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Qualification Status Pill
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (row.rank <= 4) Icons.Default.CheckCircle else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (row.rank <= 2) CricketGreenLight else if (row.rank <= 4) CricketBlueBoundary else TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = when {
                                            row.rank <= 2 -> "Qualifier 1 Zone (Double Chance)"
                                            row.rank <= 4 -> "Eliminator Zone (Playoff Spot)"
                                            else -> "Knockout Danger Zone"
                                        },
                                        color = when {
                                            row.rank <= 2 -> CricketGreenLight
                                            row.rank <= 4 -> CricketBlueBoundary
                                            else -> TextMuted
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = "Tie: ${row.tied} • NR: ${row.noResult}",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }

                            Divider(color = BorderDark, modifier = Modifier.padding(vertical = 8.dp))

                            // Recent 5 Form Badges
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Recent Form (Last 5):",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    row.recentForm.forEach { result ->
                                        val (bg, label) = when (result) {
                                            "W" -> CricketGreenPrimary to "W"
                                            "L" -> CricketRedWicket to "L"
                                            "T" -> CricketAmber to "T"
                                            else -> Color(0xFF475569) to "NR"
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(bg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Runs Scored & Conceded breakdown
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CricketNavySurface)
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "Runs Scored", color = TextMuted, fontSize = 9.sp)
                                    Text(
                                        text = "${row.runsFor} / ${row.oversFor} ov",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "Runs Conceded", color = TextMuted, fontSize = 9.sp)
                                    Text(
                                        text = "${row.runsAgainst} / ${row.oversAgainst} ov",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Qualification cut-off line after rank 4
                if (row.rank == 4) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0F3B2A).copy(alpha = 0.6f))
                            .border(1.dp, CricketGreenLight.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚡ TOP 4 TEAMS QUALIFY FOR PLAYOFFS ⚡",
                            color = CricketGreenLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }

        // Leaderboard Legend
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Cricket Standings Legend",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LegendDefinition(term = "P", desc = "Played")
                        LegendDefinition(term = "W", desc = "Won")
                        LegendDefinition(term = "L", desc = "Lost")
                        LegendDefinition(term = "NRR", desc = "Net Run Rate")
                        LegendDefinition(term = "PTS", desc = "2 Pts / Win")
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Positions 1 & 2 enter Qualifier 1 with two chances to reach the Grand Final.\n• Positions 3 & 4 enter the sudden-death Eliminator.",
                        color = TextMuted,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SortPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) CricketGreenPrimary else CricketNavyElevated)
            .clickable { onClick() }
            .padding(horizontal = 9.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
        )
    }
}

@Composable
fun LegendDefinition(term: String, desc: String) {
    Column {
        Text(text = term, color = CricketGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = desc, color = TextMuted, fontSize = 9.sp)
    }
}
