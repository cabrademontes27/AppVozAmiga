package com.example.appvozamiga.data.network

import com.example.appvozamiga.data.models.Medicamento
import com.example.appvozamiga.data.models.UserData
import com.example.appvozamiga.data.network.VerificationStatus
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
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

    @GET("api/medicamentos")
    suspend fun getMedicamentos(@Query("email") email: String): Response<List<Medicamento>>

    @POST("api/medicamentos")
    suspend fun addMedicamento(@Body medicamento: Medicamento): Response<Unit>

    @PUT("api/medicamentos/{id}")
    suspend fun updateMedicamento(@Path("id") id: String, @Body medicamento: Medicamento): Response<Unit>

    @DELETE("api/medicamentos/{id}")
    suspend fun deleteMedicamento(@Path("id") id: String): Response<Unit>




}