package com.example.autopark

import com.example.autopark.model.MapResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MapRetrofitInterface {
    //use the GET operation (the most common method used for queries/search) that accepts the keys
    //(always needed with public APIs) and the query itself. We
    //make the return type as the MarkLocation type that created based on the mapping model.
    @GET("geocoding/v5/mapbox.places/World.json?")
    suspend fun getLocations(
        @Query("access_token") accessToken: String
    ): MapResponse
}