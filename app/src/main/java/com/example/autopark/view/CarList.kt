package com.example.autopark.view



import UserViewModel
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.autopark.viewmodel.NavigationViewModel
import com.example.autopark.R
import com.example.autopark.Routes
import com.example.autopark.model.Car
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow


//A composable function for car list screen.
@RequiresApi(64)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarListScreen(drawerState: DrawerState, navViewModel: NavigationViewModel, navController: NavController,userViewModel: UserViewModel) {
    navViewModel.name.value
    Scaffold(
        topBar = {
            val coroutineScope = rememberCoroutineScope()
            TopAppBar(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .shadow(10.dp,
                        shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp),
                        clip = true)
                    .clip(RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp)),
                title = { Text(text = "Your cars") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            drawerState.open() // back to confirmed
                        }
                    }) {
                        Icon(imageVector = Icons.Filled.Menu,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions ={
                    IconButton(onClick = {
                        //navigate to add car screen
                        navController.navigate(Routes.AddCarScreen.value)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.addcar),
                            contentDescription = "Add Car",
                            modifier = Modifier.size(24.dp), // Adjust the size as needed
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    )
    {
        Box(modifier = Modifier
            .fillMaxSize())
        {
            DisplayCars(userViewModel = userViewModel, navController = navController,drawerState = drawerState)
            //Spacer(modifier = Modifier.weight(1f))
        }

    }
}


//A composable function to display cars.
@Composable
fun DisplayCars(userViewModel: UserViewModel, drawerState: DrawerState, navController: NavController) {
    val cars by userViewModel.cars.collectAsState()
    LaunchedEffect(key1 = true) {
        userViewModel.getCarsByUser()
    }

    Scaffold(
        topBar = {
            TopAppBarComponent(drawerState = drawerState, navController = navController)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(Modifier.background(color = Color.Transparent)) {
                items(cars) { car ->
                    CarListItem(
                        car = car,
                        userViewModel = userViewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}

// Make sure the Car data class is correct and not nested within CarListScreen
// Update the CarListItem composable function to accept the Car data class
@Composable
fun CarListItem(car: Car, navController: NavController, userViewModel: UserViewModel) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.car),
                    contentDescription = "Car",
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = car.plateId,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${car.brand} ${car.modelName}, ${car.yearMake}",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navController.navigate("EditCar/${car.plateId}")
                }
            ) {
                Text("Edit")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    userViewModel.deleteCar(car.plateId) { success ->
                        if (success) {
                            Toast.makeText(context, "Car deleted successfully", Toast.LENGTH_SHORT).show()
                            userViewModel.getCarsByUser()
                        } else {
                            Toast.makeText(context, "Failed to delete car", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            ) {
                Text("Delete")
            }
        }
    }
}


//A composable function for editing car details.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCarScreen(plateId: String, navController: NavController, userViewModel: UserViewModel) {
    val car = remember { mutableStateOf<Car?>(null) }
    val context = LocalContext.current

    val brands = listOf("BWM","TOYOTA","BENZ","TESLA","OTHER")
    val models = listOf("Sedan","Coupe","SUV","Wagon","Hatch")


    var isExpandedModel by remember { mutableStateOf(false) }
    var isExpandedBrand by remember { mutableStateOf(false) }

    var selectedModel by remember{ mutableStateOf("") }
    var selectedBrand by remember{ mutableStateOf("") }



    LaunchedEffect(plateId) {
        userViewModel.getCarByPlateId(plateId) { fetchedCar ->
            fetchedCar?.let {
                // Update the selectedBrand and selectedModel when car is fetched
                selectedBrand = it.brand
                selectedModel = it.modelName
                car.value = it
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Car: $plateId") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        car.value?.let { editableCar ->


            var yearMake by remember { mutableStateOf(editableCar.yearMake.toString()) }
            val editablePlateId by remember { mutableStateOf(editableCar.plateId) }

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                /*
                Text(
                    text = "Plate",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold
                )

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = editablePlateId,
                    onValueChange = { editablePlateId = it },
                )
                */

                Spacer(Modifier.height(8.dp))

                //drop down menu for brand
                // State Dropdown
                Text(
                    text = "Brand",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold
                )
                androidx.compose.material3.ExposedDropdownMenuBox(
                    expanded = isExpandedBrand,
                    onExpandedChange = { isExpandedBrand = it }
                ) {
                    androidx.compose.material3.TextField(
                        modifier = Modifier
                            .background(color = Color.Transparent)
                            .menuAnchor()
                            .focusProperties {
                                canFocus = false
                            }
                            .padding(bottom = 20.dp)
                            .fillMaxWidth(),
                        readOnly = true,
                        value = selectedBrand,
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedBrand)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = isExpandedBrand,
                        onDismissRequest = { isExpandedBrand = false },
                    )
                    {
                        brands.forEach { selectionOption ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = {
                                    Text(
                                        selectionOption, fontWeight = if
                                                                              (selectedBrand == selectionOption) FontWeight.Bold else null
                                    )
                                },
                                onClick = {
                                    selectedBrand = selectionOption
                                    isExpandedBrand = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }


                Spacer(modifier = Modifier.height(10.dp))

                // State Dropdown
                Text(
                    text = "Model",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold
                )
                androidx.compose.material3.ExposedDropdownMenuBox(
                    expanded = isExpandedModel,
                    onExpandedChange = { isExpandedModel = it }
                ) {
                    androidx.compose.material3.TextField(
                        modifier = Modifier
                            .background(color = Color.Transparent)
                            .menuAnchor()
                            .focusProperties {
                                canFocus = false
                            }
                            .padding(bottom = 20.dp)
                            .fillMaxWidth(),
                        readOnly = true,
                        value = selectedModel,
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedModel)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = isExpandedModel,
                        onDismissRequest = { isExpandedModel = false },
                    )
                    {
                        models.forEach { selectionOption ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = {
                                    Text(
                                        selectionOption, fontWeight = if
                                                                              (selectedModel == selectionOption) FontWeight.Bold else null
                                    )
                                },
                                onClick = {
                                    selectedModel = selectionOption
                                    isExpandedModel = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }


                Spacer(Modifier.height(8.dp))

                //Spacer(Modifier.height(8.dp))
                //drop down menu for model

                Text(
                    text = "Year",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold
                )

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value =  yearMake,
                    onValueChange = { yearMake  = it;},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                //add validation here

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    val updatedCar = editableCar.copy(plateId = editablePlateId, brand = selectedBrand, modelName = selectedModel, yearMake = yearMake.toInt())
                    userViewModel.updateCar(updatedCar) { success ->
                        if (success) {
                            Toast.makeText(context, "Car updated successfully", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Failed to update car", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text("Save Changes")
                }
            }
        } ?: Text("Loading or no car data available for plate ID: $plateId")
    }
}
