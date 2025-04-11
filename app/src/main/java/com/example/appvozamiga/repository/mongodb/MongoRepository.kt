package com.example.appvozamiga.repository.mongodb

import android.util.Log
import com.example.appvozamiga.repository.mongodb.models.UserData
import com.example.appvozamiga.repository.mongodb.models.VerificationStatus

object MongoUserRepository {

    //  Esta versión es la correcta para Retrofit + coroutines
    suspend fun registerUser(user: UserData): Boolean {
        return try {
            val response = RetrofitClient.apiService.registerUser(user)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("MongoRepo", "Error Retrofit: ${e.message}")
            false
        }
    }
    suspend fun checkEmailVerified(email: String): VerificationStatus? {

        return try {
            val response = RetrofitClient.apiService.checkEmailVerified(email)
            Log.d("RegisterViewModel", "Resultado: ${response.body()?.verified}")
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("MongoRepo", "Error checkEmailVerified: ${e.message}")
            null
        }
    }


    suspend fun verifyToken(tokenId: String): String? {
        return try {
            val response = RetrofitClient.apiService.verifyToken(tokenId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("MongoRepo", "Error verifyToken: ${e.message}")
            null
        }
    }
}
