package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AlnauraCoral
import com.example.ui.theme.AlnauraSkyBlue
import com.example.ui.theme.AlnauraYellow
import com.example.ui.theme.CardBackgroundWhite
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.TextDarkNavy
import com.example.ui.theme.TextSubtitle
import com.example.ui.theme.WrongRed

@Composable
fun AddQuestionDialog(
    onDismiss: () -> Unit,
    onSaveQuestion: (
        questionText: String,
        correctAnswer: String,
        wrongAnswer1: String,
        wrongAnswer2: String,
        wrongAnswer3: String,
        funFact: String,
        emojiHint: String
    ) -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf("") }
    var wrong1 by remember { mutableStateOf("") }
    var wrong2 by remember { mutableStateOf("") }
    var wrong3 by remember { mutableStateOf("") }
    var funFact by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("🌟") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val emojiChoices = listOf("🌟", "🐾", "🍎", "🎨", "🚗", "👑", "🌈", "🍕", "🍦", "🎈", "🚀", "🐱")

    val sampleTemplates = listOf(
        Pair("Apa warna kesukaan Alnaura yang cantik?", "Merah Muda"),
        Pair("Siapa anak yang paling cantik dan pintar?", "Alnaura yang Cantik"),
        Pair("Hewan apa yang bersuara petok-petok?", "Ayam Betina")
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFFFFFDF8),
            shadowElevation = 16.dp,
            border = BorderStroke(2.dp, AlnauraSkyBlue),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_custom_question_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
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
                            color = AlnauraSkyBlue,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "✏️", fontSize = 18.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "Buat Soal Kuis Baru",
                                color = TextDarkNavy,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Tambah pertanyaan seru khusus Alnaura yang cantik!",
                                color = TextSubtitle,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextSubtitle)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Templates
                Text(
                    text = "Inspirasi Cepat:",
                    color = AlnauraSkyBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(sampleTemplates) { (qSample, aSample) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEDF5FF),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    questionText = qSample
                                    correctAnswer = aSample
                                    wrong1 = "Pilihan Salah 1"
                                    wrong2 = "Pilihan Salah 2"
                                    wrong3 = "Pilihan Salah 3"
                                    funFact = "Fakta seru: $qSample adalah $aSample!"
                                }
                        ) {
                            Text(
                                text = qSample,
                                color = TextDarkNavy,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Emoji Picker
                Text(text = "Pilih Ikon Emoji:", color = TextDarkNavy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(emojiChoices) { emoji ->
                        Surface(
                            shape = CircleShape,
                            color = if (selectedEmoji == emoji) AlnauraYellow else Color(0xFFF1F5F9),
                            border = if (selectedEmoji == emoji) BorderStroke(2.dp, AlnauraCoral) else null,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .clickable { selectedEmoji = emoji }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = emoji, fontSize = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Question Input
                OutlinedTextField(
                    value = questionText,
                    onValueChange = {
                        questionText = it
                        errorMessage = null
                    },
                    label = { Text("Pertanyaan Kuis") },
                    placeholder = { Text("Contoh: Hewan apa yang suka makan rumput?") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AlnauraSkyBlue,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedContainerColor = CardBackgroundWhite,
                        unfocusedContainerColor = CardBackgroundWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_custom_question")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Correct Answer Input
                OutlinedTextField(
                    value = correctAnswer,
                    onValueChange = {
                        correctAnswer = it
                        errorMessage = null
                    },
                    label = { Text("Jawaban BENAR ✅") },
                    placeholder = { Text("Contoh: Sapi / Kambing") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CorrectGreen,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedContainerColor = CardBackgroundWhite,
                        unfocusedContainerColor = CardBackgroundWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_correct_answer")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Wrong Options
                Text(
                    text = "Pilihan Jawaban Lain (Salah):",
                    color = TextSubtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = wrong1,
                    onValueChange = { wrong1 = it },
                    label = { Text("Pilihan Salah 1") },
                    placeholder = { Text("Contoh: Buaya") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WrongRed,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedContainerColor = CardBackgroundWhite,
                        unfocusedContainerColor = CardBackgroundWhite
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = wrong2,
                        onValueChange = { wrong2 = it },
                        label = { Text("Pilihan Salah 2") },
                        placeholder = { Text("Contoh: Singa") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WrongRed,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedContainerColor = CardBackgroundWhite,
                            unfocusedContainerColor = CardBackgroundWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = wrong3,
                        onValueChange = { wrong3 = it },
                        label = { Text("Pilihan Salah 3") },
                        placeholder = { Text("Contoh: Serigala") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WrongRed,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedContainerColor = CardBackgroundWhite,
                            unfocusedContainerColor = CardBackgroundWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Fun Fact
                OutlinedTextField(
                    value = funFact,
                    onValueChange = { funFact = it },
                    label = { Text("Fakta / Penjelasan Ceria Alnaura") },
                    placeholder = { Text("Contoh: Sapi menghasilkan susu segar yang menyehatkan tubuh kita!") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AlnauraYellow,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedContainerColor = CardBackgroundWhite,
                        unfocusedContainerColor = CardBackgroundWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = WrongRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Batal",
                        color = TextSubtitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onDismiss() }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (questionText.isBlank()) {
                                errorMessage = "Mohon isi teks pertanyaan"
                                return@Button
                            }
                            if (correctAnswer.isBlank()) {
                                errorMessage = "Mohon isi jawaban yang benar"
                                return@Button
                            }
                            val w1 = wrong1.ifBlank { "Pilihan Lain 1" }
                            val w2 = wrong2.ifBlank { "Pilihan Lain 2" }
                            val w3 = wrong3.ifBlank { "Pilihan Lain 3" }
                            onSaveQuestion(
                                questionText.trim(),
                                correctAnswer.trim(),
                                w1.trim(),
                                w2.trim(),
                                w3.trim(),
                                funFact.trim(),
                                selectedEmoji
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AlnauraSkyBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("submit_custom_question_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Simpan Soal", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
