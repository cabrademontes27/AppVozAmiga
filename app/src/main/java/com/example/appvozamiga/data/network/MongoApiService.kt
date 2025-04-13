package com.example.appvozamiga.data.network

import com.example.appvozamiga.data.models.UserData
import com.example.appvozamiga.data.network.VerificationStatus
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface MongoApiService {

    @POST("api/registerUser")
    suspend fun registerUser(@Body user: UserData): Response<Unit>

    @GET("api/checkVerified")
    suspend fun checkEmailVerified(@Query("email") email: String): Response<VerificationStatus>

    @GET("verify")
    suspend fun verifyToken(@Query("id") tokenId: String): Response<String>

    @PUT("/api/updateUser")
    suspend fun updateUser(@Body user: UserData): Response<Unit>

    @GET("api/getUserByEmail")
    suspend fun getUserByEmail(@Query("email") email: String): Response<UserData>

    @DELETE("api/deleteUser")
    suspend fun deleteUserByEmail(@Query("email") email: String): Response<Unit>



}