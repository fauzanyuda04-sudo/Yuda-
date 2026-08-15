package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.model.AppScreen
import com.example.ui.AlnauraUiState
import com.example.ui.AlnauraViewModel
import com.example.ui.components.AlnauraCharacterBubble
import com.example.ui.components.ConfettiEffect
import com.example.ui.components.StickerBookDialog
import com.example.ui.theme.AlnauraCoral
import com.example.ui.theme.AlnauraPink
import com.example.ui.theme.AlnauraSkyBlue
import com.example.ui.theme.AlnauraYellow
import com.example.ui.theme.CardBackgroundWhite
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.PastelCreamBg
import com.example.ui.theme.StarGold
import com.example.ui.theme.TextDarkNavy
import com.example.ui.theme.TextSubtitle

@Composable
fun AlnauraResultScreen(
    uiState: AlnauraUiState,
    viewModel: AlnauraViewModel,
    modifier: Modifier = Modifier
) {
    var showStickers by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "trophy_bounce")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Scaffold(
        containerColor = PastelCreamBg,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF9E6), PastelCreamBg, Color(0xFFE8F4FF))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Title Ribbon
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AlnauraCoral,
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = "🎉 Selamat Alnaura yang Cantik! Horeee! 🎉",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Celebration Character Image
                Surface(
                    shape = CircleShape,
                    color = AlnauraYellow,
                    border = BorderStroke(
                        4.dp,
                        Brush.sweepGradient(listOf(AlnauraCoral, AlnauraYellow, AlnauraSkyBlue, AlnauraPink, AlnauraCoral))
                    ),
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .size(120.dp)
                        .offset(y = bounceOffset.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.alnaura_celebrate),
                        contentDescription = "Alnaura Merayakan",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stars Earned Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("stars_earned_container")
                ) {
                    repeat(3) { index ->
                        val isFilled = index < uiState.starsEarnedThisRound
                        Surface(
                            shape = CircleShape,
                            color = if (isFilled) Color(0xFFFFF9E6) else Color(0xFFF1F5F9),
                            border = BorderStroke(2.dp, if (isFilled) StarGold else Color(0xFFCBD5E1)),
                            shadowElevation = if (isFilled) 6.dp else 1.dp,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Bintang",
                                    tint = if (isFilled) StarGold else Color(0xFFCBD5E1),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Alnaura Speech Feedback
                AlnauraCharacterBubble(
                    speechText = uiState.alnauraSpeechText,
                    emotion = uiState.alnauraEmotion,
                    isVoiceEnabled = uiState.isVoiceEnabled,
                    onSpeakClick = { viewModel.replayCurrentVoice() }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Score Card
                    StatBox(
                        title = "Total Skor",
                        value = "${uiState.score}",
                        emoji = "⭐",
                        bgColor = Color(0xFFFFF9E6),
                        borderColor = StarGold,
                        modifier = Modifier.weight(1f)
                    )

                    // Correct Answers Card
                    StatBox(
                        title = "Jawaban Benar",
                        value = "${uiState.correctAnswersCount}/${uiState.totalQuestions}",
                        emoji = "🎯",
                        bgColor = Color(0xFFE8FDF0),
                        borderColor = CorrectGreen,
                        modifier = Modifier.weight(1f)
                    )

                    // Total Stars Card
                    StatBox(
                        title = "Bintang Koleksi",
                        value = "${uiState.totalStars}",
                        emoji = "👑",
                        bgColor = Color(0xFFEDF5FF),
                        borderColor = AlnauraSkyBlue,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Play Again Button
                    Button(
                        onClick = { viewModel.startQuizByCategory(uiState.selectedCategory) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AlnauraSkyBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("play_again_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Text(
                                text = "Main Lagi Ceria 🚀",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                        }
                    }

                    // Open Sticker Book Button
                    Button(
                        onClick = { showStickers = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AlnauraYellow,
                            contentColor = TextDarkNavy
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("open_sticker_book_from_result")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "📖", fontSize = 18.sp)
                            Text(
                                text = "Buka Buku Stiker Hadiah",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Back Home Button
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("result_home_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = TextDarkNavy)
                            Text(
                                text = "Kembali ke Menu Utama",
                                color = TextDarkNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Confetti
            if (uiState.showConfetti) {
                ConfettiEffect()
            }
        }
    }

    if (showStickers) {
        StickerBookDialog(
            stickers = uiState.unlockedStickers,
            totalStars = uiState.totalStars,
            onDismiss = { showStickers = false }
        )
    }
}

@Composable
private fun StatBox(
    title: String,
    value: String,
    emoji: String,
    bgColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = bgColor,
        border = BorderStroke(1.5.dp, borderColor),
        shadowElevation = 2.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = TextDarkNavy,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = TextSubtitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
