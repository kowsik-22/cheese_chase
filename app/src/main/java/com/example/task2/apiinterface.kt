package com.example.task2

import retrofit2.Call
import retrofit2.http.GET

interface apiinterface {

    @GET("obstacleLimit")
    fun getData(): Call<Aobstacle>

}