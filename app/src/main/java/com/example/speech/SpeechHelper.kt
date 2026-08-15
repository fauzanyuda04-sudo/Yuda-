package com.example.speech

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import java.util.Locale
import java.util.regex.Pattern

enum class VoicePreset(
    val label: String,
    val subtitle: String,
    val pitch: Float,
    val rate: Float,
    val emoji: String
) {
    NATURAL_HUMAN(
        label = "Manusia Alami (Paling Bagus)",
        subtitle = "Suara jernih, natural & manusiawi",
        pitch = 1.00f,
        rate = 0.98f,
        emoji = "✨"
    ),
    ALNAURA_SWEET(
        label = "Alnaura Ceria",
        subtitle = "Nada manis, ramah & bersemangat",
        pitch = 1.06f,
        rate = 1.00f,
        emoji = "👧"
    ),
    GENTLE_STORYTELLER(
        label = "Kakak Ramah & Lembut",
        subtitle = "Artikulasi lembut & menenangkan",
        pitch = 0.96f,
        rate = 0.92f,
        emoji = "🌸"
    ),
    CLEAR_SLOW(
        label = "Jelas & Santai",
        subtitle = "Kecepatan lambat mudah dipahami",
        pitch = 1.00f,
        rate = 0.84f,
        emoji = "🎧"
    )
}

class SpeechHelper(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    var isVoiceEnabled: Boolean = true

    var currentPreset: VoicePreset = VoicePreset.NATURAL_HUMAN
        private set

    var currentPitch: Float = 1.00f
        private set

    var currentRate: Float = 0.98f
        private set

    private val emojiPattern: Pattern = Pattern.compile(
        "[\uD83C-\uDBFF\uDC00-\uDFFF]|[\u2600-\u27BF]|[\uFE00-\uFE0F]|[\u1F000-\u1FFFF]|⭐|🎉|💖|✨|🐾|🍎|🎨|🚗|👑|🌈|🍕|🍦|🎈|🚀|🐱|🐶|🐰|🦁|🐘|🐒|🐸|🦋|🐳|🐯|🦖|🌻|🌲|🪐|🌧️|🌋|🍓|🍌|🥕|🥛|🍫|🍚|🍉|🕌|🤲|✨|📖|❤️|🥇|🥈|🥉|🏆|🎁|💡|❓|✅|❌",
        Pattern.UNICODE_CASE
    )

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                setupBestNaturalVoice()
                applyVoiceSettings(currentPitch, currentRate)
                isInitialized = true
            } else {
                Log.e("SpeechHelper", "TTS Initialization failed with status $status")
            }
        }
    }

    private fun setupBestNaturalVoice() {
        val t = tts ?: return
        val indonesian = Locale("id", "ID")
        val altIndonesian = Locale("in", "ID")

        // First attempt setting language
        val result = t.setLanguage(indonesian)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            t.setLanguage(altIndonesian)
        }

        // Try to pick the most natural sounding Indonesian voice available from the engine
        try {
            val voices = t.voices
            if (!voices.isNullOrEmpty()) {
                val indonesianVoices = voices.filter { v ->
                    val lang = v.locale.language.lowercase()
                    lang == "id" || lang == "in" || v.locale.country.equals("ID", ignoreCase = true)
                }

                if (indonesianVoices.isNotEmpty()) {
                    // Priority 1: Natural / Wavenet / Network / High Quality Female voice
                    val bestVoice = indonesianVoices.firstOrNull { v ->
                        val name = v.name.lowercase()
                        (name.contains("network") || name.contains("female") || name.contains("wavenet") || name.contains("neural") || name.contains("natural")) &&
                                !v.isNetworkConnectionRequired
                    } ?: indonesianVoices.firstOrNull { v ->
                        v.quality >= Voice.QUALITY_HIGH
                    } ?: indonesianVoices.firstOrNull { v ->
                        v.name.lowercase().contains("female") || v.name.lowercase().contains("dfz")
                    } ?: indonesianVoices.first()

                    t.voice = bestVoice
                    Log.d("SpeechHelper", "Selected TTS voice: ${bestVoice.name}, quality: ${bestVoice.quality}")
                }
            }
        } catch (e: Exception) {
            Log.w("SpeechHelper", "Could not inspect voices list", e)
        }
    }

    fun applyPreset(preset: VoicePreset) {
        currentPreset = preset
        currentPitch = preset.pitch
        currentRate = preset.rate
        applyVoiceSettings(currentPitch, currentRate)
    }

    fun setCustomPitchAndRate(pitch: Float, rate: Float) {
        currentPitch = pitch.coerceIn(0.7f, 1.4f)
        currentRate = rate.coerceIn(0.6f, 1.4f)
        applyVoiceSettings(currentPitch, currentRate)
    }

    private fun applyVoiceSettings(pitch: Float, rate: Float) {
        try {
            tts?.setPitch(pitch)
            tts?.setSpeechRate(rate)
        } catch (e: Exception) {
            Log.e("SpeechHelper", "Error applying voice settings", e)
        }
    }

    /**
     * Cleans text to make it sound human and smooth without reading robotic Unicode emojis
     * or awkward abbreviations.
     */
    fun sanitizeForSpeech(raw: String): String {
        var clean = emojiPattern.matcher(raw).replaceAll(" ")
        clean = clean
            .replace("⭐", " bintang ")
            .replace("&", " dan ")
            .replace("No.", "Nomor ")
            .replace("kg", "kilogram")
            .replace("km", "kilometer")
            .replace("cm", "sentimeter")
            .replace("+", " tambah ")
            .replace("=", " sama dengan ")
            .replace("✨", "")
            .replace("💖", "")
            .replace("🎉", "")
            .replace("•", ",")
            .replace("  +", " ")
            .trim()

        return clean
    }

    fun speak(text: String, flush: Boolean = true) {
        if (!isVoiceEnabled || !isInitialized) return
        try {
            val naturalSpeechText = sanitizeForSpeech(text)
            if (naturalSpeechText.isBlank()) return

            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
            }

            val queueMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            tts?.speak(naturalSpeechText, queueMode, params, "AlnauraUtterance_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("SpeechHelper", "Error speaking text", e)
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("SpeechHelper", "Error stopping TTS", e)
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("SpeechHelper", "Error shutting down TTS", e)
        }
    }
}
