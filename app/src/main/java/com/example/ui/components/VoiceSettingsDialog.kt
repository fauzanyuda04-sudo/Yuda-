package com.example.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.speech.VoicePreset
import com.example.ui.theme.AlnauraCoral
import com.example.ui.theme.AlnauraMintGreen
import com.example.ui.theme.AlnauraPink
import com.example.ui.theme.AlnauraPurple
import com.example.ui.theme.AlnauraSkyBlue
import com.example.ui.theme.CardBackgroundWhite
import com.example.ui.theme.PastelCreamBg
import com.example.ui.theme.StarGold
import com.example.ui.theme.TextDarkNavy
import com.example.ui.theme.TextSubtitle
import java.util.Locale

@Composable
fun VoiceSettingsDialog(
    currentPreset: VoicePreset,
    currentPitch: Float,
    currentRate: Float,
    onSelectPreset: (VoicePreset) -> Unit,
    onAdjustPitchRate: (pitch: Float, rate: Float) -> Unit,
    onTestVoice: (pitch: Float, rate: Float) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPreset by remember { mutableStateOf(currentPreset) }
    var pitch by remember { mutableFloatStateOf(currentPitch) }
    var rate by remember { mutableFloatStateOf(currentRate) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CardBackgroundWhite,
            shadowElevation = 12.dp,
            modifier = modifier
                .fillMaxWidth()
                .testTag("voice_settings_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(AlnauraCoral, AlnauraPink)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎙️", fontSize = 22.sp)
                        }
                        Column {
                            Text(
                                text = "Pengaturan Suara Manusia",
                                color = TextDarkNavy,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Pilih karakter suara alami Alnaura",
                                color = TextSubtitle,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = TextSubtitle
                        )
                    }
                }

                // Preset selection list
                Text(
                    text = "PILIHAN KARAKTER SUARA:",
                    color = TextSubtitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    VoicePreset.values().forEach { preset ->
                        val isSelected = selectedPreset == preset
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) AlnauraCoral.copy(alpha = 0.12f)
                                    else PastelCreamBg.copy(alpha = 0.5f)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) AlnauraCoral else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    selectedPreset = preset
                                    pitch = preset.pitch
                                    rate = preset.rate
                                    onSelectPreset(preset)
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = preset.emoji, fontSize = 22.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = preset.label,
                                    color = if (isSelected) AlnauraCoral else TextDarkNavy,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = preset.subtitle,
                                    color = TextSubtitle,
                                    fontSize = 11.sp
                                )
                            }
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(AlnauraCoral),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Fine-tuning Sliders
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PastelCreamBg)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "PENYESUAIAN HALUS (NADA & KECEPATAN)",
                        color = TextDarkNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )

                    // Pitch Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tinggi Nada Suara (Pitch)",
                                color = TextDarkNavy,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = String.format(Locale.US, "%.2fx", pitch),
                                color = AlnauraCoral,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = pitch,
                            onValueChange = {
                                pitch = it
                                onAdjustPitchRate(pitch, rate)
                            },
                            valueRange = 0.80f..1.30f,
                            colors = SliderDefaults.colors(
                                thumbColor = AlnauraCoral,
                                activeTrackColor = AlnauraCoral,
                                inactiveTrackColor = Color(0xFFE2E8F0)
                            )
                        )
                    }

                    // Rate Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Kecepatan Bicara (Speed)",
                                color = TextDarkNavy,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = String.format(Locale.US, "%.2fx", rate),
                                color = AlnauraSkyBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = rate,
                            onValueChange = {
                                rate = it
                                onAdjustPitchRate(pitch, rate)
                            },
                            valueRange = 0.70f..1.25f,
                            colors = SliderDefaults.colors(
                                thumbColor = AlnauraSkyBlue,
                                activeTrackColor = AlnauraSkyBlue,
                                inactiveTrackColor = Color(0xFFE2E8F0)
                            )
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onTestVoice(pitch, rate) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AlnauraPurple
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tes Suara",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            onAdjustPitchRate(pitch, rate)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AlnauraCoral
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Simpan & Pakai",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
