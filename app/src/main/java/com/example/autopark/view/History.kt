package com.example.autopark.view

import LocationViewModel
import UserViewModel
import androidx.annotation.RequiresApi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow

import androidx.compose.ui.unit.dp

import com.example.autopark.viewmodel.ParkingSessionViewModel
import kotlinx.coroutines.launch

//A composable function to display all the parking history in this screen.
@RequiresApi(64)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(drawerState: DrawerState, parkingSessionViewModel: ParkingSessionViewModel, userViewModel: UserViewModel, locationViewModel: LocationViewModel) {
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
                title = { Text(text = "Parking History") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =
                    MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    }) {
                        Icon(Icons.Filled.Menu, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                    }  }
            )
        }
    ) {
        //start your parking history here
            paddingValues ->
        // padding of the scaffold is enforced to be used
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ){

            ParkingHistory(parkingSessionViewModel, userViewModel, locationViewModel)

        }
    }
}


//A composable function to display a single parking session.
@Composable
fun ParkingHistory(parkingSessionViewModel: ParkingSessionViewModel, userViewModel: UserViewModel, locationViewModel: LocationViewModel) {
    val userEmail = userViewModel.user.value.email
    val parkingSessions by parkingSessionViewModel.userParkingSessions.observeAsState(emptyList())


    // Load user parking sessions when the composable is first created
    LaunchedEffect(userEmail) {
        parkingSessionViewModel.getAllUserParkingSessions(userEmail)
    }

    LazyColumn {
        items(parkingSessions) { session ->
            Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Format the LocalDateTime to the desired output string
                    Text("Start Time: ${session.startTime}")
                    Text("End Time: ${session.endTime}")
                    Text("Cost: \$${session.cost}")
                    Text("Location ID: ${locationViewModel.getLocationName(session.locationId)}")
                }
            }
        }
    }
}


