package com.mindmate.wellness.api.weather

import com.mindmate.wellness.api.RetrofitClient
import com.mindmate.wellness.data.model.WeatherInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository {

    private val api: WeatherApiService by lazy {
        RetrofitClient.getClient("https://api.open-meteo.com/")
            .create(WeatherApiService::class.java)
    }

    // Default coordinates: Lahore / Central Pakistani University Hub
    suspend fun getWeatherForCity(
        cityName: String = "Lahore",
        lat: Double = 31.5204,
        lon: Double = 74.3587
    ): WeatherInfo = withContext(Dispatchers.IO) {
        try {
            val response = api.getCurrentWeather(lat, lon)
            val curr = response.current

            val conditionPair = decodeWeatherCode(curr.weatherCode)
            val conditionName = conditionPair.first
            val emoji = conditionPair.second

            val tip = generateWellnessWeatherTip(curr.temperature, curr.weatherCode)

            WeatherInfo(
                cityName = cityName,
                temperature = curr.temperature,
                weatherCondition = conditionName,
                weatherEmoji = emoji,
                humidity = curr.humidity,
                wellnessTip = tip
            )
        } catch (_: Exception) {
            // Graceful fallback for offline mode
            WeatherInfo(
                cityName = cityName,
                temperature = 25.0,
                weatherCondition = "Pleasant",
                weatherEmoji = "🌤️",
                humidity = 45,
                wellnessTip = "A mild day on campus! Take a 10-minute mindful walk between study sessions to reset your focus."
            )
        }
    }

    private fun decodeWeatherCode(code: Int): Pair<String, String> {
        return when (code) {
            0 -> Pair("Clear Skies", "☀️")
            1, 2, 3 -> Pair("Partly Cloudy", "⛅")
            45, 48 -> Pair("Foggy / Mists", "🌫️")
            51, 53, 55 -> Pair("Light Drizzle", "🌦️")
            61, 63, 65 -> Pair("Rainy", "🌧️")
            71, 73, 75 -> Pair("Snow", "❄️")
            80, 81, 82 -> Pair("Showers", "🌧️")
            95, 96, 99 -> Pair("Thunderstorm", "⛈️")
            else -> Pair("Mild Weather", "🌤️")
        }
    }

    private fun generateWellnessWeatherTip(temp: Double, code: Int): String {
        return when {
            code in listOf(61, 63, 65, 80, 81, 82) ->
                "🌧️ Rainy weather on campus today. Make some warm chai and take gentle calming breaths while studying."
            temp >= 33.0 ->
                "☀️ High temperature today! Keep a water bottle nearby and stay hydrated to combat exam fatigue."
            temp in 18.0..28.0 ->
                "🌤️ Beautiful weather outside! Step away from your books for a 10-minute mindful stroll to boost serotonin."
            temp < 15.0 ->
                "🧣 Crisp chilly weather. Keep warm, stretch your shoulders, and rest your eyes between lecture slides."
            else ->
                "🌱 Take 3 deep breaths right now. A calm mind absorbs study material twice as effectively."
        }
    }
}
