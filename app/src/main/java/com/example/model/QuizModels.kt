package com.example.model

enum class QuizCategory(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val primaryColorHex: Long,
    val accentColorHex: Long
) {
    ANIMALS("animals", "Dunia Hewan", "Suara & Fakta Unik", "🐾", 0xFFFF9F43, 0xFFFFF2E2),
    FRUITS("fruits", "Buah & Sayur", "Makanan Sehat Bergizi", "🍎", 0xFFFF6B6B, 0xFFFFE8E8),
    COLORS_SHAPES("colors_shapes", "Warna & Angka", "Bentuk & Berhitung Ceria", "🎨", 0xFF4D96FF, 0xFFEBF3FF),
    VEHICLES("vehicles", "Benda & Kendaraan", "Mobil, Pesawat, Kereta", "🚗", 0xFF6BCB77, 0xFFEEFAEF),
    GOOD_HABITS("good_habits", "Tata Krama Baik", "Sopan Santun & Kebaikan", "🌟", 0xFF9D4EDD, 0xFFF5ECFD),
    RIDDLES("riddles", "Teka-Teki Ceria", "Siapakah Aku?", "🧩", 0xFFFF7675, 0xFFFFEBEB),
    CUSTOM("custom", "Kuis Buatan Sendiri", "Soal dari Ayah, Ibu & Teman", "✏️", 0xFF00CEC9, 0xFFE0FAF9)
}

enum class AlnauraEmotion {
    HAPPY,      // Senang dan menyapa
    THINKING,   // Sedang berpikir / menunggu jawaban
    CHEERING,   // Bertepuk tangan / Benar!
    OOPS,       // Hampir benar / Semangat lagi!
    CELEBRATING // Menang / Dapat stiker baru
}

data class QuizOption(
    val id: String,
    val text: String,
    val emoji: String = "",
    val isCorrect: Boolean
)

data class Question(
    val id: String,
    val category: QuizCategory,
    val questionText: String,
    val emojiHint: String = "✨",
    val options: List<QuizOption>,
    val funFact: String, // Penjelasan ceria dari Alnaura setelah menjawab
    val hintText: String = "",
    val isCustom: Boolean = false
)

data class StickerReward(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val requiredStars: Int,
    val isUnlocked: Boolean = false
)

data class AdventureLevel(
    val levelNumber: Int,
    val title: String,
    val category: QuizCategory,
    val targetScore: Int,
    val earnedStars: Int = 0, // 0 to 3 stars
    val isUnlocked: Boolean = false
)

enum class AppScreen {
    HOME,
    PLAYING,
    RESULT,
    STICKER_BOOK,
    CUSTOM_QUIZ_LIST
}
