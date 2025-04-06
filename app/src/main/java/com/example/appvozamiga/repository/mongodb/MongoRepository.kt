package com.example.appvozamiga.repository.mongodb

import android.util.Log
import com.example.appvozamiga.repository.mongodb.models.UserData

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
}
