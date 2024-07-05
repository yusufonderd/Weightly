package com.yonder.weightly.network

import retrofit2.Response
import retrofit2.http.GET

interface FirebaseApiService {
    @GET("getLatestRelease?platform=android")
    suspend fun getLatestRelease(): Response<LatestVersionResponse>
}
