package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.AdventureLevel
import com.example.model.AppScreen
import com.example.model.QuizCategory
import com.example.ui.AlnauraUiState
import com.example.ui.AlnauraViewModel
import com.example.ui.components.AddQuestionDialog
import com.example.ui.components.AlnauraCharacterBubble
import com.example.ui.components.ScoreStreakBadge
import com.example.ui.components.StickerBookDialog
import com.example.ui.components.VoiceSettingsDialog
import com.example.ui.theme.AlnauraCoral
import com.example.ui.theme.AlnauraMintGreen
import com.example.ui.theme.AlnauraPink
import com.example.ui.theme.AlnauraPurple
import com.example.ui.theme.AlnauraSkyBlue
import com.example.ui.theme.AlnauraYellow
import com.example.ui.theme.AlnauraYellowDark
import com.example.ui.theme.CardBackgroundWhite
import com.example.ui.theme.PastelCreamBg
import com.example.ui.theme.StarGold
import com.example.ui.theme.TextDarkNavy
import com.example.ui.theme.TextSubtitle

@Composable
fun AlnauraHomeScreen(
    uiState: AlnauraUiState,
    viewModel: AlnauraViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showStickerDialog by remember { mutableStateOf(false) }

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
                        listOf(Color(0xFFFFF9E6), PastelCreamBg, Color(0xFFF0F7FF))
                    )
                )
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Header Row
                item(span = { GridItemSpan(maxLineSpan) }) {
                    HomeTopBar(
                        totalStars = uiState.totalStars,
                        isVoiceEnabled = uiState.isVoiceEnabled,
                        onToggleVoice = { viewModel.toggleVoice() },
                        onOpenVoiceSettings = { viewModel.setVoiceSettingsDialogVisible(true) },
                        onOpenStickerBook = { showStickerDialog = true },
                        onOpenAddQuestion = { viewModel.setAddQuestionDialogVisible(true) }
                    )
                }

                // Alnaura Hero Character Speech Bubble
                item(span = { GridItemSpan(maxLineSpan) }) {
                    AlnauraCharacterBubble(
                        speechText = uiState.alnauraSpeechText,
                        emotion = uiState.alnauraEmotion,
                        isVoiceEnabled = uiState.isVoiceEnabled,
                        onSpeakClick = { viewModel.replayCurrentVoice() }
                    )
                }

                // Navigation Tabs (Kategori Kuis vs Petualangan Level)
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = CardBackgroundWhite,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = Color.Transparent,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = AlnauraSkyBlue,
                                    height = 3.dp
                                )
                            }
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🎯", fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Pilih Kategori",
                                            fontWeight = if (selectedTab == 0) FontWeight.Black else FontWeight.SemiBold,
                                            color = if (selectedTab == 0) AlnauraSkyBlue else TextSubtitle,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🚀", fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Peta Petualangan",
                                            fontWeight = if (selectedTab == 1) FontWeight.Black else FontWeight.SemiBold,
                                            color = if (selectedTab == 1) AlnauraCoral else TextSubtitle,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                // Content based on tab
                if (selectedTab == 0) {
                    // CATEGORIES GRID
                    items(QuizCategory.values(), key = { it.id }) { category ->
                        CategoryCard(
                            category = category,
                            onSelect = { viewModel.startQuizByCategory(category) }
                        )
                    }
                } else {
                    // ADVENTURE LEVELS
                    items(uiState.adventureLevels, key = { it.levelNumber }) { level ->
                        AdventureLevelCard(
                            level = level,
                            onPlay = { if (level.isUnlocked) viewModel.startAdventureLevel(level) }
                        )
                    }
                }

                // Bottom Quick Action: Custom Question Banner
                item(span = { GridItemSpan(maxLineSpan) }) {
                    CustomQuizBanner(
                        customCount = uiState.customQuestions.size,
                        onAddClick = { viewModel.setAddQuestionDialogVisible(true) },
                        onPlayCustomClick = { viewModel.startQuizByCategory(QuizCategory.CUSTOM) }
                    )
                }
            }
        }
    }

    if (showStickerDialog) {
        StickerBookDialog(
            stickers = uiState.unlockedStickers,
            totalStars = uiState.totalStars,
            onDismiss = { showStickerDialog = false }
        )
    }

    if (uiState.showAddQuestionDialog) {
        AddQuestionDialog(
            onDismiss = { viewModel.setAddQuestionDialogVisible(false) },
            onSaveQuestion = { qText, cAns, w1, w2, w3, fact, emoji ->
                viewModel.addCustomQuestion(qText, cAns, w1, w2, w3, fact, emoji)
            }
        )
    }

    if (uiState.showVoiceSettingsDialog) {
        VoiceSettingsDialog(
            currentPreset = uiState.voicePreset,
            currentPitch = uiState.voicePitch,
            currentRate = uiState.voiceRate,
            onSelectPreset = { preset -> viewModel.setVoicePreset(preset) },
            onAdjustPitchRate = { pitch, rate -> viewModel.setCustomPitchAndRate(pitch, rate) },
            onTestVoice = { pitch, rate -> viewModel.testVoice(pitch, rate) },
            onDismiss = { viewModel.setVoiceSettingsDialogVisible(false) }
        )
    }
}

