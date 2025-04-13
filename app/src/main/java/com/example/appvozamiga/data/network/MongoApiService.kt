package com.example.appvozamiga.voice

import com.example.appvozamiga.data.models.UserData
import com.example.appvozamiga.voice.models.VerificationStatus
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MongoApiService {

    @POST("api/registerUser")
    suspend fun registerUser(@Body user: UserData): Response<Unit>

    @GET("api/checkVerified")
    suspend fun checkEmailVerified(@Query("email") email: String): Response<VerificationStatus>

    @GET("verify")
    suspend fun verifyToken(@Query("id") tokenId: String): Response<String>


}