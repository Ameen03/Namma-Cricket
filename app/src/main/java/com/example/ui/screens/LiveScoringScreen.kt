package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.components.BallDot
import com.example.ui.theme.*
import com.example.util.LocalizationHelper
import com.example.viewmodel.CricketUiState

@Composable
fun LiveScoringScreen(
    uiState: CricketUiState,
    onRecordBall: (runs: Int, isWicket: Boolean, wicketType: String, isWide: Boolean, isNoBall: Boolean, isBye: Boolean, isLegBye: Boolean) -> Unit,
    onSwapStrike: () -> Unit,
    onUndoBall: () -> Unit,
    onToggleSimulation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val match = uiState.liveMatch
    val innings = match.currentInnings
    val lang = uiState.userProfile.selectedLanguage

    var showWicketDialog by remember { mutableStateOf(false) }

    val striker = innings.batsmanStats.find { it.playerId == match.currentStrikerId }
        ?: innings.batsmanStats.firstOrNull()
        ?: BattingStat("STRIKER", "Striker", 0, 0, 0, 0)
    val nonStriker = innings.batsmanStats.find { it.playerId == match.currentNonStrikerId }
        ?: innings.batsmanStats.getOrNull(1)
        ?: BattingStat("NON_STRIKER", "Non-Striker", 0, 0, 0, 0)
    val bowler = innings.bowlerStats.find { it.playerId == match.currentBowlerId }
        ?: innings.bowlerStats.firstOrNull()
        ?: BowlingStat("BOWLER", "Bowler", 0, 0, 0, 0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("live_scoring_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Match Header Card
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
                            text = "${match.currentBattingTeam.name} Innings",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = match.resultSummary,
                            color = CricketGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Score Big Pill
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${innings.totalRuns} / ${innings.wickets}",
                            color = CricketGreenLight,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "(${innings.oversFormatted} / ${match.oversLimit} ov)",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Over rate and equations strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CricketNavyElevated)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "CRR: ${String.format("%.2f", innings.currentRunRate)}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    val target = match.inningsList.first().totalRuns + 1
                    val ballsLeft = (match.oversLimit * 6) - innings.legalBalls
                    val runsNeeded = target - innings.totalRuns
                    val rrr = if (ballsLeft > 0) (runsNeeded.toDouble() * 6.0) / ballsLeft.toDouble() else 0.0
                    Text(text = "RRR: ${String.format("%.2f", rrr.coerceAtLeast(0.0))}", color = CricketAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Extras: ${innings.extras.total} (w ${innings.extras.wides}, nb ${innings.extras.noBalls})", color = TextMuted, fontSize = 11.sp)
                }
            }
        }

        // Active Batsmen & Bowler Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Striker Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CricketNavyElevated)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.SportsCricket, contentDescription = null, tint = CricketGreenLight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "${striker.name} *", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Text(
                        text = "${striker.runs} (${striker.balls}) • 4s: ${striker.fours} 6s: ${striker.sixes} [SR: ${String.format("%.1f", striker.strikeRate)}]",
                        color = Color.White,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Non-striker Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = nonStriker.name, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(
                        text = "${nonStriker.runs} (${nonStriker.balls}) • 4s: ${nonStriker.fours} 6s: ${nonStriker.sixes}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Divider(color = BorderDark, modifier = Modifier.padding(vertical = 8.dp))

                // Bowler Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.SportsBaseball, contentDescription = null, tint = CricketGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = bowler.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Text(
                        text = "${bowler.oversFormatted} ov • ${bowler.maidens}m • ${bowler.runsConceded}r • ${bowler.wickets}w [Econ: ${String.format("%.1f", bowler.economy)}]",
                        color = CricketGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Recent Over Balls Timeline
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "This Over Timeline", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val thisOverBalls = innings.ballHistory.take(6).reversed()
                    if (thisOverBalls.isEmpty()) {
                        Text(text = "Over beginning...", color = TextMuted, fontSize = 12.sp)
                    } else {
                        thisOverBalls.forEach { ball ->
                            BallDot(ball = ball)
                        }
                    }
                }
            }
        }

        // Scoring Keypad: Runs
        Text(
            text = "Record Runs (Tap to score)",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScoreButton(text = "0", label = "Dot", color = Color(0xFF334155), modifier = Modifier.weight(1f)) {
                onRecordBall(0, false, "", false, false, false, false)
            }
            ScoreButton(text = "1", label = "Single", color = CricketGreenDark, modifier = Modifier.weight(1f)) {
                onRecordBall(1, false, "", false, false, false, false)
            }
            ScoreButton(text = "2", label = "Two", color = CricketGreenDark, modifier = Modifier.weight(1f)) {
                onRecordBall(2, false, "", false, false, false, false)
            }
            ScoreButton(text = "3", label = "Three", color = CricketGreenDark, modifier = Modifier.weight(1f)) {
                onRecordBall(3, false, "", false, false, false, false)
            }
            ScoreButton(text = "4", label = "FOUR", color = CricketBlueBoundary, modifier = Modifier.weight(1.1f)) {
                onRecordBall(4, false, "", false, false, false, false)
            }
            ScoreButton(text = "6", label = "SIX", color = CricketGold, textColor = CricketNavy, modifier = Modifier.weight(1.1f)) {
                onRecordBall(6, false, "", false, false, false, false)
            }
        }

        // Scoring Keypad: Extras & Wickets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScoreButton(text = "WD", label = "Wide", color = CricketNavyElevated, modifier = Modifier.weight(1f)) {
                onRecordBall(0, false, "", true, false, false, false)
            }
            ScoreButton(text = "NB", label = "No Ball", color = CricketNavyElevated, modifier = Modifier.weight(1f)) {
                onRecordBall(0, false, "", false, true, false, false)
            }
            ScoreButton(text = "BYE", label = "Bye", color = CricketNavyElevated, modifier = Modifier.weight(1f)) {
                onRecordBall(1, false, "", false, false, true, false)
            }
            ScoreButton(text = "LB", label = "Leg Bye", color = CricketNavyElevated, modifier = Modifier.weight(1f)) {
                onRecordBall(1, false, "", false, false, false, true)
            }
            ScoreButton(
                text = "OUT",
                label = "Wicket",
                color = CricketRedWicket,
                modifier = Modifier
                    .weight(1.3f)
                    .testTag("wicket_button")
            ) {
                showWicketDialog = true
            }
        }

        // Quick Controls: Swap Strike, Undo, Auto Simulator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onSwapStrike,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("swap_strike_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CricketNavyElevated)
            ) {
                Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Swap Strike", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onUndoBall,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("undo_ball_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CricketNavyElevated)
            ) {
                Icon(imageVector = Icons.Default.Undo, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Undo Ball", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onToggleSimulation,
                modifier = Modifier
                    .weight(1.2f)
                    .height(44.dp)
                    .testTag("toggle_simulation_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isSimulationActive) CricketAmber else CricketGreenPrimary
                )
            ) {
                Icon(
                    imageVector = if (uiState.isSimulationActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (uiState.isSimulationActive) "Pause Sim" else "Live Auto-Sim",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Dismissal Selection Dialog
    if (showWicketDialog) {
        Dialog(onDismissRequest = { showWicketDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CricketNavySurface),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Select Dismissal Type",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    listOf(
                        "Bowled" to "Clean bowled through gate",
                        "Caught" to "Caught out by fielder",
                        "LBW" to "Leg Before Wicket",
                        "Run Out" to "Direct hit / run out",
                        "Stumped" to "Stumped by wicket-keeper",
                        "Hit Wicket" to "Broke own stumps"
                    ).forEach { (type, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onRecordBall(0, true, type, false, false, false, false)
                                    showWicketDialog = false
                                }
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = type, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = desc, color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreButton(
    text: String,
    label: String,
    color: Color,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("score_btn_$text"),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = label,
                color = textColor.copy(alpha = 0.8f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
