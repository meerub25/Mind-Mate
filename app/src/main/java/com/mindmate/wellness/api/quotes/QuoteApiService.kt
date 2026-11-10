package com.mindmate.wellness.api.quotes

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

data class ZenQuoteDto(
    @SerializedName("q") val text: String,
    @SerializedName("a") val author: String,
    @SerializedName("h") val html: String? = null
)

interface QuoteApiService {

    @GET("today")
    suspend fun getTodayQuote(): List<ZenQuoteDto>

    @GET("quotes")
    suspend fun getQuotes(): List<ZenQuoteDto>
}
