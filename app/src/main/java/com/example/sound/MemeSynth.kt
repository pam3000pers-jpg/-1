package com.example.sound

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

object MemeSynth {
    private const val SAMPLE_RATE = 22050

    // Play a procedurally generated retro sound effect in a background thread
    suspend fun playSound(type: String) = withContext(Dispatchers.IO) {
        try {
            val buffer = when (type.lowercase()) {
                "evil_laugh" -> generateEvilLaugh()
                "rampage_phonk" -> generateRampagePhonk()
                else -> generateEvilLaugh()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(SAMPLE_RATE)
                        .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            
            // Release the AudioTrack after playback finishes
            val durationMs = (buffer.size * 1000) / SAMPLE_RATE
            kotlinx.coroutines.delay(durationMs + 100L)
            audioTrack.stop()
            audioTrack.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun generateEvilLaugh(): ByteArray {
        val durationMs = 1500
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = Math.max(0.0, sin(t * Math.PI * 6)) // Ah-ha-ha-ha rhythm
            val freq = 120.0 - (50.0 * (i.toDouble() / numSamples))
            val noise = (Math.random() - 0.5) * 40.0
            val value = (sin(2 * Math.PI * freq * t) * 60 + noise) * env
            buffer[i] = (value + 128).toInt().coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateRampagePhonk(): ByteArray {
        val durationMs = 3000
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ByteArray(numSamples)
        
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            
            // Kick drum (heavy distorted bass every 0.5 seconds)
            val beatTime = t % 0.5
            val kickEnv = Math.exp(-beatTime * 15.0)
            val kickFreq = 150.0 * Math.exp(-beatTime * 30.0) + 40.0
            val kick = sin(2 * Math.PI * kickFreq * beatTime) * 120 * kickEnv
            
            // Cowbell / synth melody (Phonk style)
            val synthEnv = Math.exp(-(t % 0.25) * 8.0)
            val noteStep = ((t * 4).toInt() % 4)
            val synthFreq = when(noteStep) {
                0 -> 440.0
                1 -> 523.25
                2 -> 659.25
                else -> 587.33
            }
            val synth = sin(2 * Math.PI * synthFreq * t) * 60 * synthEnv
            
            val value = (kick + synth).coerceIn(-127.0, 127.0)
            buffer[i] = (value + 128).toInt().coerceIn(0, 255).toByte()
        }
        return buffer
    }
}
