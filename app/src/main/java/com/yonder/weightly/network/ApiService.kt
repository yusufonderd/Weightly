package com.yonder.weightly.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {
    @POST("send-mail/index.php")
    suspend fun sendEmail(
        @Body request: SendEmailRequest
    ): Response<SendEmailResponse>
}

