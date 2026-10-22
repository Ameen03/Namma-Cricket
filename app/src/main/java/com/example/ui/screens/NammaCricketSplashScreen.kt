package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun NammaCricketSplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Ball scale animation: Coming in front of the user (from far distance to close-up)
    val ballScale by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.05f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ballScale"
    )

    // Ball alpha
    val ballAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "ballAlpha"
    )

    // Continuous fast realistic cricket ball spin animation
    val infiniteTransition = rememberInfiniteTransition(label = "ballSpinTransition")
    val ballRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ballRotation"
    )

    // Seam glow pulsation
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // Progress bar value
    val progress by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
        label = "progress"
    )

    // Trigger animation immediately on composition
    LaunchedEffect(Unit) {
        startAnimation = true
        // Allow user to watch the dramatic ball zooming & spinning into view
        delay(2700)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF132A46),
                        Color(0xFF091626),
                        Color(0xFF040A12)
                    )
                )
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                // Tap to skip
                onSplashFinished()
            }
            .testTag("namma_cricket_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Subtle ambient stadium floodlight rays in background
        Box(
            modifier = Modifier
                .size(380.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CricketGold.copy(alpha = glowAlpha * 0.25f),
                            CricketGreenLight.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // THE CRICKET BALL: Coming in front of the user and spinning
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .scale(ballScale)
                    .alpha(ballAlpha),
                contentAlignment = Alignment.Center
            ) {
                // Ball depth shadow on pitch
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 18.dp)
                        .width(180.dp)
                        .height(30.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                            )
                        )
                )

                // Half-White, Half-Red Cricket Ball Image spinning
                Image(
                    painter = painterResource(id = R.drawable.namma_cricket_ball_icon_1792663190671),
                    contentDescription = "Namma Cricket Split Ball",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(210.dp)
                        .clip(CircleShape)
                        .rotate(ballRotation)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.8f),
                                    CricketGold,
                                    Color(0xFFDC2626),
                                    Color.White
                                )
                            ),
                            shape = CircleShape
                        )
                        .testTag("spinning_cricket_ball")
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Animated Title & Subtitle revealing underneath
            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn(tween(1000, delayMillis = 400)) + scaleIn(tween(800, delayMillis = 400))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "NAMMA CRICKET",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 3.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "ನಮ್ಮ ಕ್ರಿಕೆಟ್ • Grassroots Cricket Powered Live",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CricketGold,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Half-White & Half-Red Seam • CricHeroes Edition",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Loading Seam Bar
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(CricketNavyElevated)
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = CricketGreenLight,
                    trackColor = CricketNavyElevated
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Tap anywhere to skip",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted.copy(alpha = 0.6f)
            )
        }
    }
}
