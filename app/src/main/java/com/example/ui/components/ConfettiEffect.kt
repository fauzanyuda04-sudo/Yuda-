package com.example.ui.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color(0xFFFF6B6B),
        Color(0xFFFFD166),
        Color(0xFF4D96FF),
        Color(0xFF6BCB77),
        Color(0xFF9D4EDD),
        Color(0xFFFF85A1),
        Color(0xFF00CEC9)
    )

    val particles = remember {
        List(40) {
            ConfettiParticle(
                xNorm = Random.nextFloat(),
                yInitNorm = Random.nextFloat() * -0.5f,
                speed = 0.4f + Random.nextFloat() * 0.6f,
                size = 8f + Random.nextFloat() * 12f,
                color = colors.random(),
                isCircle = Random.nextBoolean()
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "confetti_fall")
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        particles.forEach { p ->
            val curY = ((p.yInitNorm + progress.value * p.speed) % 1.2f) * height
            val curX = (p.xNorm + kotlin.math.sin(progress.value * 6.28f + p.xNorm * 10f) * 0.05f) * width

            if (p.isCircle) {
                drawCircle(
                    color = p.color,
                    radius = p.size,
                    center = Offset(curX, curY)
                )
            } else {
                drawRect(
                    color = p.color,
                    topLeft = Offset(curX, curY),
                    size = Size(p.size * 1.5f, p.size)
                )
            }
        }
    }
}

private data class ConfettiParticle(
    val xNorm: Float,
    val yInitNorm: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val isCircle: Boolean
)
