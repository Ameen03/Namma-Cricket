package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StandingRow
import com.example.data.model.Tournament
import com.example.ui.theme.*
import com.example.viewmodel.CricketUiState

@Composable
fun TournamentsScreen(
    uiState: CricketUiState,
    onSelectTournament: (String) -> Unit,
    onOpenPaymentModal: () -> Unit,
    onOpenCreateTournamentModal: () -> Unit,
    onExportRulebook: () -> Unit,
    onAddSponsor: (tournamentId: String, name: String, category: String, amount: Double) -> Unit,
    onOpenLeaderboard: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val selectedTournament = uiState.tournaments.find { it.id == uiState.selectedTournamentId }
        ?: uiState.tournaments.first()

    var showAddSponsorDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("tournaments_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header & Create Tournament Action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tournament Hub",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Grassroots & League Management",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Button(
                    onClick = onOpenCreateTournamentModal,
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("create_tournament_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New League", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Tournaments Selector Strip
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CricketNavySurface)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                uiState.tournaments.forEach { t ->
                    val isSelected = t.id == selectedTournament.id
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CricketGreenPrimary else Color.Transparent)
                            .clickable { onSelectTournament(t.id) }
                            .padding(vertical = 10.dp, horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = t.name,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Tournament Overview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = selectedTournament.name,
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${selectedTournament.edition} • ${selectedTournament.location}",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CricketGreenDark)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = selectedTournament.status.name, color = CricketGreenLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CricketNavyElevated)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TournamentStatItem(label = "Teams", value = "${selectedTournament.teamsCount}")
                        TournamentStatItem(label = "Matches", value = "${selectedTournament.totalMatches}")
                        TournamentStatItem(label = "Entry Fee", value = "$${selectedTournament.registrationFee}")
                        TournamentStatItem(label = "Prize Pool", value = selectedTournament.prizePool)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onOpenPaymentModal,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("register_team_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pay Entry & Register", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onExportRulebook,
                            modifier = Modifier
                                .weight(0.9f)
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Rulebook", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Standings / Points Table
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Official Points Table",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Top 4 teams qualify for the Championship playoffs",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = onOpenLeaderboard,
                            colors = ButtonDefaults.buttonColors(containerColor = CricketNavyElevated),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp).testTag("open_leaderboard_screen_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Leaderboard, contentDescription = null, tint = CricketGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Full Table", color = CricketGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Table Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("#", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.4f))
                        Text("TEAM", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                        Text("P", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                        Text("W", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                        Text("L", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                        Text("NRR", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                        Text("PTS", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
                    }

                    Divider(color = BorderDark, modifier = Modifier.padding(vertical = 6.dp))

                    selectedTournament.standings.forEach { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (row.rank <= 4) CricketNavyElevated.copy(alpha = 0.5f) else Color.Transparent)
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${row.rank}", color = if (row.rank <= 4) CricketGold else TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.4f))
                            Text(text = row.teamName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(2f), maxLines = 1)
                            Text(text = "${row.matchesPlayed}", color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                            Text(text = "${row.won}", color = CricketGreenLight, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                            Text(text = "${row.lost}", color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                            Text(text = String.format("%+.2f", row.netRunRate), color = TextMuted, fontSize = 11.sp, modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                            Text(text = "${row.points}", color = CricketGold, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(0.6f), textAlign = TextAlign.End)
                        }
                    }
                }
            }
        }

        // Sponsors & Brand Partners
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Sponsors & Brand Partners",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "Supporting grassroots cricket", color = TextMuted, fontSize = 11.sp)
                        }

                        TextButton(onClick = { showAddSponsorDialog = true }) {
                            Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, tint = CricketGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Sponsor", color = CricketGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    selectedTournament.sponsors.forEach { sponsor ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CricketNavyElevated)
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = sponsor.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = sponsor.category, color = CricketGold, fontSize = 11.sp)
                            }
                            Text(
                                text = "$${String.format("%.0f", sponsor.contributionAmount)}",
                                color = CricketGreenLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }

    if (showAddSponsorDialog) {
        var sName by remember { mutableStateOf("RedBull Sports") }
        var sCat by remember { mutableStateOf("Beverage Partner") }
        var sAmt by remember { mutableStateOf("4500.0") }

        AlertDialog(
            onDismissRequest = { showAddSponsorDialog = false },
            title = { Text("Add Tournament Sponsor", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = sName, onValueChange = { sName = it }, label = { Text("Sponsor Name") })
                    OutlinedTextField(value = sCat, onValueChange = { sCat = it }, label = { Text("Category / Tier") })
                    OutlinedTextField(value = sAmt, onValueChange = { sAmt = it }, label = { Text("Contribution ($)") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = sAmt.toDoubleOrNull() ?: 3000.0
                        onAddSponsor(selectedTournament.id, sName, sCat, amt)
                        showAddSponsorDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary)
                ) {
                    Text("Add Sponsor")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSponsorDialog = false }) { Text("Cancel", color = Color.White) }
            },
            containerColor = CricketNavySurface
        )
    }
}

@Composable
fun TournamentStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = label, color = TextMuted, fontSize = 10.sp)
    }
}
