package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlnauraCoral
import com.example.ui.theme.AlnauraYellow
import com.example.ui.theme.AlnauraYellowDark
import com.example.ui.theme.StarGold
import com.example.ui.theme.TextDarkNavy

@Composable
fun ScoreStreakBadge(
    score: Int,
    streak: Int,
    totalStars: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Star Score Pill
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFFFF9E6),
            border = BorderStroke(1.5.dp, StarGold),
            shadowElevation = 2.dp,
            modifier = Modifier.testTag("score_star_badge")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = StarGold,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "${totalStars ?: score}",
                    color = TextDarkNavy,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
        }

        // Streak Flame multiplier (if active)
        if (streak > 1) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFFFEBEB),
                border = BorderStroke(1.5.dp, AlnauraCoral),
                shadowElevation = 2.dp,
                modifier = Modifier.testTag("streak_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(text = "🔥", fontSize = 13.sp)
                    Text(
                        text = "${streak}x Kombo!",
                        color = AlnauraCoral,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