@Composable
private fun HomeTopBar(
    totalStars: Int,
    isVoiceEnabled: Boolean,
    onToggleVoice: () -> Unit,
    onOpenVoiceSettings: () -> Unit,
    onOpenStickerBook: () -> Unit,
    onOpenAddQuestion: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Title Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AlnauraYellow,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "👧", fontSize = 22.sp)
                }
            }
            Column {
                Text(
                    text = "Alnaura yang Cantik",
                    color = TextDarkNavy,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Text(
                    text = "Game Spesial Alnaura yang Cantik ✨",
                    color = AlnauraCoral,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        // Action Buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Star score pill
            ScoreStreakBadge(score = totalStars, streak = 0, totalStars = totalStars)

            // Sticker Book Button
            Surface(
                shape = CircleShape,
                color = CardBackgroundWhite,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable { onOpenStickerBook() }
                    .testTag("sticker_book_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "📖", fontSize = 18.sp)
                }
            }

            // Voice settings dialog button
            Surface(
                shape = CircleShape,
                color = CardBackgroundWhite,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable { onOpenVoiceSettings() }
                    .testTag("voice_settings_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "🎙️", fontSize = 18.sp)
                }
            }

            // Voice toggle button
            Surface(
                shape = CircleShape,
                color = if (isVoiceEnabled) AlnauraSkyBlue else Color(0xFFE2E8F0),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable { onToggleVoice() }
                    .testTag("toggle_voice_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isVoiceEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "Suara Alnaura",
                        tint = if (isVoiceEnabled) Color.White else Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: QuizCategory,
    onSelect: () -> Unit
) {
    val primaryColor = Color(category.primaryColorHex)
    val bgColor = Color(category.accentColorHex)

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundWhite),
        border = BorderStroke(2.dp, primaryColor.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable { onSelect() }
            .testTag("category_card_${category.id}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .background(bgColor, CircleShape)
            ) {
                Text(text = category.iconEmoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = category.title,
                color = TextDarkNavy,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = category.subtitle,
                color = TextSubtitle,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = primaryColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Main Ceria",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AdventureLevelCard(
    level: AdventureLevel,
    onPlay: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (level.isUnlocked) CardBackgroundWhite else Color(0xFFF1F5F9)
        ),
        border = BorderStroke(
            1.5.dp,
            if (level.isUnlocked) AlnauraCoral.copy(alpha = 0.5f) else Color(0xFFCBD5E1)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (level.isUnlocked) 4.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = level.isUnlocked) { onPlay() }
            .testTag("adventure_level_${level.levelNumber}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (level.isUnlocked) AlnauraCoral else Color(0xFF94A3B8),
                    modifier = Modifier.padding(2.dp)
                ) {
                    Text(
                        text = "Level ${level.levelNumber}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (!level.isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Terkunci",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Row {
                        repeat(3) { idx ->
                            val isEarned = idx < level.earnedStars
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (isEarned) StarGold else Color(0xFFE2E8F0),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = level.title,
                color = if (level.isUnlocked) TextDarkNavy else Color(0xFF94A3B8),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Target: ${level.targetScore} Poin",
                color = TextSubtitle,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (level.isUnlocked) AlnauraSkyBlue else Color(0xFFCBD5E1),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (level.isUnlocked) "Mulai Level" else "Terkunci",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomQuizBanner(
    customCount: Int,
    onAddClick: () -> Unit,
    onPlayCustomClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFFE6FAF9),
        border = BorderStroke(1.5.dp, Color(0xFF00CEC9)),
        shadowElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF00CEC9),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "✏️", fontSize = 20.sp)
                    }
                }
                Column {
                    Text(
                        text = "Kuis Alnaura yang Cantik",
                        color = TextDarkNavy,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "$customCount soal buatan khusus Alnaura yang cantik",
                        color = TextSubtitle,
                        fontSize = 11.sp
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF00CEC9),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onAddClick() }
                        .testTag("home_add_custom_question")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Text(text = "Buat Soal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
