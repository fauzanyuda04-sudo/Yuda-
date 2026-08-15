package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.StickerReward
import com.example.ui.theme.AlnauraCoral
import com.example.ui.theme.AlnauraPink
import com.example.ui.theme.AlnauraSkyBlue
import com.example.ui.theme.AlnauraYellow
import com.example.ui.theme.CardBackgroundWhite
import com.example.ui.theme.StarGold
import com.example.ui.theme.TextDarkNavy
import com.example.ui.theme.TextSubtitle

@Composable
fun StickerBookDialog(
    stickers: List<StickerReward>,
    totalStars: Int,
    onDismiss: () -> Unit
) {
    val unlockedCount = stickers.count { it.isUnlocked }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFFFFFDF8),
            shadowElevation = 16.dp,
            border = BorderStroke(2.dp, AlnauraYellow),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sticker_book_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AlnauraYellow,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "📖", fontSize = 20.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "Buku Stiker Alnaura yang Cantik",
                                color = TextDarkNavy,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "$unlockedCount dari ${stickers.size} Terbuka • ⭐ $totalStars Bintang",
                                color = AlnauraCoral,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextSubtitle)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stickers Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 8.dp),
                    modifier = Modifier.height(340.dp)
                ) {
                    items(stickers, key = { it.id }) { sticker ->
                        StickerItemCard(sticker = sticker, currentStars = totalStars)
                    }
                }
            }
        }
    }
}

@Composable
private fun StickerItemCard(
    sticker: StickerReward,
    currentStars: Int
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (sticker.isUnlocked) Color(0xFFFFF9E6) else Color(0xFFF1F5F9),
        border = BorderStroke(
            1.5.dp,
            if (sticker.isUnlocked) StarGold else Color(0xFFE2E8F0)
        ),
        shadowElevation = if (sticker.isUnlocked) 4.dp else 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        if (sticker.isUnlocked) Color.White else Color(0xFFE2E8F0),
                        CircleShape
                    )
            ) {
                if (sticker.isUnlocked) {
                    Text(text = sticker.emoji, fontSize = 24.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Terkunci",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = sticker.title,
                color = if (sticker.isUnlocked) TextDarkNavy else Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            if (sticker.isUnlocked) {
                Text(
                    text = "Terbuka ✨",
                    color = AlnauraCoral,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = StarGold,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${sticker.requiredStars}",
                        color = Color(0xFF64748B),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
