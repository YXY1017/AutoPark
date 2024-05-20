package com.example.autopark.repository

import com.example.autopark.MapRetrofitObject
import com.example.autopark.model.MapResponse
import com.mapbox.common.MapboxOptions

class MapRepository {
    //create the repository as an additional abstraction layer between the ViewModel and RetrofitInterface to provide the keys
    private val mapService = MapRetrofitObject.retrofitService
    private val access_token = "pk.eyJ1IjoieXF6aHUiLCJhIjoiY2x2d2ZraXg4MjlqNzJucnJpcDJmNGplcCJ9.vgNVRcxjHKk0nEyqKE6sYQ"

    //suspend function that calls the getLocations() function in the interface for the GET method
    suspend fun getResponse(): MapResponse {
        MapboxOptions.accessToken = access_token
        return mapService.getLocations(
            access_token
        )
    }
}