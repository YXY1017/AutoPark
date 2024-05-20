package com.example.autopark.view


import LocationViewModel
import UserViewModel
import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu

import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.autopark.Routes
import com.example.autopark.viewmodel.MapViewModel
import com.example.autopark.viewmodel.NavigationViewModel
import com.example.autopark.viewmodel.ParkingSessionViewModel

import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import androidx.compose.material.Surface
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationSearching

import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.annotations

import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager


import kotlinx.coroutines.launch


//A composable function to display the map and all parking locations nearby.
@RequiresApi(64)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(drawerState: DrawerState, navViewModel: NavigationViewModel, navController: NavController, mapViewModel: MapViewModel, locationViewModel: LocationViewModel, parkingSessionViewModel: ParkingSessionViewModel,userViewModel: UserViewModel) {

    LaunchedEffect(Unit) {
        locationViewModel.fetchLocations()
        userViewModel.getCarsByUser()
        userViewModel.fetchUserAndCards()

        parkingSessionViewModel.getAllUserParkingSessions(userViewModel.getUserEmail())
        //fetch all the parking session

    }

    navViewModel.name.value

    val context = LocalContext.current
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val locationState = remember { mutableStateOf<Location?>(null) }

    val locationListener = remember {
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.d("LocationUpdates", "New Location: Lat ${location.latitude}, Lng ${location.longitude}")
                locationState.value = location
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
    }

    // Launcher for handling permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            try {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000L,
                    0f,
                    locationListener
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            val coroutineScope = rememberCoroutineScope()
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .shadow(
                        10.dp,
                        shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp),
                        clip = true
                    )
                    .clip(RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp)),
                title = { Text(text = "Home") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    }) {
                        Icon(imageVector = Icons.Filled.Menu,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    )
    {
        Box(modifier = Modifier
            .fillMaxSize())
        {
            // Call map by using retrofit
            Box(contentAlignment = Alignment.Center) {
                locationState.value?.let { location ->
                    Text("Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                }
                // Ask for gps permission
                LaunchedEffect(key1 = true) {
                    permissionLauncher.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ))
                }
                MapScreen(modifier = Modifier
                    .fillMaxSize(), locationState, mapViewModel, locationViewModel, navController, parkingSessionViewModel)
            }

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
                .clip(RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp))) {
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    ScrollBoxes(navController,locationViewModel,parkingSessionViewModel)
                }
            }
        }
    }
}



@Composable
private fun ScrollBoxes(navController: NavController,locationViewModel: LocationViewModel,parkingSessionViewModel: ParkingSessionViewModel) {


    //locations = locations in firebase, fetched from firebase, a list of Location()
    val locations by locationViewModel.locations.collectAsState()

    //lazy column
    LazyColumn(modifier = Modifier.background(color = Color.Transparent)) {
        items(locations) { location ->
            LocationCardOne(
                location = location,onClick = {//if you click on each item

                    //pass selected location id to viewModel
                    parkingSessionViewModel.setLocationId(location.locationId)
                    parkingSessionViewModel.setRatePerHous(location.rateDollarPerHour)

                    //navigate to Confirmation Screen
                    navController.navigate(Routes.ConfirmationScreen.value)


                }
            )
        }
    }

}

@Composable
fun LocationCardOne(location: com.example.autopark.model.Location, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp))// Adjust the size as necessary
                    .background(MaterialTheme.colorScheme.primary), // Set background color for the ID box
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = location.areaCode, // Replace with your location ID
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = location.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = location.address, color = MaterialTheme.colorScheme.primary)
            }

            Icon(
                imageVector = Icons.Default.Info, // Use appropriate icon
                contentDescription = "Info",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp) // Adjust icon size as necessary
            )
        }
    }
}

