package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.HighlightClip
import com.example.data.model.Tournament
import com.example.ui.components.LiveMatchHeroCard
import com.example.ui.theme.*
import com.example.util.LocalizationHelper
import com.example.viewmodel.CricketUiState
import com.example.viewmodel.NavTab

@Composable
fun HomeScreen(
    uiState: CricketUiState,
    onNavigate: (NavTab) -> Unit,
    onOpenMatchDetail: (tabIndex: Int) -> Unit,
    onShareMatch: () -> Unit,
    onVoteFanPoll: (Boolean) -> Unit,
    onOpenPayment: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = uiState.userProfile.selectedLanguage

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header & Sync indicator
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.namma_cricket_ball_icon_1792663190671),
                            contentDescription = "Namma Cricket Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, CricketGold, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = LocalizationHelper.getString(lang, "app_title"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "Grassroots & League Cricket Platform",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                // Cloud Sync Status Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (uiState.userProfile.offlineModeEnabled) Color(0xFF475569) else CricketNavyElevated)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (uiState.userProfile.offlineModeEnabled) Icons.Default.CloudOff else Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = if (uiState.userProfile.offlineModeEnabled) CricketGold else CricketGreenLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (uiState.userProfile.offlineModeEnabled) "Offline" else "Synced",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Live Match Hero Card
        item {
            LiveMatchHeroCard(
                match = uiState.liveMatch,
                onCardClick = { onOpenMatchDetail(0) },
                onShareClick = onShareMatch,
                onStreamClick = { onOpenMatchDetail(4) }
            )
        }

        // Quick Navigation Grid / Chips
        item {
            Text(
                text = "Match Center & Features",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    title = LocalizationHelper.getString(lang, "scoring"),
                    subtitle = "Score live ball-by-ball",
                    icon = Icons.Default.EditNote,
                    color = CricketGreenPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NavTab.SCORER) }
                )
                QuickActionCard(
                    title = LocalizationHelper.getString(lang, "tournaments"),
                    subtitle = "Fixtures & Points",
                    icon = Icons.Default.EmojiEvents,
                    color = CricketGold,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NavTab.TOURNAMENTS) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    title = LocalizationHelper.getString(lang, "live_stream"),
                    subtitle = "Crowd reactions & HUD",
                    icon = Icons.Default.LiveTv,
                    color = CricketRedWicket,
                    modifier = Modifier.weight(1f),
                    onClick = { onOpenMatchDetail(4) }
                )
                QuickActionCard(
                    title = LocalizationHelper.getString(lang, "players"),
                    subtitle = "Stats & MVP rankings",
                    icon = Icons.Default.Person,
                    color = CricketBlueBoundary,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NavTab.PLAYERS) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            QuickActionCard(
                title = "Tournament Leaderboard",
                subtitle = "Rankings, Points, NRR & Playoff Cutoff",
                icon = Icons.Default.Leaderboard,
                color = CricketGold,
                modifier = Modifier.fillMaxWidth().testTag("open_leaderboard_home_action"),
                onClick = { onNavigate(NavTab.LEADERBOARD) }
            )
        }

        // Fan Poll of the Match Card
        item {
            val totalVotes = uiState.liveMatch.fanPollTeamAVotes + uiState.liveMatch.fanPollTeamBVotes
            val teamAPercent = if (totalVotes == 0) 50 else (uiState.liveMatch.fanPollTeamAVotes * 100) / totalVotes
            val teamBPercent = 100 - teamAPercent

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fan_poll_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Poll, contentDescription = null, tint = CricketGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Fan Poll: Who will win tonight?",
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(text = "$totalVotes votes", color = TextMuted, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onVoteFanPoll(true) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("fan_poll_vote_teama"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CricketNavyElevated)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(uiState.liveMatch.teamA.shortName, color = Color.White, fontWeight = FontWeight.Bold)
                                Text("$teamAPercent%", color = CricketGold, fontWeight = FontWeight.ExtraBold)
                            }
                        }

                        Button(
                            onClick = { onVoteFanPoll(false) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("fan_poll_vote_teamb"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(uiState.liveMatch.teamB.shortName, color = Color.White, fontWeight = FontWeight.Bold)
                                Text("$teamBPercent%", color = CricketGreenLight, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }

        // Video Highlights Carousel
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LocalizationHelper.getString(lang, "highlights"),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "See All",
                    color = CricketGreenLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onOpenMatchDetail(4) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(uiState.highlights) { clip ->
                    HighlightThumbnailCard(
                        clip = clip,
                        onClick = { onOpenMatchDetail(4) }
                    )
                }
            }
        }

        // Active Tournaments Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Featured Tournaments",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Register Team",
                    color = CricketGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onOpenPayment() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            uiState.tournaments.forEach { tournament ->
                TournamentMiniCard(
                    tournament = tournament,
                    onClick = { onNavigate(NavTab.TOURNAMENTS) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = subtitle, color = TextMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun HighlightThumbnailCard(
    clip: HighlightClip,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(CricketNavyElevated),
                contentAlignment = Alignment.Center
            ) {
                // Stadium background or icon
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = CricketGold,
                    modifier = Modifier.size(42.dp)
                )

                // Category pill
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CricketRedWicket)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = clip.category, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }

                // Duration pill
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "${clip.durationSec}s", color = Color.White, fontSize = 9.sp)
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = clip.title,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${clip.viewsCount / 1000}k views", color = TextMuted, fontSize = 10.sp)
                    Text(text = "❤️ ${clip.likesCount}", color = CricketGreenLight, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun TournamentMiniCard(
    tournament: Tournament,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
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
                        .clip(RoundedCornerShape(8.dp))
                        .background(CricketGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = CricketGold, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = tournament.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "${tournament.teamsCount} Teams • Prize ${tournament.prizePool}", color = TextMuted, fontSize = 11.sp)
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CricketGreenDark)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = tournament.status.name, color = CricketGreenLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
