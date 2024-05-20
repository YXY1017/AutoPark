package com.example.autopark.view

import LocationViewModel
import UserViewModel
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.autopark.viewmodel.NavigationViewModel
import com.example.autopark.R
import com.example.autopark.Routes
import com.example.autopark.model.Car
import com.example.autopark.model.Location
import com.example.autopark.viewmodel.ParkingSessionViewModel
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext

import com.example.autopark.model.BankCard
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


//A composable function to display confirmation screen.
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(drawerState: DrawerState, navViewModel: NavigationViewModel, navController: NavController,locationViewModel: LocationViewModel, parkingSessionViewModel: ParkingSessionViewModel,userViewModel: UserViewModel) {
    navViewModel.name.value

    Scaffold(
        topBar = {
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
                title = { Text(text = "") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =
                    MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        //back arrow
                        navController.navigate(Routes.Home.value) //return to homepage

                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                    }  }

            )
        }
    ) { paddingValues ->
        // padding of the scaffold is enforced to be used
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        )
        {
            DisplayConfirmation(navController, userViewModel,parkingSessionViewModel, locationViewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DisplayConfirmation(navController: NavController,userViewModel: UserViewModel, parkingSessionViewModel: ParkingSessionViewModel, locationViewModel:LocationViewModel){

    //firebase load data， return list of location()
    //locationViewModel.fetchLocations()
    val fetchedLocation = locationViewModel.locations.collectAsState().value

    //firebase load data, return as list of cars()
    //userViewModel.getCarsByUser()
    val fetchedCars = userViewModel.cars.collectAsState().value

    // Retrieve the selected location ID from the parking session ViewModel
    val selectedLocationId = parkingSessionViewModel.getLocation()

    // Determine the default or initial location
    val initialLocation = fetchedLocation.firstOrNull() ?: Location(/* provide default values here if the list might be empty initially */)

    // Initialize and determine the selected location as a mutable state
    val selectedLocation = remember { mutableStateOf(initialLocation) }

    // Update the selected location based on the location ID when locations are fetched
    LaunchedEffect(fetchedLocation, selectedLocationId) {
        selectedLocation.value = fetchedLocation.find { it.locationId == selectedLocationId } ?: initialLocation
    }

    //can be null, when new user have no car added
    val selectedCar = remember { mutableStateOf(fetchedCars.firstOrNull()) }

    val fetchedCards = userViewModel.bankCards

    Log.d("GetCards", "Updated number of bank cards: ${fetchedCards.size}")

    //can be null, when user have no cards added
    val selectedCard = remember { mutableStateOf(fetchedCards.firstOrNull())}

    LaunchedEffect(fetchedLocation,fetchedCards,fetchedCars) {
        // Log the list to debug
        Log.d("ConfirmationScreen", "Locations updated: ${fetchedLocation.size}")
        // Additional log to confirm selection update
        Log.d("ConfirmationScreen", "Fetched cards: ${fetchedCards.size}")
        Log.d("ConfirmationScreen", "Fetched cars: ${fetchedCars.size}")

    }

    val openConfirmationDialog = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {

        //Top Bar
        //TopAppBarWithBack("")

        //Location Section
        LocationSection(fetchedLocation,selectedLocation,parkingSessionViewModel)

        Spacer(modifier = Modifier.height(8.dp))

        CarInformation(fetchedCars,selectedCar)

        //CarInformation(cars,selectedCar)

        Spacer(modifier = Modifier.height(20.dp))

        //use drop down
        //Detail Section， maxstay,rate,expiry time
        SessionInfo(selectedLocation)

        //Button
        StartButton(openConfirmationDialog,selectedCar,selectedCard)

        when{
            openConfirmationDialog.value ->{
                ParkingDetailsDialog(navController, parkingSessionViewModel, userViewModel, selectedCar.value, selectedCard.value) {
                    openConfirmationDialog.value=false
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        //Payment Section
        //PaymentInformation(creditCardNumber = "**** **** **** 1234")
        PaymentInformation(fetchedCards,selectedCard)

    }
}


//Display all the details of the current parking session.
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ParkingDetailsDialog(
    navController: NavController, parkingSessionViewModel: ParkingSessionViewModel, userViewModel: UserViewModel, selectedCar: Car?, selectedCard: BankCard?, onDismiss: () -> Unit) {

    //get start time
    val startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    //trim the start time into the format HH:MM


    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 64.dp), // Adjust padding as needed
            shape = RoundedCornerShape(16.dp)

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color.Blue,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "PLEASE CONFIRM YOUR PARKING DETAILS",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                )

                //location.areacode
                Text(
                    text = "Selected Location: " + parkingSessionViewModel.getLocation(),
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                val location by parkingSessionViewModel.location.observeAsState()

                LaunchedEffect(Unit) {
                    parkingSessionViewModel.getLocationInstance()
                }

                //returnedLocation = parkingSessionViewModel.getLocationInstance()

                location?.let {
                    Text(
                        text = "Location name: ${it.name}",
                        modifier = Modifier.padding(4.dp)
                    )
                    Text(
                        text="Locaiton adress:${it.address}",
                        modifier = Modifier.padding(4.dp)
                    )
                } ?: Text(
                    text = "Loading location...",
                    modifier = Modifier.padding(4.dp)
                )



                //location.name + location.address
                Text(
                    text = "Adress: 308 Woodside road",
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                if (selectedCar != null) {
                    Text(
                        text = "Select Car:"+selectedCar.plateId +" "+ selectedCar.modelName,

                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                if (selectedCard != null) {
                    val lastFourDigits = selectedCard.cardNumber.takeLast(4)

                    Text(
                        text = "Payment Card:"+lastFourDigits,

                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }


                Text(
                    text = "Start Time:"+startTime,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Parking is according to local laws and regulations.",
                )
                Text(
                    text = "Timed parking sessions can be stopped at any time from the 'Main' screen. This message can be disabled in the settings screen.",
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("CANCEL")
                    }
                    Button(
                        onClick = { if (selectedCar != null && selectedCard != null) {

                            parkingSessionViewModel.startParking(userViewModel.getUserEmail(),selectedCar.plateId, selectedCard.cardNumber)

                            //can not be null
                            navController.navigate(Routes.DuringSession.value)

                        }
                          //jump to session screen
                            /* on click process the payment*/ } ,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}


@Composable
fun StartButton(openConfirmationDialog: MutableState<Boolean>,selectedCar: MutableState<Car?>, selectedCard: MutableState<BankCard?>){
    val context = LocalContext.current // Get the local context to show toast
    Column(Modifier.padding(8.dp)) {
        Button(
            onClick = {
                if (selectedCar.value != null && selectedCard.value != null) {
                    Log.d("Selected car","$selectedCar")
                    Log.d("Selected card","$selectedCard")
                    openConfirmationDialog.value = !openConfirmationDialog.value

                } else {
                    // Show an error toast if either is null
                    Toast.makeText(context, "Please select a car and a payment method to start parking.", Toast.LENGTH_LONG).show()
                }

                 },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350))

        ) {
            Text("Start Parking")
        }

    }
}

@Composable
fun CarInformation(cars: List<Car>, selectedCar: MutableState<Car?>) {

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.car),
                contentDescription = "Car",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Conditional content based on whether a car is selected
            if (selectedCar.value != null) {
                Column {
                    Text(text = selectedCar.value!!.plateId, fontWeight = FontWeight.Bold)
                    Text(text = "${selectedCar.value!!.brand} ${selectedCar.value!!.modelName}, ${selectedCar.value!!.yearMake}")
                }
            } else {
                Text("No car selected", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { expanded = true },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("Change")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
                ) {
                if (cars.isNotEmpty()) {
                    cars.forEach { car ->
                        DropdownMenuItem(
                            onClick = {
                                selectedCar.value = car
                                expanded = false
                            },

                            text = {
                                Text(text = "${car.brand} ${car.modelName} - ${car.plateId}")
                            })
                    }
                } else {
                    DropdownMenuItem(
                        onClick = { expanded = false },
                        text = { Text("No cars available") }
                    )
                }
            }
        }
    }
}


@Composable
fun LocationSection(locations:List<Location>,selectedLocation:MutableState<Location>,parkingSessionViewModel: ParkingSessionViewModel) {

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            //.fillMaxWidth()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.cityofmonash), // Replace with your actual logo resource id
                contentDescription = "City Logo",
                modifier = Modifier.size(50.dp) // Assign a size to your logo if necessary
            )

            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = selectedLocation.value.name)
                Text(text = selectedLocation.value.areaCode)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { expanded = true },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("Change")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()

            ) {
                locations.forEach { location ->
                    DropdownMenuItem(
                        onClick = {
                            selectedLocation.value = location
                            expanded = false
                            parkingSessionViewModel.setLocationId(location.locationId)
                            parkingSessionViewModel.setRatePerHous(location.rateDollarPerHour)
                        },
                        text = {
                            Text(text = "${location.name} ${location.areaCode}")
                        })

                }
            }


        }
    }
}

