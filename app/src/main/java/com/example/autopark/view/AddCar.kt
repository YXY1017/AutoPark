package com.example.autopark.view

import UserViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import com.example.autopark.Routes
import com.example.autopark.viewmodel.NavigationViewModel
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCarScreen(navViewModel: NavigationViewModel, navController: NavController, userViewModel: UserViewModel) {
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
                title = { Text(text = "Add New Car") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =
                    MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Routes.CarListScreen.value)
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                    }  },
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
            InputSection(userViewModel,navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputSection(userViewModel: UserViewModel, navController: NavController) {

    val states = listOf("VIC", "NSW", "QLD", "SA", "WA", "TAS", "NT", "ACT")
    val brands = listOf("Bmw","Toyota","Benz","Tesla","Lexus","OTHER")
    val models = listOf("Sedan","Coupe","Suv","Wagon","Hatch","Mpv")

    var isExpanded by remember { mutableStateOf(false) }
    var isExpandedModel by remember { mutableStateOf(false) }
    var isExpandedBrand by remember { mutableStateOf(false) }

    var selectedState by remember { mutableStateOf(states[0]) }

    var selectedModel by remember{ mutableStateOf("") }
    var selectedBrand by remember{ mutableStateOf("") }


    var carPlateIsError by remember { mutableStateOf(false) }
    val brandIsError by remember { mutableStateOf(false) }
    val modelIsError by remember { mutableStateOf(false) }
    var yearIsError by remember { mutableStateOf(false) }
    val plateId = remember { mutableStateOf("") }

    val yearMake = remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {

        Spacer(modifier = Modifier.height(20.dp))

        // State Dropdown
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
            value = plateId.value,
            onValueChange = { plateId.value = it.uppercase(Locale.getDefault()).take(6); carPlateIsError = isCarPlate(plateId.value) },
            placeholder = { Text("Enter plate ID: eg.1FX5KT") },
            isError = carPlateIsError,
            supportingText = {
                if (carPlateIsError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Invalid Car plate id.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                if (carPlateIsError)
                    Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colorScheme.error)
            }
        )
        Spacer(modifier = Modifier.height(10.dp))

        // State Dropdown
        Text(
            text = "Brand",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 4.dp),
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold
        )
        ExposedDropdownMenuBox(
            expanded = isExpandedBrand,
            onExpandedChange = { isExpandedBrand = it }
        ) {
            TextField(
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
                expanded =isExpandedBrand,
                onDismissRequest = { isExpandedBrand = false },
            )
            {
                brands.forEach { selectionOption ->
                    DropdownMenuItem(
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


        //Spacer(modifier = Modifier.height(10.dp))

        // State Dropdown
        Text(
            text = "Model",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 4.dp),
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold
        )
        ExposedDropdownMenuBox(
            expanded = isExpandedModel,
            onExpandedChange = { isExpandedModel = it }
        ) {
            TextField(
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
                expanded =isExpandedModel,
                onDismissRequest = { isExpandedModel = false },
            )
            {
                models.forEach { selectionOption ->
                    DropdownMenuItem(
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


        // State Dropdown
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
            value = yearMake.value,
            onValueChange = { yearMake.value = it; yearIsError = isYear(yearMake.value)},
            placeholder = { Text("Enter year of make") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = yearIsError,
            supportingText = {
                if (yearIsError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Year cannot be empty and should be integer numbers.Before 2025",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                if (yearIsError)
                    Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colorScheme.error)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // State Dropdown
        Text(
            text = "State",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 4.dp),
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold
        )
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = it }
        ) {
            TextField(
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .menuAnchor()
                    .focusProperties {
                        canFocus = false
                    }
                    .padding(bottom = 20.dp)
                    .fillMaxWidth(),
                readOnly = true,
                value = selectedState,
                onValueChange = {},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                }
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
            )
            {
                states.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                selectionOption, fontWeight = if
                                        (selectedState == selectionOption) FontWeight.Bold else null
                            )
                        },
                        onClick = {
                            selectedState = selectionOption
                            isExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }

        var valueIsLegit by remember { mutableStateOf(false) }
        valueIsLegit = !carPlateIsError && !brandIsError && !modelIsError && !yearIsError && yearMake.value.isNotEmpty()
        val context = LocalContext.current
        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                // Check if all inputs are valid and the plate ID is not empty
                if (valueIsLegit && !plateId.value.isNullOrEmpty()) {
                    // Check if the car plate already exists in the database
                    userViewModel.carPlateAlreadyAdded(plateId.value) { isAlreadyAdded ->
                        if (!isAlreadyAdded) {
                            // If the car plate is not already added, proceed to add the car
                            userViewModel.addCar(
                                plateId.value,
                                selectedBrand,
                                selectedModel,
                                yearMake.value.toInt(),
                                selectedState
                            ) { isSuccess ->
                                if (isSuccess) {
                                    // Car added successfully, handle success
                                    Toast.makeText(context, "Car added successfully.", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Routes.CarListScreen.value)
                                } else {
                                    // Failed to add car, handle failure
                                    Toast.makeText(context, "Failed to add car, please try again.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            // If the car plate is already added, display a toast message
                            Toast.makeText(context, "Car plate already exists, please use a different plate ID.", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    // If inputs are not valid or plate ID is empty
                    Toast.makeText(context, "Failed to add car, please check your inputs.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Car")
        }

    }
}

//Check if the user provided a valid car plate id,
// if provided car plate is valid, return true, otherwise return false.
fun isCarPlate(text: String): Boolean {
    return text.isEmpty() || !Regex("\\b[A-Za-z0-9]{6}\\b").matches(text)
}


//Check if the user provided the correct manufacturer year, if car is not made between 2000-2024,
// return true.
fun isYear(text: String): Boolean {
    if(text.isNotEmpty() && text.isDigitsOnly())
    {
        if(text.toInt() > 2024)
        {
             return true
        }
    }
    return text.isEmpty() || !Regex("\\b20[0-9]{2}\\b").matches(text)
}


