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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.NotificationItem
import com.example.ui.theme.*
import com.example.util.LocalizationHelper
import com.example.viewmodel.CricketUiState

@Composable
fun ProfileSettingsScreen(
    uiState: CricketUiState,
    onSetLanguage: (AppLanguage) -> Unit,
    onToggleTheme: (Boolean) -> Unit,
    onToggleHighContrast: (Boolean) -> Unit,
    onToggleOfflineMode: (Boolean) -> Unit,
    onOpenTwoFactorModal: () -> Unit,
    onExportScorecard: () -> Unit,
    onUploadHighlight: (title: String, category: String) -> Unit,
    onReplaySplash: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val profile = uiState.userProfile
    val lang = profile.selectedLanguage
    var showUploadClipDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("profile_settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(CricketGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile.name,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(imageVector = Icons.Default.Verified, contentDescription = "Verified", tint = CricketGreenLight, modifier = Modifier.size(16.dp))
                            }
                            Text(text = profile.role, color = TextMuted, fontSize = 11.sp)
                            Text(text = profile.email, color = CricketGold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stats row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CricketNavyElevated)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ProfileMetric(value = "${profile.matchesScored}", label = "Matches Scored")
                        ProfileMetric(value = "${profile.tournamentsManaged}", label = "Tournaments")
                        ProfileMetric(value = "${profile.totalRunsLogged}", label = "Runs Logged")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Badges row
                    Text(text = "Earned Badges", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        profile.badges.forEach { badge ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CricketGreenDark)
                                    .border(1.dp, CricketGreenLight.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = "🏅 $badge", color = CricketGreenLight, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // Multi-Factor Verification & Security
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = CricketGreenLight, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Multi-Factor Authentication (2FA)",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (profile.twoFactorEnabled) "2FA Active & Verified" else "Requires PIN verification",
                                    color = if (profile.twoFactorEnabled) CricketGreenLight else CricketAmber,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = onOpenTwoFactorModal,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (profile.twoFactorEnabled) CricketNavyElevated else CricketGreenPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("verify_2fa_button")
                        ) {
                            Text(
                                text = if (profile.twoFactorEnabled) "Change PIN" else "Verify Now",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Cloud Storage & Media Management
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
                                text = "Cloud Storage & Media Library",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${String.format("%.1f", uiState.cloudStorageUsedMb)} MB used of 1.0 GB",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = { showUploadClipDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Upload Clip", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { (uiState.cloudStorageUsedMb / uiState.cloudStorageLimitMb).toFloat() },
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

        // Multilingual Support Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Translate, contentDescription = null, tint = CricketGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Language / भाषा (Multilingual Support)",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AppLanguage.values().take(3).forEach { l ->
                            LanguageChoiceChip(
                                language = l,
                                isSelected = (profile.selectedLanguage == l),
                                onClick = { onSetLanguage(l) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AppLanguage.values().drop(3).forEach { l ->
                            LanguageChoiceChip(
                                language = l,
                                isSelected = (profile.selectedLanguage == l),
                                onClick = { onSetLanguage(l) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Appearance & Accessibility (Dark/Light/High-Contrast)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Display & Accessibility",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Dark Mode", color = Color.White, fontSize = 13.sp)
                            Text(text = "Optimal for night match scoring", color = TextMuted, fontSize = 11.sp)
                        }
                        Switch(
                            checked = profile.darkTheme,
                            onCheckedChange = { onToggleTheme(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CricketGreenLight)
                        )
                    }

                    Divider(color = BorderDark, modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "High Contrast Mode", color = Color.White, fontSize = 13.sp)
                            Text(text = "Enhanced legibility under bright sunlight", color = TextMuted, fontSize = 11.sp)
                        }
                        Switch(
                            checked = profile.highContrast,
                            onCheckedChange = { onToggleHighContrast(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CricketGold)
                        )
                    }

                    Divider(color = BorderDark, modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Offline Mode", color = Color.White, fontSize = 13.sp)
                            Text(text = "Cache all matches & score without internet", color = TextMuted, fontSize = 11.sp)
                        }
                        Switch(
                            checked = profile.offlineModeEnabled,
                            onCheckedChange = { onToggleOfflineMode(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CricketGreenLight)
                        )
                    }

                    Divider(color = BorderDark, modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "3D Spinning Ball Opening", color = Color.White, fontSize = 13.sp)
                            Text(text = "Watch the half-white, half-red ball entrance", color = TextMuted, fontSize = 11.sp)
                        }
                        Button(
                            onClick = onReplaySplash,
                            colors = ButtonDefaults.buttonColors(containerColor = CricketNavyElevated),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp), tint = CricketGold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Replay Intro", fontSize = 11.sp, color = CricketGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Export Document Management
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Scorecard & Roster Export",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Export official PDF/CSV match report",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = onExportScorecard,
                        colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("export_scorecard_btn")
                    ) {
                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Real-Time Push Alerts & Notifications List
        item {
            Text(
                text = "Recent Notifications (${uiState.notifications.size})",
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(uiState.notifications) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CricketNavySurface)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CricketNavyElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = CricketGold, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = item.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = item.timestamp, color = TextMuted, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = item.message, color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        }
    }

    if (showUploadClipDialog) {
        var clipTitle by remember { mutableStateOf("Best Sixes of the Death Overs") }
        var clipCat by remember { mutableStateOf("Sixes") }

        AlertDialog(
            onDismissRequest = { showUploadClipDialog = false },
            title = { Text("Upload Match Video Reel", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = clipTitle, onValueChange = { clipTitle = it }, label = { Text("Video Title") })
                    OutlinedTextField(value = clipCat, onValueChange = { clipCat = it }, label = { Text("Tag / Category (e.g. Sixes, Wickets)") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUploadHighlight(clipTitle, clipCat)
                        showUploadClipDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary)
                ) {
                    Text("Upload to Cloud")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadClipDialog = false }) { Text("Cancel", color = Color.White) }
            },
            containerColor = CricketNavySurface
        )
    }
}

@Composable
fun ProfileMetric(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = CricketGold, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = label, color = TextMuted, fontSize = 10.sp)
    }
}

@Composable
fun LanguageChoiceChip(
    language: AppLanguage,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) CricketGreenPrimary else CricketNavyElevated)
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = language.nativeName,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
