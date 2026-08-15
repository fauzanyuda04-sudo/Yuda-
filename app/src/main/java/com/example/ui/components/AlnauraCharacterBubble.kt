package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AlnauraEmotion
import com.example.ui.theme.AlnauraCoral
import com.example.ui.theme.AlnauraPink
import com.example.ui.theme.AlnauraPurple
import com.example.ui.theme.AlnauraSkyBlue
import com.example.ui.theme.AlnauraYellow
import com.example.ui.theme.CardBackgroundWhite
import com.example.ui.theme.TextDarkNavy

@Composable
fun AlnauraCharacterBubble(
    speechText: String,
    emotion: AlnauraEmotion,
    isVoiceEnabled: Boolean,
    onSpeakClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    // Joyful breathing / bounce animation
    val infiniteTransition = rememberInfiniteTransition(label = "alnaura_bounce")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce_y"
    )

    val emotionEmoji = when (emotion) {
        AlnauraEmotion.HAPPY -> "😄"
        AlnauraEmotion.THINKING -> "🤔"
        AlnauraEmotion.CHEERING -> "👏🎉"
        AlnauraEmotion.OOPS -> "✨💪"
        AlnauraEmotion.CELEBRATING -> "🏆🥳"
    }

    val bubbleGradient = when (emotion) {
        AlnauraEmotion.CHEERING -> listOf(Color(0xFFE8FDF0), Color(0xFFF4FFF8))
        AlnauraEmotion.OOPS -> listOf(Color(0xFFFFF3F3), Color(0xFFFFF9F9))
        AlnauraEmotion.CELEBRATING -> listOf(Color(0xFFFFF7DB), Color(0xFFFFFDF5))
        else -> listOf(CardBackgroundWhite, Color(0xFFFAF8F5))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Alnaura Avatar with Cute Circular Glow
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .offset(y = bounceOffset.dp)
                .size(if (isCompact) 72.dp else 88.dp)
                .testTag("alnaura_avatar_view")
        ) {
            Surface(
                shape = CircleShape,
                color = AlnauraYellow,
                border = androidx.compose.foundation.BorderStroke(
                    3.dp,
                    Brush.sweepGradient(listOf(AlnauraCoral, AlnauraYellow, AlnauraSkyBlue, AlnauraPink, AlnauraCoral))
                ),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .size(if (isCompact) 68.dp else 84.dp)
                    .align(Alignment.Center)
            ) {
                Image(
                    painter = painterResource(
                        id = if (emotion == AlnauraEmotion.CELEBRATING) R.drawable.alnaura_celebrate else R.drawable.alnaura_avatar
                    ),
                    contentDescription = "Karakter Alnaura Ceria",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.clip(CircleShape)
                )
            }

            // Emotion status badge
            Surface(
                shape = CircleShape,
                color = CardBackgroundWhite,
                shadowElevation = 4.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.size(26.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = emotionEmoji, fontSize = 13.sp)
                }
            }
        }

        // Speech Bubble
        Surface(
            shape = RoundedCornerShape(
                topStart = 6.dp,
                topEnd = 20.dp,
                bottomEnd = 20.dp,
                bottomStart = 20.dp
            ),
            color = bubbleGradient.first(),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (emotion == AlnauraEmotion.CHEERING) Color(0xFF6BCB77) else if (emotion == AlnauraEmotion.OOPS) AlnauraCoral else Color(0xFFFFD166)
            ),
            shadowElevation = 6.dp,
            modifier = Modifier
                .weight(1f)
                .clip(
                    RoundedCornerShape(
                        topStart = 6.dp,
                        topEnd = 20.dp,
                        bottomEnd = 20.dp,
                        bottomStart = 20.dp
                    )
                )
                .clickable { onSpeakClick() }
                .testTag("alnaura_speech_bubble")
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Alnaura yang Cantik",
                            color = AlnauraCoral,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                        Text(text = "💖✨", fontSize = 11.sp)
                    }

                    // Audio speaker button
                    Surface(
                        shape = CircleShape,
                        color = if (isVoiceEnabled) AlnauraSkyBlue.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .clickable { onSpeakClick() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Dengarkan Suara Alnaura",
                                tint = if (isVoiceEnabled) AlnauraSkyBlue else Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = speechText,
                    color = TextDarkNavy,
                    fontSize = if (isCompact) 13.sp else 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 19.sp
                )
            }
        }
    }
}
