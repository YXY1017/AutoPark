package com.example.autopark

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MapRetrofitObject {
    private val BASE_URL = "https://api.mapbox.com/"
    val retrofitService: MapRetrofitInterface by lazy {
        //use "by lazy" intentionally to delay object creation until it is really needed to avoid unnecessary computation
        Log.i("LocationRetrofitObject", "called")
        val retrofit = Retrofit.Builder() // create an instance of Retrofit
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(MapRetrofitInterface::class.java)
    }
}