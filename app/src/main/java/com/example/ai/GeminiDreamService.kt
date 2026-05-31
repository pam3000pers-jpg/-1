package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiDreamService {
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Offline templates to ensure the user gets a hilarious experience even without an internet/api key.
    private val offlineProphecies = listOf(
        "Verily, the prophecy fortold a hero clad in Plaid pajama pants and a vibrant Yellow Safety Vest, carrying a shield of pure Trollface energy. The legends say he would stand with -10 physical defense, yet strike down the OVERKILL legions with a single smug smile.",
        "And so, he lay on the floor inside the Crimson Hall, sliding at velocities unseen by mortal men. The crowd gasped as the announcer proclaimed a RAMPAGE! The director wept—it was the ultimate stage victory.",
        "When the three elements align: the Trollface of -10, the Skull of OVERKILL, and the recumbent posture of RAMPAGE; then shall Andrey arise. The theatre hall shall echo with retro retro riffs, and the world will witness the final curtain call.",
        "They mocked his Plaid pants. They laughed at his yellow safety vest. But when the skeleton chorus sang their dark overtones, Andrey raised his hand and subtracted 10 from reality itself. Total tactical victory!"
    )

    private val offlineDirectorRants = listOf(
        "ACTING! Yes, this is pure acting. Look at him lying on the floor in absolute repose! That is not laziness, that is a physical manifestation of existensial RAMPAGE! Five out of five stars, absolute classic theatrical genius!",
        "Why is there a yellow safety vest on stage?! I asked for high-class drama and Andrey gives me high-visibility construction wear! Underneath he wears plaid sleeping gear! And the troll shell? Brilliant. It subverts everything. Pure avant-garde overkill.",
        "Look at the skulls in the back row! The choir has literally decayed into skeletons waiting for Andrey to complete his dramatic monologue on the floor. Beautiful symbolism about the passage of time and the theatrical process.",
        "A defense level of minus ten?! How do you even survive the opening scene with minus ten defense?! Unless... in this theater, physical harm is ignored, and only raw comedic trolling deal structural damage! Magnificent."
    )

    private val offlineFanTheories = listOf(
        "Theoretical analysis: The crimson curtains are a reference to Twin Peaks. Andrey represents the modern trickster god (Loki) entering the red room. The safety vest signifies that joke boundaries are safe but hazardous.",
        "Deep Lore: The boy on the floor isn't defeated. He is performing the 'Low Sweep' maneuver, a famous RPG tactic that deals 100x damage to skeletal ankles but reduces standing height to zero. It's a high-intelligence move.",
        "The red stamp seals on the velvet chairs indicate that Andrey has officially been certified by the Backstage Meme Committee as a Grade-A Drama Legend.",
        "Andrey is actually the mastermind directing the skeleton troupe. The Trollface is a mask to hide his intense concentration as he orchestrates the RAMPAGE rhythm on the stage."
    )

    suspend fun generateTheatricalContent(promptType: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        // Return quick fallback if API key is blank/default
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineResponse(promptType)
        }

        val prompt = when (promptType.lowercase()) {
            "prophecy" -> "Write a short (40-60 words), funny, dramatic ancient prophecy (in Russian or English, but Russian is preferred as the user prompted in Russian) about 'Andrei' (Андрей) based on a meme photo where a kid in a yellow safety vest and plaid pants holds a Trollface, a guy with a Trollface lies on stage with 'RAMPAGE' written over him, and a choir of girls sit in the background on stage with skeleton faces and 'OVERKILL' written over them. Keep the tone dramatic, like ancient prophecies or RPG bestiary entries."
            "director_rant" -> "Write a hilarious, brief (40-60 words) monologue of a furious, eccentric theater director (in Russian) reviewing Andrei's performance. Mention the yellow vest (-10), the guy sliding on the floor (RAMPAGE), and the skeleton choir in the seats (OVERKILL)."
            "fan_theory" -> "Write a brief, hilarious conspiracy theory (in Russian) analyzing the deep lore behind Andrei's Meme Theater. Mention the crimson velvet curtains, the red seals, and the trollface masks as signs of a secret stage society."
            else -> "Write a funny 2-sentence theater meme quote in Russian about Andrey, -10 trolling, and Rampage style."
        }

        try {
            val jsonRequest = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObject = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            val partObject = JSONObject().apply {
                                put("text", prompt)
                            }
                            put(partObject)
                        }
                        put("parts", partsArray)
                    }
                    put(contentObject)
                }
                put("contents", contentsArray)

                // Optional: set system instruction and temperature
                val configObject = JSONObject().apply {
                    put("temperature", 0.85)
                }
                put("generationConfig", configObject)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonRequest.toString().toRequestBody(mediaType)

            val urlWithKey = "$BASE_URL?key=$apiKey"
            val request = Request.Builder()
                .url(urlWithKey)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.getJSONArray("candidates")
                val firstCandidate = candidates.getJSONObject(0)
                val responseContent = firstCandidate.getJSONObject("content")
                val parts = responseContent.getJSONArray("parts")
                val text = parts.getJSONObject(0).getString("text")
                if (text.isNotBlank()) {
                    return@withContext text.trim()
                }
            }
            
            // If response failed or parsed empty, fallback to local
            return@withContext getOfflineResponse(promptType)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext getOfflineResponse(promptType)
        }
    }

    private fun getOfflineResponse(promptType: String): String {
        return when (promptType.lowercase()) {
            "prophecy" -> "🔮 Пророчество: " + offlineProphecies.random()
            "director_rant" -> "🎬 Крик режиссера: " + offlineDirectorRants.random()
            "fan_theory" -> "👽 Теория заговора: " + offlineFanTheories.random()
            else -> "✨ Мудрость Андрея: Тот, кто лежит на сцене на боку в позе RAMPAGE, преисполнился в актерском мастерстве больше, чем все скелеты OVERKILL на стульях."
        }
    }
}