@Composable
fun MapScreen(modifier: Modifier = Modifier.fillMaxSize(), locationState: State<Location?>, mapViewModel: MapViewModel, locationViewModel: LocationViewModel,navController: NavController, parkingSessionViewModel: ParkingSessionViewModel) {

    val context = LocalContext.current
    val retrofitData by mapViewModel.retrofitResponse.collectAsState()
    var pointAnnotationManager: PointAnnotationManager? by remember { mutableStateOf(null) }
    val cameraInitialized = remember { mutableStateOf(false) }
    val locations by locationViewModel.locations.collectAsState()
    // For return current location button
    var mapViewReference: MapView? by remember { mutableStateOf(null) }

    // For showing pop-ups
    var showPopUp by remember { mutableStateOf(false) }
    var popUpContent by remember { mutableStateOf("") }
    var locationId by remember { mutableStateOf(0) }
    var popUpPosition by remember { mutableStateOf(Point.fromLngLat(0.0, 0.0)) }
//    var popUpScreenPosition by remember { mutableStateOf(Offset.Zero) } // Screen coordinates for positioning the popup


    // Resize the marker
    fun resizeBitmap(drawableId: Int, width: Int, height: Int): Bitmap {
        val bitmap = BitmapFactory.decodeResource(context.resources, drawableId)
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    // Initialize the MapView and annotation manager
    AndroidView(
        modifier = modifier,
        factory = { MapView(context).also { mapView ->
            mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->

                // Load the current location marker image from resources
                val resourceId = context.resources.getIdentifier("current_location_icon", "drawable", context.packageName)
                if (resourceId != 0) {
                    val bitmap = resizeBitmap(resourceId, 120, 140)  // Adjust width and height as needed
                    style.addImage("custom-marker", bitmap)
                } else {
                    // Log or handle the missing resource appropriately
                    println("Error: Resource current_location_icon not found.")
                }

                // Load and set the parking location area marker image
                val otherLocationResourceId = context.resources.getIdentifier("parking_location", "drawable", context.packageName)
                if (otherLocationResourceId != 0) {
                    val bitmap = resizeBitmap(otherLocationResourceId, 140, 140)
                    style.addImage("parking-location-marker", bitmap)
                } else {
                    println("Error: Resource other_location_icon not found.")
                }

                val annotationApi = mapView.annotations
                pointAnnotationManager = annotationApi.createPointAnnotationManager().apply {
                    addClickListener { pointAnnotation ->
                        // Convert point to screen position
//                        mapView.getMapboxMap().pixelForCoordinate(pointAnnotation.point).let { screenPos ->
//                            popUpScreenPosition = Offset(screenPos.x.toFloat(), screenPos.y.toFloat())
//                        }
                        showPopUp = true
                        // Determine content based on whether it's the user's location or another
                        if (pointAnnotation.iconImage == "custom-marker") {
                            popUpContent = "Your current Location"
                        } else {
                            // Find the Firebase location that matches this point
                            val matchingLocation = locations.firstOrNull { location ->
                                location.latitude == pointAnnotation.point.latitude() &&
                                        location.longitude == pointAnnotation.point.longitude()
                            }
                            if (matchingLocation != null) {
                                popUpContent = "Parking Location Name: ${matchingLocation.name}\n Address: ${matchingLocation.address}"
                                locationId = matchingLocation.locationId
                            } else {
                                popUpContent = "No match location"
                            }
                        }
                        popUpPosition = pointAnnotation.point
                        true
                    }
                }
                mapViewReference = mapView  // Storing reference to mapView
            }
        }},
        update = { mapView ->
            // Update annotations based on Retrofit data
            pointAnnotationManager?.let { manager ->
                manager.deleteAll()  // Clear existing annotations

                // Iterate over each location fetched from Retrofit and create annotations
                retrofitData.makeLocation.forEach { markLocation ->
                    val point = Point.fromLngLat(markLocation.lng, markLocation.lat)
                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(point)
                    manager.create(pointAnnotationOptions)
                }

                if (locationState.value != null) {
                    // Handle the user's current location marker
                    locationState.value?.let { location ->
                        val currentUserPoint = Point.fromLngLat(location.longitude, location.latitude)
                        val userLocationMarker = PointAnnotationOptions()
                            .withPoint(currentUserPoint)
                            .withIconImage("custom-marker")
//                            .withTextField("Your current Location")

                        // Add or update the location marker
                        manager.create(userLocationMarker)

                        // Update the camera to the current location if not initialized
                        if (!cameraInitialized.value) {
                            mapView.getMapboxMap().setCamera(
                                CameraOptions.Builder()
                                    .center(Point.fromLngLat(location.longitude, location.latitude-0.001))
                                    .zoom(16.0)
                                    .build()
                            )
                            cameraInitialized.value = true  // Set camera initialized to true
                        }
                    }
                } else{
                    // Optionally center the map on the first location from Retrofit if no current location
                    retrofitData.makeLocation.firstOrNull()?.let { location ->
                        mapView.getMapboxMap().setCamera(
                            CameraOptions.Builder()
                                .center(Point.fromLngLat(location.lng, location.lat))
                                .zoom(16.0)
                                .build()
                        )
                    }
                }

                // Display other locations from Firebase
//                val locations by locationViewModel.locations.collectAsState()
                locations.forEach { location ->
                    val point = Point.fromLngLat(location.longitude, location.latitude)
                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(point)
                        .withIconImage("parking-location-marker")
                    manager.create(pointAnnotationOptions)
                }
            }
        }
    )

    // UI component to re-center map
    Box (
        modifier = Modifier.fillMaxSize()
    ){
        Button(
            onClick = {
                locationState.value?.let { location ->
                    // Use the reference to mapView to set camera
                    mapViewReference?.getMapboxMap()?.setCamera(
                        CameraOptions.Builder()
                            .center(Point.fromLngLat(location.longitude, location.latitude-0.001))
                            .zoom(16.0)
                            .build()
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp),
            shape = RoundedCornerShape(50)
        ) {
            Icon(
                imageVector = Icons.Filled.LocationSearching,
                contentDescription = "Locate Me",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    // Pop-up UI for the clicked location
    if (showPopUp) {
        PopupUI(
            popUpContent,
            locationId,
            navController,
            parkingSessionViewModel,
            onCloseClick = { showPopUp = false }
        )
    }
}


@Composable
fun PopupUI(text: String,locationId: Int, navController: NavController, parkingSessionViewModel: ParkingSessionViewModel ,onCloseClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier.padding(top = 150.dp),
            elevation = 5.dp,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.background(Color.White).padding(8.dp)) {
                IconButton(onClick = onCloseClick) {
                    Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(15.dp))
                }
                Spacer(modifier = Modifier.height(1.dp))
                Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.CenterHorizontally))
                if (text != "Your current Location"){
                    Button(
                        onClick = {
                            //pass selected location id to viewModel
                            parkingSessionViewModel.setLocationId(locationId)

                            //navigate to Confirmation Screen
                            navController.navigate(Routes.ConfirmationScreen.value)
                        },
                        modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "View Parking Location"
                        )
                    }
                } else{
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}