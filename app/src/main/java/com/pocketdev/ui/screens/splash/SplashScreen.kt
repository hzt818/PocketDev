package com.pocketdev.ui.screens.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToMain: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")

    val animatedHue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hue"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showContent = true
        delay(2500)
        onNavigateToMain()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.minDimension / 1.5f

            rotate(animatedHue) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.hsv(animatedHue, 0.7f, 0.5f, 0.3f),
                            Color.hsv((animatedHue + 60f) % 360f, 0.8f, 0.4f, 0.2f),
                            Color.Black
                        ),
                        center = Offset(centerX, centerY),
                        radius = radius
                    ),
                    radius = radius,
                    center = Offset(centerX, centerY)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Canvas(
                modifier = Modifier.size(120.dp)
            ) {
                val hue = animatedHue
                val chevronColor = Color.hsv(hue, 0.6f, 0.9f, pulseAlpha)

                val path = Path().apply {
                    moveTo(size.width * 0.3f, size.height * 0.15f)
                    lineTo(size.width * 0.75f, size.height * 0.5f)
                    lineTo(size.width * 0.3f, size.height * 0.85f)
                    lineTo(size.width * 0.45f, size.height * 0.85f)
                    lineTo(size.width * 0.9f, size.height * 0.5f)
                    lineTo(size.width * 0.45f, size.height * 0.15f)
                    close()
                }
                drawPath(path, chevronColor)

                drawRect(
                    color = chevronColor.copy(alpha = pulseAlpha * 0.5f),
                    topLeft = Offset(size.width * 0.78f, size.height * 0.2f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.08f, size.height * 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (showContent) {
                Text(
                    text = "PocketDev",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 28.sp
                    ),
                    color = Color.hsv(animatedHue, 0.5f, 0.9f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "AI-Powered Development",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = Color.hsv((animatedHue + 180f) % 360f, 0.4f, 0.7f)
                )
            }
        }
    }
}
