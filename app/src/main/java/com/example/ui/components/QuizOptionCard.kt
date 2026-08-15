package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.QuizOption
import com.example.ui.theme.AlnauraCoral
import com.example.ui.theme.AlnauraMintGreen
import com.example.ui.theme.AlnauraPink
import com.example.ui.theme.AlnauraPurple
import com.example.ui.theme.AlnauraSkyBlue
import com.example.ui.theme.AlnauraYellow
import com.example.ui.theme.CardBackgroundWhite
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.TextDarkNavy
import com.example.ui.theme.WrongRed

@Composable
fun QuizOptionCard(
    option: QuizOption,
    index: Int,
    isSelected: Boolean,
    isAnswerRevealed: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val optionLetters = listOf("A", "B", "C", "D")
    val letter = optionLetters.getOrElse(index) { "${index + 1}" }

    val defaultPillColors = listOf(
        Pair(AlnauraSkyBlue, Color(0xFFEBF3FF)),
        Pair(AlnauraCoral, Color(0xFFFFEBEB)),
        Pair(AlnauraMintGreen, Color(0xFFEFFBF1)),
        Pair(AlnauraPurple, Color(0xFFF5ECFD))
    )
    val pillColorPair = defaultPillColors.getOrElse(index % defaultPillColors.size) { Pair(AlnauraYellow, Color(0xFFFFF9E6)) }

    // Color logic
    val targetBgColor = when {
        isAnswerRevealed && option.isCorrect -> Color(0xFFE8FDF0) // Highlight correct
        isAnswerRevealed && isSelected && !option.isCorrect -> Color(0xFFFFEEEE) // Selected wrong
        isSelected -> pillColorPair.second
        else -> CardBackgroundWhite
    }

    val targetBorderColor = when {
        isAnswerRevealed && option.isCorrect -> CorrectGreen
        isAnswerRevealed && isSelected && !option.isCorrect -> WrongRed
        isSelected -> pillColorPair.first
        else -> Color(0xFFE2E8F0)
    }

    val animatedBg by animateColorAsState(targetValue = targetBgColor, animationSpec = tween(300), label = "bg_anim")
    val animatedBorder by animateColorAsState(targetValue = targetBorderColor, animationSpec = tween(300), label = "border_anim")

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = animatedBg),
        border = BorderStroke(if (isAnswerRevealed && (option.isCorrect || isSelected)) 2.5.dp else 1.5.dp, animatedBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = !isAnswerRevealed) { onSelect() }
            .testTag("quiz_option_${index}_${option.id}")
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Letter Badge Circle (A, B, C, D)
                Surface(
                    shape = CircleShape,
                    color = when {
                        isAnswerRevealed && option.isCorrect -> CorrectGreen
                        isAnswerRevealed && isSelected && !option.isCorrect -> WrongRed
                        else -> pillColorPair.first
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = letter,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Text & Emoji
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (option.emoji.isNotBlank()) {
                        Text(text = option.emoji, fontSize = 20.sp)
                    }
                    Text(
                        text = option.text,
                        color = TextDarkNavy,
                        fontWeight = if (isSelected || (isAnswerRevealed && option.isCorrect)) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            // Status icon if revealed
            if (isAnswerRevealed) {
                if (option.isCorrect) {
                    Surface(
                        shape = CircleShape,
                        color = CorrectGreen,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Benar",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else if (isSelected && !option.isCorrect) {
                    Surface(
                        shape = CircleShape,
                        color = WrongRed,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Kurang Tepat",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
