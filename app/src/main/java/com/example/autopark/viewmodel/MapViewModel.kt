package com.example.autopark.viewmodel

import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autopark.model.MapResponse

import com.example.autopark.repository.MapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.launch

class MapViewModel: ViewModel() {
    //create an instance of the retrofit Repository
    private val repository = MapRepository()
    private val _retrofitLocations =MutableStateFlow<MapResponse>(MapResponse())
    val retrofitResponse: StateFlow<MapResponse> = _retrofitLocations


    init {
        getResponse()
    }

    fun getResponse() {
        viewModelScope.launch {
            try {
                val response = repository.getResponse()
                _retrofitLocations.value = response
            } catch (e: Exception) {
                Log.e("Error ", "Response failed",e)
            }
        }
    }

}