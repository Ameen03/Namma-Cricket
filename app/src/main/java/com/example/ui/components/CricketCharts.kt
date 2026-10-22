package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BallEvent
import com.example.data.model.ViewerRetentionPoint
import com.example.data.model.WormRunPoint
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WagonWheelCanvas(
    balls: List<BallEvent>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CricketNavySurface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "360° Ground Wagon Wheel",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Shot spray distribution across the field",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .size(230.dp)
                .testTag("wagon_wheel_canvas"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val outerRadius = size.width / 2f - 8f
                val innerCircleRadius = outerRadius * 0.55f

                // Outer boundary grass
                drawCircle(
                    color = Color(0xFF0A3D29),
                    radius = outerRadius,
                    center = center
                )

                // Boundary rope stroke
                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    radius = outerRadius,
                    center = center,
                    style = Stroke(width = 3f)
                )

                // 30-yard inner circle (dashed/subtle)
                drawCircle(
                    color = Color.White.copy(alpha = 0.25f),
                    radius = innerCircleRadius,
                    center = center,
                    style = Stroke(width = 2f)
                )

                // Central pitch rectangle
                val pitchW = 20f
                val pitchH = 44f
                drawRect(
                    color = PitchBeigeDark,
                    topLeft = Offset(center.x - pitchW / 2f, center.y - pitchH / 2f),
                    size = Size(pitchW, pitchH)
                )

                // Draw each ball vector
                balls.take(24).forEach { ball ->
                    val angleRad = Math.toRadians(ball.wagonAngleDeg.toDouble() - 90.0)
                    val strokeColor = when {
                        ball.isWicket -> CricketRedWicket
                        ball.runs >= 6 -> CricketGold
                        ball.runs == 4 -> CricketBlueBoundary
                        ball.runs > 0 -> Color.White.copy(alpha = 0.7f)
                        else -> Color.Gray.copy(alpha = 0.3f)
                    }
                    val shotDistance = when {
                        ball.runs >= 6 -> outerRadius
                        ball.runs == 4 -> outerRadius * 0.95f
                        ball.runs > 0 -> outerRadius * 0.65f
                        else -> outerRadius * 0.25f
                    }

                    val endX = center.x + (shotDistance * cos(angleRad)).toFloat()
                    val endY = center.y + (shotDistance * sin(angleRad)).toFloat()

                    drawLine(
                        color = strokeColor,
                        start = center,
                        end = Offset(endX, endY),
                        strokeWidth = if (ball.runs >= 4) 3.5f else 2f
                    )
                    drawCircle(
                        color = strokeColor,
                        radius = if (ball.runs >= 4) 4.5f else 3f,
                        center = Offset(endX, endY)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Legend row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(label = "6s (Maximum)", color = CricketGold)
            LegendItem(label = "4s (Fours)", color = CricketBlueBoundary)
            LegendItem(label = "1s & 2s", color = Color.White)
            LegendItem(label = "Wickets", color = CricketRedWicket)
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun WormRunComparisonChart(
    points: List<WormRunPoint>,
    teamAName: String,
    teamBName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CricketNavySurface)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Worm Chart: Run Progression",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Over-by-over comparative scoreline",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LegendItem(label = teamAName, color = Color(0xFFD71920))
                LegendItem(label = teamBName, color = CricketGreenLight)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .testTag("worm_chart_canvas")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val paddingLeft = 40f
                val paddingBottom = 40f
                val graphWidth = size.width - paddingLeft - 20f
                val graphHeight = size.height - paddingBottom - 20f

                // Max runs on Y-axis
                val maxRuns = 200f
                val totalOvers = 20f

                // Grid lines (horizontal)
                for (step in 0..4) {
                    val runVal = step * 50
                    val y = size.height - paddingBottom - (runVal / maxRuns * graphHeight)
                    drawLine(
                        color = Color.White.copy(alpha = 0.08f),
                        start = Offset(paddingLeft, y),
                        end = Offset(size.width - 20f, y),
                        strokeWidth = 1f
                    )
                }

                if (points.isNotEmpty()) {
                    val pathA = Path()
                    val pathB = Path()

                    points.forEachIndexed { index, pt ->
                        val x = paddingLeft + (pt.over.toFloat() / totalOvers * graphWidth)
                        val yA = size.height - paddingBottom - (pt.teamARuns.toFloat() / maxRuns * graphHeight)
                        val yB = size.height - paddingBottom - (pt.teamBRuns.toFloat() / maxRuns * graphHeight)

                        if (index == 0) {
                            pathA.moveTo(paddingLeft, size.height - paddingBottom)
                            pathB.moveTo(paddingLeft, size.height - paddingBottom)
                        }
                        pathA.lineTo(x, yA)
                        pathB.lineTo(x, yB)

                        // Draw dots
                        drawCircle(color = Color(0xFFD71920), radius = 3.5f, center = Offset(x, yA))
                        drawCircle(color = CricketGreenLight, radius = 3.5f, center = Offset(x, yB))
                    }

                    // Stroke paths
                    drawPath(path = pathA, color = Color(0xFFD71920), style = Stroke(width = 3f))
                    drawPath(path = pathB, color = CricketGreenLight, style = Stroke(width = 3.5f))
                }
            }
        }
    }
}

@Composable
fun ViewerRetentionChart(
    retentionData: List<ViewerRetentionPoint>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CricketNavySurface)
            .padding(16.dp)
    ) {
        Text(
            text = "Live Stream Viewer Retention",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Peak concurrent audience during thrilling death overs",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            val maxCount = retentionData.maxOfOrNull { it.viewersCount } ?: 50000

            retentionData.forEach { pt ->
                val ratio = (pt.viewersCount.toFloat() / maxCount.toFloat()).coerceIn(0.15f, 1f)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${pt.viewersCount / 1000}k",
                        color = CricketGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height((90f * ratio).dp)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(CricketGold, CricketAmber)
                                )
                            )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = pt.overLabel,
                        color = TextMuted,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
