package com.example.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Player
import com.example.data.model.PlayerRole
import com.example.ui.theme.*
import com.example.viewmodel.CricketUiState

@Composable
fun PlayersScreen(
    uiState: CricketUiState,
    onSelectPlayer: (String) -> Unit,
    onSharePlayer: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf<PlayerRole?>(null) }

    val selectedPlayer = uiState.players.find { it.id == uiState.selectedPlayerId }
        ?: uiState.players.first()

    val filteredPlayers = remember(uiState.players, searchQuery, selectedRole) {
        uiState.players.filter { p ->
            (searchQuery.isBlank() || p.name.contains(searchQuery, ignoreCase = true)) &&
            (selectedRole == null || p.role == selectedRole)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("players_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Player Statistics & Profiles",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Grassroots & League Career Tracking",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CricketGold)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = "MVP Leaderboard", color = CricketNavy, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search player name...", color = TextMuted) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_player_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CricketNavySurface,
                    unfocusedContainerColor = CricketNavySurface
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Role Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = (selectedRole == null),
                        onClick = { selectedRole = null },
                        label = { Text("All Roles") }
                    )
                }
                items(PlayerRole.values()) { role ->
                    FilterChip(
                        selected = (selectedRole == role),
                        onClick = { selectedRole = if (selectedRole == role) null else role },
                        label = { Text(role.name.replace("_", " ")) }
                    )
                }
            }
        }

        // Selected Player Hero Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("selected_player_profile_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(CricketNavyElevated, CricketNavySurface)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(CricketGreenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = selectedPlayer.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = selectedPlayer.name,
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(imageVector = Icons.Default.Verified, contentDescription = "Verified", tint = CricketGreenLight, modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "${selectedPlayer.role.name} • ${selectedPlayer.battingStyle}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = { onSharePlayer(selectedPlayer) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CricketNavySurface)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Key Stats Grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CricketNavySurface)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CareerStatItem(label = "Matches", value = "${selectedPlayer.matches}")
                        CareerStatItem(label = "Runs", value = "${selectedPlayer.runs}")
                        CareerStatItem(label = "Average", value = String.format("%.1f", selectedPlayer.battingAverage))
                        CareerStatItem(label = "SR", value = String.format("%.1f", selectedPlayer.strikeRate))
                        CareerStatItem(label = "Wickets", value = "${selectedPlayer.wickets}")
                        CareerStatItem(label = "MVP Pts", value = "${selectedPlayer.mvpPoints}")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Form Guide / Last 5 innings
                    Text(text = "Recent Form (Last 5 innings)", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedPlayer.recentScores.forEach { score ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (score >= 50) CricketGreenDark else CricketNavyElevated)
                                    .border(1.dp, if (score >= 50) CricketGreenLight else Color.Transparent, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "$score",
                                    color = if (score >= 50) CricketGreenLight else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onSharePlayer(selectedPlayer) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("share_player_profile_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Official Player Card", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // All Players List
        item {
            Text(
                text = "Player Directory (${filteredPlayers.size})",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        items(filteredPlayers) { player ->
            val isSelected = player.id == selectedPlayer.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelectPlayer(player.id) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) CricketGreenDark.copy(alpha = 0.4f) else CricketNavySurface
                ),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, CricketGreenLight) else null
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(CricketNavyElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = player.name.firstOrNull()?.toString() ?: "P",
                                color = CricketGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = player.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "${player.role.name} • ${player.country}", color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "${player.runs} Runs • ${player.wickets} Wkts", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${player.mvpPoints} MVP Pts", color = CricketGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun CareerStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = label, color = TextMuted, fontSize = 9.sp)
    }
}
