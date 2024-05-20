
package com.example.autopark


import LocationViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.autopark.ui.theme.AutoParkTheme
import com.example.autopark.viewmodel.NavigationViewModel
import UserViewModel
import com.example.autopark.utils.DatabaseInitializer
import com.example.autopark.viewmodel.MapViewModel
import com.example.autopark.viewmodel.ParkingSessionViewModel
import com.mapbox.maps.plugin.Plugin


//import com.example.assignment1.ui.theme.Assignment1Theme

class MainActivity : ComponentActivity() {
    private val viewModel: NavigationViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val locationViewModel:LocationViewModel by viewModels()
    private val mapViewModel: MapViewModel by viewModels()
    private val parkingSessionViewModel:ParkingSessionViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AutoParkTheme {
                MainNavigation(navViewModel = viewModel, userViewModel=userViewModel,locationViewModel=locationViewModel, mapViewModel = mapViewModel,
                    parkingSessionViewModel = parkingSessionViewModel
                )
            }
        }

        // insert locations into firebase from here
        //val locations = DatabaseInitializer.generateMockLocations()
        //DatabaseInitializer.insertMockLocationsToFirebase(locations)

    }





}



