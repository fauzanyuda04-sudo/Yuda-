package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppScreen
import com.example.ui.AlnauraUiState
import com.example.ui.AlnauraViewModel
import com.example.ui.components.AlnauraCharacterBubble
import com.example.ui.components.ConfettiEffect
import com.example.ui.components.QuizOptionCard
import com.example.ui.components.ScoreStreakBadge
import com.example.ui.theme.AlnauraCoral
import com.example.ui.theme.AlnauraMintGreen
import com.example.ui.theme.AlnauraSkyBlue
import com.example.ui.theme.AlnauraYellow
import com.example.ui.theme.CardBackgroundWhite
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.PastelCreamBg
import com.example.ui.theme.TextDarkNavy
import com.example.ui.theme.TextSubtitle

@Composable
fun AlnauraQuizPlayScreen(
    uiState: AlnauraUiState,
    viewModel: AlnauraViewModel,
    modifier: Modifier = Modifier
) {
    val currentQ = uiState.currentQuestion
    val progressAnimated by animateFloatAsState(targetValue = uiState.progressPercent, label = "progress_anim")
    val categoryColor = Color(uiState.selectedCategory.primaryColorHex)

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
                        listOf(Color(0xFFFFFDF8), PastelCreamBg, Color(0xFFF1F7FF))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    Surface(
                        shape = CircleShape,
                        color = CardBackgroundWhite,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { viewModel.navigateTo(AppScreen.HOME) }
                            .testTag("quiz_back_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = TextDarkNavy, modifier = Modifier.size(20.dp))
                        }
                    }

                    // Category Pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(uiState.selectedCategory.accentColorHex),
                        border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.5f)),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = uiState.selectedCategory.iconEmoji, fontSize = 14.sp)
                            Text(
                                text = uiState.selectedCategory.title,
                                color = TextDarkNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Score and Streak
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ScoreStreakBadge(score = uiState.score, streak = uiState.streak)

                        // Voice Button
                        Surface(
                            shape = CircleShape,
                            color = if (uiState.isVoiceEnabled) AlnauraSkyBlue else Color(0xFFE2E8F0),
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .clickable { viewModel.toggleVoice() }
                                .testTag("quiz_toggle_voice")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (uiState.isVoiceEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                    contentDescription = "Suara",
                                    tint = if (uiState.isVoiceEnabled) Color.White else Color(0xFF64748B),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar with question number indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Soal ${uiState.currentQuestionIndex + 1} dari ${uiState.totalQuestions}",
                        color = AlnauraCoral,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${(uiState.progressPercent * 100).toInt()}% Selesai",
                        color = TextSubtitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { progressAnimated },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = AlnauraSkyBlue,
                    trackColor = Color(0xFFE2E8F0),
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Alnaura speech and emotion feedback bubble
                AlnauraCharacterBubble(
                    speechText = uiState.alnauraSpeechText,
                    emotion = uiState.alnauraEmotion,
                    isVoiceEnabled = uiState.isVoiceEnabled,
                    onSpeakClick = { viewModel.replayCurrentVoice() },
                    isCompact = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Question Card
                if (currentQ != null) {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackgroundWhite),
                        border = BorderStroke(2.dp, categoryColor.copy(alpha = 0.35f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("current_question_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Emoji Visual
                            Surface(
                                shape = CircleShape,
                                color = Color(uiState.selectedCategory.accentColorHex),
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = currentQ.emojiHint, fontSize = 28.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = currentQ.questionText,
                                color = TextDarkNavy,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Options List
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        currentQ.options.forEachIndexed { idx, option ->
                            QuizOptionCard(
                                option = option,
                                index = idx,
                                isSelected = uiState.selectedOptionId == option.id,
                                isAnswerRevealed = uiState.isAnswerRevealed,
                                onSelect = { viewModel.selectOption(option) }
                            )
                        }
                    }

                    // Fun Fact Explanation (Revealed after answering)
                    AnimatedVisibility(
                        visible = uiState.isAnswerRevealed,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (uiState.isAnswerCorrect == true) Color(0xFFE8FDF0) else Color(0xFFFFF3F3),
                            border = BorderStroke(
                                1.5.dp,
                                if (uiState.isAnswerCorrect == true) CorrectGreen else AlnauraCoral
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (uiState.isAnswerCorrect == true) "💡" else "🌟",
                                    fontSize = 22.sp
                                )
                                Column {
                                    Text(
                                        text = if (uiState.isAnswerCorrect == true) "Fakta Seru Alnaura:" else "Tahukah Kamu?",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (uiState.isAnswerCorrect == true) CorrectGreen else AlnauraCoral
                                    )
                                    Text(
                                        text = currentQ.funFact,
                                        fontSize = 12.sp,
                                        color = TextDarkNavy,
                                        fontWeight = FontWeight.SemiBold,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bottom Navigation Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hint Button
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFFFF9E6),
                            border = BorderStroke(1.dp, AlnauraYellow),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.toggleHintDialog(true) }
                                .testTag("hint_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Petunjuk",
                                    tint = AlnauraCoral,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Bantuan",
                                    color = TextDarkNavy,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Next Question Button (Enabled after answer is revealed)
                        if (uiState.isAnswerRevealed) {
                            val isLast = uiState.currentQuestionIndex + 1 >= uiState.totalQuestions
                            Button(
                                onClick = { viewModel.nextQuestion() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isLast) CorrectGreen else AlnauraSkyBlue,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                                modifier = Modifier.testTag("next_question_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (isLast) "Lihat Hasil Kuis" else "Soal Berikutnya",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                    Icon(
                                        imageVector = if (isLast) Icons.Default.EmojiEvents else Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Confetti effect overlay on correct answer
            if (uiState.showConfetti) {
                ConfettiEffect()
            }
        }
    }

    // Hint Dialog
    if (uiState.showHintDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleHintDialog(false) },
            shape = RoundedCornerShape(22.dp),
            containerColor = Color(0xFFFFFDF8),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "💡", fontSize = 22.sp)
                    Text(
                        text = "Petunjuk dari Alnaura",
                        fontWeight = FontWeight.Black,
                        color = TextDarkNavy,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Text(
                    text = currentQ?.hintText?.ifBlank { "Perhatikan baik-baik ciri khas dari pertanyaannya ya!" } ?: "",
                    fontSize = 14.sp,
                    color = TextDarkNavy,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.toggleHintDialog(false) },
                    colors = ButtonDefaults.buttonColors(containerColor = AlnauraSkyBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Mengerti! ✨", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
