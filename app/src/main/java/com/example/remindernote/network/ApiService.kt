package com.example.remindernote.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiService {

    @POST("/api/user/login")
    fun verifyUser(@Header("authToken") authToken: String?): Call<JsonObject>

    @POST("/api/user/register")
    fun registerUser(@Header("authToken") authToken: String?, @Body data: JsonObject): Call<JsonObject>

    @POST("/api/user/update")
    fun updateUser(@Header("authToken") authToken: String?, @Body data: JsonObject): Call<JsonObject>

    @POST("/api/data/{path}")
    fun backup(
        @Header("authToken") authToken: String?,
        @Path("path") path: String,
        @Body data: JsonObject
    ): Call<JsonObject>

    @POST("/api/data/restore")
    fun restore(
        @Header("authToken") authToken: String?,
        @Body data: JsonObject
    ): Call<JsonObject>
}
