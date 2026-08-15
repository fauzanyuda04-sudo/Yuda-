package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.QuizRepository
import com.example.model.AdventureLevel
import com.example.model.AlnauraEmotion
import com.example.model.AppScreen
import com.example.model.Question
import com.example.model.QuizCategory
import com.example.model.QuizOption
import com.example.model.StickerReward
import com.example.speech.SoundFxHelper
import com.example.speech.SpeechHelper
import com.example.speech.VoicePreset
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AlnauraUiState(
    val currentScreen: AppScreen = AppScreen.HOME,
    val selectedCategory: QuizCategory = QuizCategory.ANIMALS,
    val activeLevel: AdventureLevel? = null,
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionId: String? = null,
    val isAnswerRevealed: Boolean = false,
    val isAnswerCorrect: Boolean? = null,
    val score: Int = 0,
    val streak: Int = 0,
    val correctAnswersCount: Int = 0,
    val starsEarnedThisRound: Int = 0,
    val alnauraSpeechText: String = "Halo! Ini game kuis spesial hanya untuk Alnaura yang cantik dan pintar! Ayo kita mulai bermain!",
    val alnauraEmotion: AlnauraEmotion = AlnauraEmotion.HAPPY,
    val isVoiceEnabled: Boolean = true,
    val showConfetti: Boolean = false,
    val showHintDialog: Boolean = false,
    val showAddQuestionDialog: Boolean = false,
    val showVoiceSettingsDialog: Boolean = false,
    val voicePreset: VoicePreset = VoicePreset.NATURAL_HUMAN,
    val voicePitch: Float = 1.00f,
    val voiceRate: Float = 0.98f,
    val totalStars: Int = 0,
    val unlockedStickers: List<StickerReward> = emptyList(),
    val adventureLevels: List<AdventureLevel> = emptyList(),
    val customQuestions: List<Question> = emptyList()
) {
    val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex)

    val totalQuestions: Int
        get() = questions.size

    val progressPercent: Float
        get() = if (totalQuestions == 0) 0f else (currentQuestionIndex + 1).toFloat() / totalQuestions.toFloat()
}

class AlnauraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QuizRepository(application)
    private val speechHelper = SpeechHelper(application)

    private val _uiState = MutableStateFlow(AlnauraUiState())
    val uiState: StateFlow<AlnauraUiState> = _uiState.asStateFlow()

    init {
        refreshStateFromRepo()
        // Welcome greeting with natural human TTS
        viewModelScope.launch {
            delay(500)
            speak("Halo! Ini game kuis spesial hanya untuk Alnaura yang cantik dan pintar! Ayo kita mulai bermain!")
        }
    }

    private fun refreshStateFromRepo() {
        val totalStars = repository.totalStars.value
        val stickers = repository.getAllStickers()
        val levels = repository.getAdventureLevels()
        val customList = repository.customQuestions.value

        _uiState.update {
            it.copy(
                totalStars = totalStars,
                unlockedStickers = stickers,
                adventureLevels = levels,
                customQuestions = customList,
                voicePreset = speechHelper.currentPreset,
                voicePitch = speechHelper.currentPitch,
                voiceRate = speechHelper.currentRate
            )
        }
    }

    fun startQuizByCategory(category: QuizCategory) {
        val list = repository.getQuestionsByCategory(category).shuffled()
        val questionsToPlay = if (list.size > 5) list.take(5) else list

        _uiState.update {
            it.copy(
                currentScreen = AppScreen.PLAYING,
                selectedCategory = category,
                activeLevel = null,
                questions = questionsToPlay,
                currentQuestionIndex = 0,
                selectedOptionId = null,
                isAnswerRevealed = false,
                isAnswerCorrect = null,
                score = 0,
                streak = 0,
                correctAnswersCount = 0,
                starsEarnedThisRound = 0,
                alnauraSpeechText = "Pertanyaan pertama di kategori ${category.title}! Ayo Alnaura yang cantik, kamu pasti bisa!",
                alnauraEmotion = AlnauraEmotion.HAPPY,
                showConfetti = false
            )
        }
        narrateCurrentQuestion()
    }

    fun startAdventureLevel(level: AdventureLevel) {
        val list = repository.getQuestionsByCategory(level.category).shuffled()
        val questionsToPlay = if (list.size > 5) list.take(5) else list

        _uiState.update {
            it.copy(
                currentScreen = AppScreen.PLAYING,
                selectedCategory = level.category,
                activeLevel = level,
                questions = questionsToPlay,
                currentQuestionIndex = 0,
                selectedOptionId = null,
                isAnswerRevealed = false,
                isAnswerCorrect = null,
                score = 0,
                streak = 0,
                correctAnswersCount = 0,
                starsEarnedThisRound = 0,
                alnauraSpeechText = "Level ${level.levelNumber}: ${level.title}! Kumpulkan bintang emasnya Alnaura yang cantik!",
                alnauraEmotion = AlnauraEmotion.HAPPY,
                showConfetti = false
            )
        }
        narrateCurrentQuestion()
    }

    fun selectOption(option: QuizOption) {
        val state = _uiState.value
        if (state.isAnswerRevealed) return // already answered

        val isCorrect = option.isCorrect
        val currentQ = state.currentQuestion ?: return

        val newStreak = if (isCorrect) state.streak + 1 else 0
        val pointsToAdd = if (isCorrect) 10 + (newStreak * 2) else 0
        val newScore = state.score + pointsToAdd
        val newCorrectCount = if (isCorrect) state.correctAnswersCount + 1 else state.correctAnswersCount

        // Trigger pleasant audio feedback sound effect immediately
        if (isCorrect) {
            SoundFxHelper.playCorrectChime()
        } else {
            SoundFxHelper.playIncorrectTone()
        }

        val feedbackText = if (isCorrect) {
            val compliments = listOf(
                "Yaaay, hebat sekali! Alnaura yang cantik benar!",
                "Luar biasa pintar! Alnaura yang cantik memang cerdas!",
                "Wah, jempolan! Alnaura yang cantik hebat banget!",
                "Betul banget! Alnaura yang cantik dan manis juara!"
            )
            compliments.random() + " " + currentQ.funFact
        } else {
            val encouragement = listOf(
                "Wah hampir tepat! Semangat ya Alnaura yang cantik!",
                "Tidak apa-apa, ayo coba lagi di soal berikutnya Alnaura yang cantik!",
                "Semangat terus Alnaura yang cantik! Belajar itu asyik!"
            )
            encouragement.random() + " Jawaban yang benar adalah " + (currentQ.options.find { it.isCorrect }?.text ?: "") + ". " + currentQ.funFact
        }

        _uiState.update {
            it.copy(
                selectedOptionId = option.id,
                isAnswerRevealed = true,
                isAnswerCorrect = isCorrect,
                score = newScore,
                streak = newStreak,
                correctAnswersCount = newCorrectCount,
                alnauraSpeechText = feedbackText,
                alnauraEmotion = if (isCorrect) AlnauraEmotion.CHEERING else AlnauraEmotion.OOPS,
                showConfetti = isCorrect
            )
        }

        speak(feedbackText)
    }

    fun nextQuestion() {
        val state = _uiState.value
        if (state.currentQuestionIndex + 1 < state.totalQuestions) {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    selectedOptionId = null,
                    isAnswerRevealed = false,
                    isAnswerCorrect = null,
                    alnauraEmotion = AlnauraEmotion.THINKING,
                    showConfetti = false
                )
            }
            narrateCurrentQuestion()
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        val state = _uiState.value
        val total = state.totalQuestions
        val correct = state.correctAnswersCount
        val ratio = if (total > 0) correct.toFloat() / total.toFloat() else 0f

        val starsEarned = when {
            ratio >= 0.8f -> 3
            ratio >= 0.5f -> 2
            ratio > 0.0f -> 1
            else -> 0
        }

        if (starsEarned >= 2) {
            SoundFxHelper.playCelebrationFanfare()
        }

        val celebrationSpeech = when (starsEarned) {
            3 -> "Horeeee! Luar biasa! Alnaura yang cantik berhasil dapat 3 Bintang Emas Penuh! Alnaura yang cantik memang super juara!"
            2 -> "Hebat sekali! Alnaura yang cantik berhasil mendapatkan 2 Bintang Emas! Sedikit lagi sempurna!"
            1 -> "Bagus sekali! Alnaura yang cantik dapat 1 Bintang Emas! Terus belajar dengan ceria ya!"
            else -> "Terima kasih sudah bermain dengan ceria, Alnaura yang cantik! Ayo kita coba lagi bersama!"
        }

        repository.addStars(starsEarned * 5 + state.score / 10)
        refreshStateFromRepo()

        _uiState.update {
            it.copy(
                currentScreen = AppScreen.RESULT,
                starsEarnedThisRound = starsEarned,
                alnauraSpeechText = celebrationSpeech,
                alnauraEmotion = AlnauraEmotion.CELEBRATING,
                showConfetti = starsEarned >= 2
            )
        }

        speak(celebrationSpeech)
    }

    fun narrateCurrentQuestion() {
        val currentQ = _uiState.value.currentQuestion ?: return
        val speech = currentQ.questionText
        _uiState.update {
            it.copy(
                alnauraSpeechText = speech,
                alnauraEmotion = AlnauraEmotion.THINKING
            )
        }
        speak(speech)
    }

    fun replayCurrentVoice() {
        val text = _uiState.value.alnauraSpeechText
        speak(text)
    }

    fun toggleVoice() {
        val newVal = !_uiState.value.isVoiceEnabled
        speechHelper.isVoiceEnabled = newVal
        _uiState.update { it.copy(isVoiceEnabled = newVal) }
        if (!newVal) {
            speechHelper.stop()
        } else {
            speak("Suara Alnaura yang cantik aktif kembali!")
        }
    }

    fun setVoiceSettingsDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showVoiceSettingsDialog = visible) }
    }

    fun setVoicePreset(preset: VoicePreset) {
        speechHelper.applyPreset(preset)
        _uiState.update {
            it.copy(
                voicePreset = preset,
                voicePitch = preset.pitch,
                voiceRate = preset.rate
            )
        }
        testVoice(preset.pitch, preset.rate)
    }

    fun setCustomPitchAndRate(pitch: Float, rate: Float) {
        speechHelper.setCustomPitchAndRate(pitch, rate)
        _uiState.update {
            it.copy(
                voicePitch = speechHelper.currentPitch,
                voiceRate = speechHelper.currentRate
            )
        }
    }

    fun testVoice(pitch: Float, rate: Float) {
        speechHelper.setCustomPitchAndRate(pitch, rate)
        val testSpeech = "Halo! Aku Alnaura yang cantik dan pintar! Suara aku sekarang terdengar jernih, merdu, dan alami seperti manusia asli!"
        _uiState.update { it.copy(alnauraSpeechText = testSpeech, alnauraEmotion = AlnauraEmotion.CHEERING) }
        speechHelper.speak(testSpeech)
    }

    fun speak(text: String) {
        if (_uiState.value.isVoiceEnabled) {
            speechHelper.speak(text)
        }
    }

    fun toggleHintDialog(show: Boolean) {
        _uiState.update { it.copy(showHintDialog = show) }
        if (show) {
            val hint = _uiState.value.currentQuestion?.hintText ?: "Pikirkan baik-baik ya!"
            speak("Petunjuk dari Alnaura yang cantik: $hint")
        }
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update {
            it.copy(
                currentScreen = screen,
                showConfetti = false,
                alnauraEmotion = AlnauraEmotion.HAPPY
            )
        }
        if (screen == AppScreen.HOME) {
            val greet = "Halo lagi Alnaura yang cantik! Kategori seru apa yang ingin kita mainkan sekarang?"
            _uiState.update { it.copy(alnauraSpeechText = greet) }
            speak(greet)
        } else if (screen == AppScreen.STICKER_BOOK) {
            val greet = "Ini buku stiker koleksi Alnaura yang cantik! Kumpulkan lebih banyak bintang untuk membuka semua stiker!"
            _uiState.update { it.copy(alnauraSpeechText = greet) }
            speak(greet)
        }
    }

    fun setAddQuestionDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showAddQuestionDialog = visible) }
    }

    fun addCustomQuestion(
        questionText: String,
        correctAnswer: String,
        wrongAnswer1: String,
        wrongAnswer2: String,
        wrongAnswer3: String,
        funFact: String,
        emojiHint: String
    ) {
        repository.addCustomQuestion(
            questionText = questionText,
            correctAnswer = correctAnswer,
            wrongAnswer1 = wrongAnswer1,
            wrongAnswer2 = wrongAnswer2,
            wrongAnswer3 = wrongAnswer3,
            funFact = funFact,
            emojiHint = emojiHint
        )
        refreshStateFromRepo()
        SoundFxHelper.playStarPop()
        _uiState.update { it.copy(showAddQuestionDialog = false) }
        speak("Hore! Pertanyaan baru khusus Alnaura yang cantik berhasil disimpan! Sekarang kamu bisa memainkannya di Kuis Buatan Sendiri!")
    }

    fun deleteCustomQuestion(id: String) {
        repository.deleteCustomQuestion(id)
        refreshStateFromRepo()
    }

    override fun onCleared() {
        super.onCleared()
        speechHelper.shutdown()
    }
}
