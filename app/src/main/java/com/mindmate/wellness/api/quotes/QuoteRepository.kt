package com.mindmate.wellness.api.quotes

import com.mindmate.wellness.api.RetrofitClient
import com.mindmate.wellness.data.model.Quote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuoteRepository {

    private val api: QuoteApiService by lazy {
        RetrofitClient.getClient("https://zenquotes.io/api/")
            .create(QuoteApiService::class.java)
    }

    private val fallbackQuotes = listOf(
        Quote("Your GPA or exam marks do not define your entire self-worth. You are doing the best you can.", "Student Wellness"),
        Quote("The greatest weapon against stress is our ability to choose one thought over another.", "William James"),
        Quote("You don't have to control your thoughts. You just have to stop letting them control you.", "Dan Millman"),
        Quote("Breathe. It is just a semester, not a lifetime. Everything unfolds at its own right time.", "Mindmate"),
        Quote("Comparison is the thief of joy. Focus on your own growth and celebrate small victories.", "Theodore Roosevelt"),
        Quote("Take a rest. A field that has rested yields a bountiful crop.", "Ovid")
    )

    suspend fun getDailyQuote(): Quote = withContext(Dispatchers.IO) {
        try {
            val response = api.getTodayQuote()
            if (response.isNotEmpty()) {
                val item = response[0]
                Quote(quote = item.text, author = item.author)
            } else {
                getRandomFallback()
            }
        } catch (_: Exception) {
            getRandomFallback()
        }
    }

    fun getRandomFallback(): Quote {
        return fallbackQuotes.random()
    }
}
