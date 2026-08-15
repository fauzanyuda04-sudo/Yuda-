package com.example.speech

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Generates pleasant, melodious sound effects (chimes, celebratory fanfare, soft tones)
 * natively using synthesized sine waves for instant, lightweight, latency-free feedback.
 */
object SoundFxHelper {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun playCorrectChime() {
        scope.launch {
            // Cheerful ascending two-tone chime (E5 -> B5 -> E6)
            playTones(
                listOf(
                    Pair(659.25, 110), // E5
                    Pair(987.77, 110), // B5
                    Pair(1318.51, 240) // E6
                )
            )
        }
    }

    fun playIncorrectTone() {
        scope.launch {
            // Soft gentle descending tone (G4 -> E4)
            playTones(
                listOf(
                    Pair(392.00, 140), // G4
                    Pair(329.63, 220)  // E4
                ),
                volume = 0.45f
            )
        }
    }

    fun playCelebrationFanfare() {
        scope.launch {
            // Joyful victory fanfare (C5 -> E5 -> G5 -> C6)
            playTones(
                listOf(
                    Pair(523.25, 120),
                    Pair(659.25, 120),
                    Pair(783.99, 140),
                    Pair(1046.50, 320)
                ),
                volume = 0.6f
            )
        }
    }

    fun playStarPop() {
        scope.launch {
            playTones(
                listOf(
                    Pair(880.00, 60),
                    Pair(1174.66, 120)
                ),
                volume = 0.4f
            )
        }
    }

    private fun playTones(tones: List<Pair<Double, Int>>, volume: Float = 0.55f) {
        val sampleRate = 44100
        val totalMs = tones.sumOf { it.second }
        val totalSamples = (sampleRate * (totalMs / 1000.0)).toInt()
        val generatedSnd = ShortArray(totalSamples)

        var sampleIndex = 0
        for ((freqHz, durationMs) in tones) {
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            for (i in 0 until numSamples) {
                if (sampleIndex >= totalSamples) break
                // Envelope (smooth attack and decay to prevent clicking)
                val attackSamples = (numSamples * 0.1).toInt().coerceAtLeast(1)
                val decaySamples = (numSamples * 0.3).toInt().coerceAtLeast(1)
                val envelope = when {
                    i < attackSamples -> i.toFloat() / attackSamples
                    i > numSamples - decaySamples -> (numSamples - i).toFloat() / decaySamples
                    else -> 1.0f
                }
                val angle = 2.0 * Math.PI * i / (sampleRate / freqHz)
                val sampleValue = (sin(angle) * envelope * volume * Short.MAX_VALUE).toInt()
                generatedSnd[sampleIndex++] = sampleValue.toShort()
            }
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(generatedSnd.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(generatedSnd, 0, generatedSnd.size)
            audioTrack.play()
            Thread.sleep(totalMs.toLong() + 50)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {
            // Ignore audio track exceptions on background exit
        }
    }
}
