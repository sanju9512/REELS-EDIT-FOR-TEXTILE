package com.example.ai

import android.util.Log
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

data class GeneratedProductCopy(
    val description: String,
    val hashtags: String,
    val viralHook: String,
    val fabricCare: String
)

class GeminiDescriptionService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun generateTextileCopy(
        title: String,
        category: String,
        fabricType: String,
        price: Double,
        tone: String = "Luxury Festive",
        language: String = "English & Hindi mix (Hinglish)"
    ): GeneratedProductCopy = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // If valid API key is present and not placeholder, call Gemini REST API
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    You are a top textile and fashion marketing expert for Indian and global ethnic wear.
                    Generate a high-converting, trendy social media product copy for:
                    Product: $title
                    Category: $category
                    Fabric: $fabricType
                    Price: ₹$price
                    Tone: $tone
                    Language: $language
                    
                    Return output strictly as a JSON object with these 4 keys:
                    "description": (a compelling 2-3 sentence product story highlighting weave, craftsmanship, comfort, styling tips),
                    "hashtags": (8-10 trending tags including #saree #textilefashion #ethnicwear etc),
                    "viralHook": (a 1-line punchy hook for Instagram Reel / YouTube Shorts),
                    "fabricCare": (dry clean / handwash recommendation)
                    
                    Do NOT include markdown formatting or backticks around the json if possible, just the json.
                """.trimIndent()

                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

                val jsonBody = JSONObject().apply {
                    val contentsArray = JSONArray().apply {
                        val partObj = JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", prompt)
                                })
                            })
                        }
                        put(partObj)
                    }
                    put("contents", contentsArray)
                }

                val request = Request.Builder()
                    .url(url)
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val root = JSONObject(responseBody)
                    val candidates = root.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCandidate = candidates.getJSONObject(0)
                        val text = firstCandidate.getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text")

                        val cleanJson = text
                            .replace("```json", "")
                            .replace("```", "")
                            .trim()

                        val parsed = JSONObject(cleanJson)
                        return@withContext GeneratedProductCopy(
                            description = parsed.optString("description", "Exquisite $fabricType $category crafted with premium weave."),
                            hashtags = parsed.optString("hashtags", "#$category #$fabricType #TextileFashion #EthnicWear"),
                            viralHook = parsed.optString("viralHook", "✨ Stop scrolling: pure $fabricType perfection!"),
                            fabricCare = parsed.optString("fabricCare", "Dry clean only to maintain zari and silk lustre.")
                        )
                    }
                }
            } catch (e: Exception) {
                Log.w("GeminiService", "Gemini API call fell back to local domain intelligence: ${e.message}")
            }
        }

        // Domain-tuned fallback generator for textile commerce (works seamlessly offline or without live key)
        generateDomainTunedCopy(title, category, fabricType, price, tone)
    }

    private fun generateDomainTunedCopy(
        title: String,
        category: String,
        fabricType: String,
        price: Double,
        tone: String
    ): GeneratedProductCopy {
        val description = when (category.lowercase()) {
            "saree" -> "Drape yourself in royal elegance with this handcrafted $title in pure $fabricType. Features rich woven borders, authentic heritage motifs, and a fluid silhouette that turns every festive evening into an unforgettable statement. Paired with unstitched blouse piece."
            "kurti", "kurti & suit" -> "Redefine comfort and high-fashion ethnic style with our $title. Made from breathable $fabricType adorned with delicate artisanal needlework, tailored for modern festive celebrations, office chic, and intimate gatherings."
            "lehenga" -> "A showstopper bridal & reception masterpiece! This $title in opulent $fabricType is drenched in celebratory sparkle, intricate kalis, and a voluminous flair engineered for timeless wedding portraits."
            else -> "Premium $title made from ethically sourced $fabricType. Superior tensile strength, tactile richness, and natural drape crafted for discerning fashion curators. Ready to ship nationwide."
        }

        val hashtags = when (category.lowercase()) {
            "saree" -> "#SareeLove #HandloomSaree #${fabricType.replace(" ", "")} #IndianWeaves #BridalFashion #FestiveSaree #VaranasiHeritage #TextileArtisan"
            "kurti", "kurti & suit" -> "#DesignerKurti #EthnicWear #SuitSet #IndianOutfits #OOTDFashion #SummerEthnic #ChikankariLove"
            else -> "#TextileIndustry #FabricLove #IndianFashion #Handcrafted #LuxuryEthnic #VocalForLocal"
        }

        val hook = when {
            price > 5000 -> "✨ The ₹${price.toInt()} luxury $category that everyone is asking about this wedding season!"
            tone.contains("Viral", ignoreCase = true) -> "🔥 Don't buy another $category until you see this handloom $fabricType weave!"
            else -> "👗 How to style this pure $fabricType $title for instant royal glam!"
        }

        val care = when {
            fabricType.contains("silk", ignoreCase = true) || fabricType.contains("zari", ignoreCase = true) ->
                "Dry clean only. Store in muslin cloth away from direct sunlight."
            fabricType.contains("cotton", ignoreCase = true) || fabricType.contains("linen", ignoreCase = true) ->
                "Gentle handwash with mild shampoo in cold water. Iron on reverse."
            else -> "Dry clean recommended for fabric longevity."
        }

        return GeneratedProductCopy(
            description = description,
            hashtags = hashtags,
            viralHook = hook,
            fabricCare = care
        )
    }
}
