package com.phantomfiles.pro.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class GroqRequest(
    val model: String = "llama3-8b-8192",
    val messages: List<GroqMessage>,
    val temperature: Double = 0.3,
    val max_tokens: Int = 1024
)

data class GroqMessage(
    val role: String,
    val content: String
)

data class GroqResponse(
    val choices: List<GroqChoice>?
)

data class GroqChoice(
    val message: GroqMessage?
)

interface GroqApi {
    @POST("chat/completions")
    suspend fun chat(
        @Header("Authorization") authorization: String,
        @Body request: GroqRequest
    ): GroqResponse
}
