package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AdventureLevel
import com.example.model.Question
import com.example.model.QuizCategory
import com.example.model.QuizOption
import com.example.model.StickerReward
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class QuizRepository(context: Context? = null) {

    private val prefs: SharedPreferences? = context?.getSharedPreferences("alnaura_quiz_prefs", Context.MODE_PRIVATE)

    private val _customQuestions = MutableStateFlow<List<Question>>(emptyList())
    val customQuestions: StateFlow<List<Question>> = _customQuestions.asStateFlow()

    private val _totalStars = MutableStateFlow(0)
    val totalStars: StateFlow<Int> = _totalStars.asStateFlow()

    private val _unlockedStickerIds = MutableStateFlow<Set<String>>(emptySet())
    val unlockedStickerIds: StateFlow<Set<String>> = _unlockedStickerIds.asStateFlow()

    init {
        loadPersistedData()
    }

    private fun loadPersistedData() {
        prefs?.let {
            _totalStars.value = it.getInt("total_stars", 15) // Start with some encouraging stars
            val savedStickers = it.getStringSet("unlocked_stickers", setOf("stk_welcome", "stk_first_star")) ?: setOf("stk_welcome", "stk_first_star")
            _unlockedStickerIds.value = savedStickers

            // Load custom questions JSON
            val customJson = it.getString("custom_questions_json", null)
            if (!customJson.isNullOrBlank()) {
                try {
                    val list = mutableListOf<Question>()
                    val array = JSONArray(customJson)
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val optionsArray = obj.getJSONArray("options")
                        val options = mutableListOf<QuizOption>()
                        for (j in 0 until optionsArray.length()) {
                            val optObj = optionsArray.getJSONObject(j)
                            options.add(
                                QuizOption(
                                    id = optObj.getString("id"),
                                    text = optObj.getString("text"),
                                    emoji = optObj.optString("emoji", "⭐"),
                                    isCorrect = optObj.getBoolean("isCorrect")
                                )
                            )
                        }
                        list.add(
                            Question(
                                id = obj.getString("id"),
                                category = QuizCategory.CUSTOM,
                                questionText = obj.getString("questionText"),
                                emojiHint = obj.optString("emojiHint", "✏️"),
                                options = options,
                                funFact = obj.optString("funFact", "Pertanyaan kustom ceria!"),
                                hintText = obj.optString("hintText", ""),
                                isCustom = true
                            )
                        )
                    }
                    _customQuestions.value = list
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun addStars(stars: Int) {
        val newTotal = _totalStars.value + stars
        _totalStars.value = newTotal
        prefs?.edit()?.putInt("total_stars", newTotal)?.apply()
        checkStickerUnlocks(newTotal)
    }

    private fun checkStickerUnlocks(stars: Int) {
        val allStickers = getAllStickers()
        val currentSet = _unlockedStickerIds.value.toMutableSet()
        var changed = false
        allStickers.forEach { sticker ->
            if (stars >= sticker.requiredStars && !currentSet.contains(sticker.id)) {
                currentSet.add(sticker.id)
                changed = true
            }
        }
        if (changed) {
            _unlockedStickerIds.value = currentSet
            prefs?.edit()?.putStringSet("unlocked_stickers", currentSet)?.apply()
        }
    }

    fun addCustomQuestion(
        questionText: String,
        correctAnswer: String,
        wrongAnswer1: String,
        wrongAnswer2: String,
        wrongAnswer3: String,
        funFact: String,
        emojiHint: String
    ): Question {
        val id = "custom_${System.currentTimeMillis()}"
        val options = listOf(
            QuizOption("opt_1", correctAnswer, "🌟", isCorrect = true),
            QuizOption("opt_2", wrongAnswer1, "✨", isCorrect = false),
            QuizOption("opt_3", wrongAnswer2, "🎈", isCorrect = false),
            QuizOption("opt_4", wrongAnswer3, "🎯", isCorrect = false)
        ).shuffled()

        val newQ = Question(
            id = id,
            category = QuizCategory.CUSTOM,
            questionText = questionText,
            emojiHint = emojiHint.ifBlank { "✏️" },
            options = options,
            funFact = funFact.ifBlank { "Jawaban yang hebat! Teruslah belajar bersama Alnaura ya!" },
            hintText = "Pikirkan baik-baik ya!",
            isCustom = true
        )

        val updated = _customQuestions.value + newQ
        _customQuestions.value = updated
        saveCustomQuestions(updated)
        return newQ
    }

    fun deleteCustomQuestion(id: String) {
        val updated = _customQuestions.value.filterNot { it.id == id }
        _customQuestions.value = updated
        saveCustomQuestions(updated)
    }

    private fun saveCustomQuestions(list: List<Question>) {
        try {
            val array = JSONArray()
            list.forEach { q ->
                val obj = JSONObject()
                obj.put("id", q.id)
                obj.put("questionText", q.questionText)
                obj.put("emojiHint", q.emojiHint)
                obj.put("funFact", q.funFact)
                obj.put("hintText", q.hintText)
                val optsArray = JSONArray()
                q.options.forEach { opt ->
                    val optObj = JSONObject()
                    optObj.put("id", opt.id)
                    optObj.put("text", opt.text)
                    optObj.put("emoji", opt.emoji)
                    optObj.put("isCorrect", opt.isCorrect)
                    optsArray.put(optObj)
                }
                obj.put("options", optsArray)
                array.put(obj)
            }
            prefs?.edit()?.putString("custom_questions_json", array.toString())?.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getQuestionsByCategory(category: QuizCategory): List<Question> {
        if (category == QuizCategory.CUSTOM) {
            return if (_customQuestions.value.isEmpty()) getDefaultCustomQuestions() else _customQuestions.value
        }
        return getBuiltInQuestions().filter { it.category == category }
    }

    fun getAllQuestions(): List<Question> {
        return getBuiltInQuestions() + _customQuestions.value
    }

    fun getAdventureLevels(): List<AdventureLevel> {
        val stars = _totalStars.value
        return listOf(
            AdventureLevel(1, "Sahabat Hewan Ceria", QuizCategory.ANIMALS, targetScore = 30, earnedStars = 3, isUnlocked = true),
            AdventureLevel(2, "Kebun Buah Segar", QuizCategory.FRUITS, targetScore = 40, earnedStars = if (stars >= 20) 3 else 2, isUnlocked = true),
            AdventureLevel(3, "Pelangi Warna & Angka", QuizCategory.COLORS_SHAPES, targetScore = 50, earnedStars = if (stars >= 30) 2 else 0, isUnlocked = stars >= 10),
            AdventureLevel(4, "Petualangan Kendaraan", QuizCategory.VEHICLES, targetScore = 50, earnedStars = if (stars >= 40) 2 else 0, isUnlocked = stars >= 20),
            AdventureLevel(5, "Anak Pintar & Sopan", QuizCategory.GOOD_HABITS, targetScore = 60, earnedStars = 0, isUnlocked = stars >= 30),
            AdventureLevel(6, "Teka-Teki Misteri Alnaura", QuizCategory.RIDDLES, targetScore = 70, earnedStars = 0, isUnlocked = stars >= 45),
            AdventureLevel(7, "Pakar Hewan Liar", QuizCategory.ANIMALS, targetScore = 80, earnedStars = 0, isUnlocked = stars >= 60),
            AdventureLevel(8, "Koki Cilik Makanan Sehat", QuizCategory.FRUITS, targetScore = 80, earnedStars = 0, isUnlocked = stars >= 75),
            AdventureLevel(9, "Juara Pintar Serba Tahu", QuizCategory.RIDDLES, targetScore = 90, earnedStars = 0, isUnlocked = stars >= 90),
            AdventureLevel(10, "Bintang Emas Alnaura", QuizCategory.GOOD_HABITS, targetScore = 100, earnedStars = 0, isUnlocked = stars >= 110)
        )
    }

    fun getAllStickers(): List<StickerReward> {
        val unlocked = _unlockedStickerIds.value
        return listOf(
            StickerReward("stk_welcome", "Sahabat Alnaura", "Selamat datang bermain bersama Alnaura!", "👧", 0, isUnlocked = true),
            StickerReward("stk_first_star", "Bintang Cilik", "Mendapatkan bintang pertamamu!", "⭐", 5, isUnlocked = unlocked.contains("stk_first_star")),
            StickerReward("stk_animal_lover", "Pencinta Kucing", "Menjawab soal hewan dengan hebat!", "🐱", 20, isUnlocked = unlocked.contains("stk_animal_lover")),
            StickerReward("stk_apple", "Juara Buah Sehat", "Mengenal aneka buah kaya vitamin!", "🍎", 35, isUnlocked = unlocked.contains("stk_apple")),
            StickerReward("stk_rocket", "Roket Luar Angkasa", "Kecepatan menjawab luar biasa!", "🚀", 50, isUnlocked = unlocked.contains("stk_rocket")),
            StickerReward("stk_crown", "Mahkota Pintar", "Anak hebat, cerdas dan rajin belajar!", "👑", 70, isUnlocked = unlocked.contains("stk_crown")),
            StickerReward("stk_rainbow", "Pelangi Ajaib", "Berhasil menaklukkan banyak kategori kuis!", "🌈", 90, isUnlocked = unlocked.contains("stk_rainbow")),
            StickerReward("stk_trophy", "Piala Emas Super", "Juara Utama Kuis Tanya Jawab Alnaura!", "🏆", 120, isUnlocked = unlocked.contains("stk_trophy")),
            StickerReward("stk_icecream", "Es Krim Ceria", "Hadiah manis untuk anak yang gigih!", "🍦", 150, isUnlocked = unlocked.contains("stk_icecream"))
        )
    }

    private fun getDefaultCustomQuestions(): List<Question> {
        return listOf(
            Question(
                id = "custom_demo_1",
                category = QuizCategory.CUSTOM,
                questionText = "Siapa anak yang paling cantik, pintar, dan berhati baik?",
                emojiHint = "👧",
                options = listOf(
                    QuizOption("opt_1", "Alnaura yang Cantik", "💖", isCorrect = true),
                    QuizOption("opt_2", "Putri Tidur", "😴", isCorrect = false),
                    QuizOption("opt_3", "Boneka Beruang", "🧸", isCorrect = false),
                    QuizOption("opt_4", "Kelinci Lucu", "🐰", isCorrect = false)
                ),
                funFact = "Pintar sekali! Alnaura yang cantik adalah anak yang paling hebat dan membanggakan!",
                hintText = "Ada di judul game spesial ini lho!",
                isCustom = true
            ),
            Question(
                id = "custom_demo_2",
                category = QuizCategory.CUSTOM,
                questionText = "Apa yang harus kita lakukan sebelum makan bersama keluarga?",
                emojiHint = "🧼",
                options = listOf(
                    QuizOption("opt_1", "Cuci tangan dan berdoa", "🙏", isCorrect = true),
                    QuizOption("opt_2", "Langsung tidur", "😴", isCorrect = false),
                    QuizOption("opt_3", "Bermain tanah", "🌱", isCorrect = false),
                    QuizOption("opt_4", "Menyiram tanaman", "💧", isCorrect = false)
                ),
                funFact = "Mencuci tangan dengan sabun membunuh kuman, dan berdoa mensyukuri rezeki!",
                hintText = "Supaya tangan bersih dari kuman.",
                isCustom = true
            )
        )
    }

    private fun getBuiltInQuestions(): List<Question> {
        return listOf(
            // --- KATEGORI 1: DUNIA HEWAN (ANIMALS) ---
            Question(
                id = "q_anim_1",
                category = QuizCategory.ANIMALS,
                questionText = "Hewan apa yang bersuara 'Meong... Meong...' dan suka minum susu?",
                emojiHint = "🐾",
                options = listOf(
                    QuizOption("opt_1", "Kucing", "🐱", isCorrect = true),
                    QuizOption("opt_2", "Anjing", "🐶", isCorrect = false),
                    QuizOption("opt_3", "Bebek", "🦆", isCorrect = false),
                    QuizOption("opt_4", "Kambing", "🐐", isCorrect = false)
                ),
                funFact = "Kucing memiliki kumis yang membantunya merasakan ruangan sempit dan berburu!",
                hintText = "Hewan berbulu lembut yang suka dielus."
            ),
            Question(
                id = "q_anim_2",
                category = QuizCategory.ANIMALS,
                questionText = "Hewan apa yang memiliki belalai panjang dan telinga yang sangat besar?",
                emojiHint = "🐘",
                options = listOf(
                    QuizOption("opt_1", "Gajah", "🐘", isCorrect = true),
                    QuizOption("opt_2", "Jerapah", "🦒", isCorrect = false),
                    QuizOption("opt_3", "Singa", "🦁", isCorrect = false),
                    QuizOption("opt_4", "Kuda", "🐴", isCorrect = false)
                ),
                funFact = "Belalai gajah sangat kuat dan bisa digunakan untuk minum, mandi, serta menyapa temannya!",
                hintText = "Badannya sangat besar dan berwarna abu-abu."
            ),
            Question(
                id = "q_anim_3",
                category = QuizCategory.ANIMALS,
                questionText = "Hewan apa yang lehernya paling panjang dan suka makan daun di pohon tinggi?",
                emojiHint = "🦒",
                options = listOf(
                    QuizOption("opt_1", "Jerapah", "🦒", isCorrect = true),
                    QuizOption("opt_2", "Zebra", "🦓", isCorrect = false),
                    QuizOption("opt_3", "Kancil", "🦌", isCorrect = false),
                    QuizOption("opt_4", "Kelinci", "🐰", isCorrect = false)
                ),
                funFact = "Jerapah adalah hewan tertinggi di dunia, tingginya bisa mencapai 5 meter lebih!",
                hintText = "Lehernya panjang menjulang ke atas pohon."
            ),
            Question(
                id = "q_anim_4",
                category = QuizCategory.ANIMALS,
                questionText = "Di manakah ikan bernapas dan hidup?",
                emojiHint = "🐠",
                options = listOf(
                    QuizOption("opt_1", "Di dalam air", "🌊", isCorrect = true),
                    QuizOption("opt_2", "Di atas pohon", "🌳", isCorrect = false),
                    QuizOption("opt_3", "Di awan", "☁️", isCorrect = false),
                    QuizOption("opt_4", "Di bawah pasir kering", "🏜️", isCorrect = false)
                ),
                funFact = "Ikan bernapas menggunakan insang khusus untuk menyerap oksigen di dalam air!",
                hintText = "Tempat yang basah dan berombak."
            ),
            Question(
                id = "q_anim_5",
                category = QuizCategory.ANIMALS,
                questionText = "Hewan mungil apa yang suka mengisap nektar bunga dan menghasilkan madu manis?",
                emojiHint = "🐝",
                options = listOf(
                    QuizOption("opt_1", "Lebah", "🐝", isCorrect = true),
                    QuizOption("opt_2", "Semut", "🐜", isCorrect = false),
                    QuizOption("opt_3", "Kumbang", "🪲", isCorrect = false),
                    QuizOption("opt_4", "Nyamuk", "🦟", isCorrect = false)
                ),
                funFact = "Lebah bekerja sama dalam sarang dan membuat madu lezat yang menyehatkan tubuh!",
                hintText = "Warnanya belang kuning hitam dan bersuara 'Bzzz'."
            ),
            Question(
                id = "q_anim_6",
                category = QuizCategory.ANIMALS,
                questionText = "Hewan apa yang jalannya melompat-lompat dan punya kantung di perutnya?",
                emojiHint = "🦘",
                options = listOf(
                    QuizOption("opt_1", "Kanguru", "🦘", isCorrect = true),
                    QuizOption("opt_2", "Katak", "🐸", isCorrect = false),
                    QuizOption("opt_3", "Monyet", "🐒", isCorrect = false),
                    QuizOption("opt_4", "Panda", "🐼", isCorrect = false)
                ),
                funFact = "Kantung kanguru digunakan untuk membawa dan menyusui bayinya yang disebut joey!",
                hintText = "Berasal dari Australia dan suka melompat jauh."
            ),

            // --- KATEGORI 2: BUAH & SAYUR (FRUITS) ---
            Question(
                id = "q_fruit_1",
                category = QuizCategory.FRUITS,
                questionText = "Buah apa yang kulitnya berwarna kuning, melengkung, dan sangat disukai monyet?",
                emojiHint = "🍌",
                options = listOf(
                    QuizOption("opt_1", "Pisang", "🍌", isCorrect = true),
                    QuizOption("opt_2", "Semangka", "🍉", isCorrect = false),
                    QuizOption("opt_3", "Jeruk", "🍊", isCorrect = false),
                    QuizOption("opt_4", "Anggur", "🍇", isCorrect = false)
                ),
                funFact = "Pisang kaya akan kalium dan energi yang membuat kita kuat bermain dan berolahraga!",
                hintText = "Kulitnya mudah dikupas dan rasanya manis lembut."
            ),
            Question(
                id = "q_fruit_2",
                category = QuizCategory.FRUITS,
                questionText = "Buah apel umumnya berwarna apa saat sudah matang dan manis?",
                emojiHint = "🍎",
                options = listOf(
                    QuizOption("opt_1", "Merah cerah", "🔴", isCorrect = true),
                    QuizOption("opt_2", "Biru tua", "🔵", isCorrect = false),
                    QuizOption("opt_3", "Ungu pekat", "🟣", isCorrect = false),
                    QuizOption("opt_4", "Hitam legam", "⚫", isCorrect = false)
                ),
                funFact = "Satu buah apel sehari menjaga tubuh tetap sehat dan bebas dari dokter!",
                hintText = "Warna cerah seperti warna bendera kita bagian atas."
            ),
            Question(
                id = "q_fruit_3",
                category = QuizCategory.FRUITS,
                questionText = "Sayuran berwarna oranye apa yang sangat baik untuk kesehatan mata kita?",
                emojiHint = "🥕",
                options = listOf(
                    QuizOption("opt_1", "Wortel", "🥕", isCorrect = true),
                    QuizOption("opt_2", "Bayam", "🥬", isCorrect = false),
                    QuizOption("opt_3", "Terong", "🍆", isCorrect = false),
                    QuizOption("opt_4", "Jagung", "🌽", isCorrect = false)
                ),
                funFact = "Wortel kaya Vitamin A dan Beta-Karoten yang membuat penglihatan mata kita jernih dan tajam!",
                hintText = "Makanan favorit kelinci yang berbentuk kerucut."
            ),
            Question(
                id = "q_fruit_4",
                category = QuizCategory.FRUITS,
                questionText = "Buah semangka bagian dalamnya berwarna merah, rasanya segar dan banyak...",
                emojiHint = "🍉",
                options = listOf(
                    QuizOption("opt_1", "Air yang segar", "💧", isCorrect = true),
                    QuizOption("opt_2", "Minyak goreng", "🛢️", isCorrect = false),
                    QuizOption("opt_3", "Tepung terigu", "🌾", isCorrect = false),
                    QuizOption("opt_4", "Pasir pantai", "🏖️", isCorrect = false)
                ),
                funFact = "Sekitar 92% buah semangka adalah air, sangat cocok dimakan saat cuaca panas!",
                hintText = "Bikin tenggorokan segar saat haus."
            ),

            // --- KATEGORI 3: WARNA, BENTUK & ANGKA (COLORS_SHAPES) ---
            Question(
                id = "q_shape_1",
                category = QuizCategory.COLORS_SHAPES,
                questionText = "Benda seperti bola kasti dan matahari memiliki bentuk...",
                emojiHint = "⚽",
                options = listOf(
                    QuizOption("opt_1", "Lingkaran (Bulat)", "⭕", isCorrect = true),
                    QuizOption("opt_2", "Kotak Persegi", "⬛", isCorrect = false),
                    QuizOption("opt_3", "Segitiga", "🔺", isCorrect = false),
                    QuizOption("opt_4", "Bintang", "⭐", isCorrect = false)
                ),
                funFact = "Bentuk bulat membuat bola bisa menggelinding dengan mudah di tanah!",
                hintText = "Tidak punya sudut lancip, bundar berkeliling."
            ),
            Question(
                id = "q_shape_2",
                category = QuizCategory.COLORS_SHAPES,
                questionText = "Berapakah jumlah jari di kedua tangan kita jika digabungkan?",
                emojiHint = "🖐️",
                options = listOf(
                    QuizOption("opt_1", "10 Jari", "🔟", isCorrect = true),
                    QuizOption("opt_2", "5 Jari", "🖐️", isCorrect = false),
                    QuizOption("opt_3", "8 Jari", "🎱", isCorrect = false),
                    QuizOption("opt_4", "20 Jari", "👐", isCorrect = false)
                ),
                funFact = "Tangan kiri punya 5 jari dan tangan kanan punya 5 jari, totalnya 5 + 5 = 10!",
                hintText = "Hitung jari tangan kanan dan kirimu bersama-sama."
            ),
            Question(
                id = "q_shape_3",
                category = QuizCategory.COLORS_SHAPES,
                questionText = "Jika warna Kuning dicampur dengan warna Biru, akan menjadi warna apa?",
                emojiHint = "🎨",
                options = listOf(
                    QuizOption("opt_1", "Hijau Daun", "🟢", isCorrect = true),
                    QuizOption("opt_2", "Merah Muda", "🌸", isCorrect = false),
                    QuizOption("opt_3", "Cokelat Tua", "🟤", isCorrect = false),
                    QuizOption("opt_4", "Hitam", "⚫", isCorrect = false)
                ),
                funFact = "Kuning dan Biru adalah warna primer yang jika digabung menghasilkan warna sekunder hijau!",
                hintText = "Warna rumput dan daun pohon."
            ),
            Question(
                id = "q_shape_4",
                category = QuizCategory.COLORS_SHAPES,
                questionText = "Berapa banyak sisi yang dimiliki oleh bentuk Segitiga?",
                emojiHint = "🔺",
                options = listOf(
                    QuizOption("opt_1", "3 Sisi", "3️⃣", isCorrect = true),
                    QuizOption("opt_2", "4 Sisi", "4️⃣", isCorrect = false),
                    QuizOption("opt_3", "2 Sisi", "2️⃣", isCorrect = false),
                    QuizOption("opt_4", "5 Sisi", "5️⃣", isCorrect = false)
                ),
                funFact = "Potongan pizza atau atap rumah tradisional sering berbentuk segitiga dengan 3 sisi!",
                hintText = "Sesuai namanya: 'SEGI - TIGA'."
            ),

            // --- KATEGORI 4: BENDA & KENDARAAN (VEHICLES) ---
            Question(
                id = "q_veh_1",
                category = QuizCategory.VEHICLES,
                questionText = "Kendaraan panjang yang berjalan di atas rel besi dan bersuara 'Tut tut gujes gujes' adalah...",
                emojiHint = "🚂",
                options = listOf(
                    QuizOption("opt_1", "Kereta Api", "🚆", isCorrect = true),
                    QuizOption("opt_2", "Bus Kota", "🚌", isCorrect = false),
                    QuizOption("opt_3", "Perahu Karet", "🚣", isCorrect = false),
                    QuizOption("opt_4", "Sepeda Ontel", "🚲", isCorrect = false)
                ),
                funFact = "Kereta api dikemudikan oleh masinis dan berhenti di stasiun!",
                hintText = "Punya gerbong panjang dan jalurnya di rel besi."
            ),
            Question(
                id = "q_veh_2",
                category = QuizCategory.VEHICLES,
                questionText = "Kendaraan yang punya sayap besar dan bisa terbang tinggi di awan adalah...",
                emojiHint = "✈️",
                options = listOf(
                    QuizOption("opt_1", "Pesawat Terbang", "✈️", isCorrect = true),
                    QuizOption("opt_2", "Kapal Selam", "🛳️", isCorrect = false),
                    QuizOption("opt_3", "Mobil Balap", "🏎️", isCorrect = false),
                    QuizOption("opt_4", "Becak", "🛺", isCorrect = false)
                ),
                funFact = "Pesawat terbang dikemudikan oleh pilot dan lepas landas dari bandara udara!",
                hintText = "Terbang di langit biru bersama burung."
            ),
            Question(
                id = "q_veh_3",
                category = QuizCategory.VEHICLES,
                questionText = "Mobil merah yang membawa sirine 'Nguung-nguung' dan selang air besar untuk padamkan api adalah...",
                emojiHint = "🚒",
                options = listOf(
                    QuizOption("opt_1", "Mobil Pemadam Kebakaran", "🚒", isCorrect = true),
                    QuizOption("opt_2", "Truk Sampah", "🚛", isCorrect = false),
                    QuizOption("opt_3", "Mobil Es Krim", "🚚", isCorrect = false),
                    QuizOption("opt_4", "Taksi Kuning", "🚕", isCorrect = false)
                ),
                funFact = "Petugas pemadam kebakaran adalah pahlawan pemberani yang selalu siap menolong saat darurat!",
                hintText = "Petugasnya memakai helm pelindung dan menyemprotkan air."
            ),

            // --- KATEGORI 5: TATA KRAMA & KEBIASAAN BAIK (GOOD_HABITS) ---
            Question(
                id = "q_habit_1",
                category = QuizCategory.GOOD_HABITS,
                questionText = "Saat kita diberi hadiah atau bantuan oleh teman, kata ajaib apa yang harus kita ucapkan?",
                emojiHint = "🎁",
                options = listOf(
                    QuizOption("opt_1", "Terima Kasih", "💖", isCorrect = true),
                    QuizOption("opt_2", "Diam saja", "🤐", isCorrect = false),
                    QuizOption("opt_3", "Marah-marah", "😡", isCorrect = false),
                    QuizOption("opt_4", "Kabur", "🏃", isCorrect = false)
                ),
                funFact = "Mengucapkan 'Terima Kasih' membuat orang lain merasa dihargai dan bahagia!",
                hintText = "Ungkapan rasa syukur dan kesopanan."
            ),
            Question(
                id = "q_habit_2",
                category = QuizCategory.GOOD_HABITS,
                questionText = "Kapan waktu yang paling tepat untuk menggosok gigi dengan sikat dan odol?",
                emojiHint = "🪥",
                options = listOf(
                    QuizOption("opt_1", "Pagi setelah makan & malam sebelum tidur", "✨", isCorrect = true),
                    QuizOption("opt_2", "Sebulan sekali saja", "🗓️", isCorrect = false),
                    QuizOption("opt_3", "Hanya saat mau bermain bola", "⚽", isCorrect = false),
                    QuizOption("opt_4", "Tidak pernah sikat gigi", "❌", isCorrect = false)
                ),
                funFact = "Sikat gigi minimal 2 kali sehari mencegah kuman merusak gigi dan membuat napas kita segar!",
                hintText = "Supaya kuman makanan tidak membuat gigi berlubang."
            ),
            Question(
                id = "q_habit_3",
                category = QuizCategory.GOOD_HABITS,
                questionText = "Jika kita tidak sengaja menabrak teman atau berbuat salah, kita harus mengucapkan...",
                emojiHint = "🤝",
                options = listOf(
                    QuizOption("opt_1", "Minta Maaf", "🙏", isCorrect = true),
                    QuizOption("opt_2", "Tertawa kencang", "😆", isCorrect = false),
                    QuizOption("opt_3", "Menyalahkan orang lain", "👉", isCorrect = false),
                    QuizOption("opt_4", "Pura-pura tidak tahu", "🙈", isCorrect = false)
                ),
                funFact = "Anak yang berani meminta maaf dan memaafkan adalah anak yang hebat berhati mulia!",
                hintText = "Tanda kita menyesal dan ingin berteman baik kembali."
            ),

            // --- KATEGORI 6: TEKA-TEKI CERIA (RIDDLES) ---
            Question(
                id = "q_rid_1",
                category = QuizCategory.RIDDLES,
                questionText = "Siapakah aku? Badanku bulat, bersinar terang di siang hari, dan menghangatkan seluruh bumi?",
                emojiHint = "☀️",
                options = listOf(
                    QuizOption("opt_1", "Matahari", "☀️", isCorrect = true),
                    QuizOption("opt_2", "Bulan Purnama", "🌕", isCorrect = false),
                    QuizOption("opt_3", "Lampu Kamar", "💡", isCorrect = false),
                    QuizOption("opt_4", "Api Unggun", "🔥", isCorrect = false)
                ),
                funFact = "Matahari adalah bintang raksasa yang memberikan cahaya dan kehidupan untuk semua makhluk bumi!",
                hintText = "Terbit di timur pada pagi hari dan terbenam di barat."
            ),
            Question(
                id = "q_rid_2",
                category = QuizCategory.RIDDLES,
                questionText = "Siapakah aku? Aku punya jarum panjang dan pendek, berbunyi 'Tiktak-tiktak', dan menunjukkan waktu?",
                emojiHint = "⏰",
                options = listOf(
                    QuizOption("opt_1", "Jam Dinding", "⏰", isCorrect = true),
                    QuizOption("opt_2", "Radio Suara", "📻", isCorrect = false),
                    QuizOption("opt_3", "Kipas Angin", "🌀", isCorrect = false),
                    QuizOption("opt_4", "Kulkas Es", "🧊", isCorrect = false)
                ),
                funFact = "Jam membantu kita mengatur jadwal belajar, makan, bermain, dan tidur tepat waktu!",
                hintText = "Angkanya 1 sampai 12 dan terus berputar."
            ),
            Question(
                id = "q_rid_3",
                category = QuizCategory.RIDDLES,
                questionText = "Siapakah aku? Aku dibuka saat hujan turun agar kamu tidak basah kehujanan?",
                emojiHint = "☔",
                options = listOf(
                    QuizOption("opt_1", "Payung", "☂️", isCorrect = true),
                    QuizOption("opt_2", "Kacamata Hitam", "🕶️", isCorrect = false),
                    QuizOption("opt_3", "Sepatu Roda", "🛼", isCorrect = false),
                    QuizOption("opt_4", "Panci Dapur", "🍳", isCorrect = false)
                ),
                funFact = "Payung diciptakan lebih dari 4.000 tahun yang lalu untuk melindungi dari panas dan hujan!",
                hintText = "Bentuknya seperti tudung pelindung yang bisa dilipat."
            )
        )
    }
}
