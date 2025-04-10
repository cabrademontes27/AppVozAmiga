package com.example.appvozamiga.repository.mongodb

import com.example.appvozamiga.repository.mongodb.models.UserData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MongoApiService {

    @POST("api/registerUser")
    suspend fun registerUser(@Body user: UserData): Response<Unit>
}