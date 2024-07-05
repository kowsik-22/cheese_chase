package com.example.task2.ui.theme

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface apiinterface2 {

    @GET("image")
    suspend fun getimage(@Query("character") character : String): Response<ResponseBody>
}