@Composable
fun PaymentInformation(creditCards: List<BankCard>, selectedCard: MutableState<BankCard?>) {

    var expanded by remember { mutableStateOf(false) }


    Column(modifier = Modifier.padding(8.dp)) {

        Spacer(modifier = Modifier.height(6.dp))



        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // This will space the children as far apart as possible
        ) {
            // Conditionally display card details or a placeholder
            if (selectedCard.value != null) {
                Text(text = "Payment: **** **** **** ${selectedCard.value!!.cardNumber.takeLast(4)}")
            } else {
                Text(text = "No card selected")
            }
            //change card
            Button(
                onClick = { expanded = true },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("Change Card")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (creditCards.isNotEmpty()) {
                    creditCards.forEach { card ->
                        DropdownMenuItem(
                            onClick = {
                                selectedCard.value = card
                                expanded = false
                            },
                            text = {
                                Text(text = "**** **** **** ${card.cardNumber.takeLast(4)}")
                            }
                        )
                    }
                } else {
                    // Display a non-clickable menu item when no cards are available
                    DropdownMenuItem(
                        onClick = { expanded = false },
                        text = { Text("No cards available") }
                    )
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SessionInfo(selectedLocation:MutableState<Location>){

    //get start time
    //val startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

    // Current start time
    val startTime = LocalDateTime.now()

    // Formatter for the time display
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // Calculate expiry time by adding maxStay hours to the start time
    val expiryTime = startTime.plusHours(selectedLocation.value.maxStayInHours.toLong())

    // Format times for display
    val formattedExpiryTime = expiryTime.format(timeFormatter)

    Column(modifier = Modifier.padding(8.dp)){
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Text(
                text = "Expiry time:"+formattedExpiryTime, //selectedlocation.maxstay + current time start time
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Text(
                text = "Max stay :"+ selectedLocation.value.maxStayInHours+" hour",
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Text(
                text = "Rate:"+selectedLocation.value.rateDollarPerHour +" $/hour",
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

    }

}

