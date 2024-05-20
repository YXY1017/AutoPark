package com.example.autopark.view

import UserViewModel
import android.content.ContentValues.TAG
import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu

import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.platform.LocalContext
import com.example.autopark.model.User
import com.example.autopark.viewmodel.NavigationViewModel
import kotlinx.coroutines.launch
import java.util.Date


//A composable function to display the user's personal information and allows
// them to update it in this screen.
@RequiresApi(64)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(drawerState: DrawerState, navViewModel: NavigationViewModel, userViewModel: UserViewModel) {
    navViewModel.name.value
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
                title = { Text(text = "Profile") },
                colors = topAppBarColors(
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
                    }
                }
            )
        }
    ) { paddingValues ->
        // padding of the scaffold is enforced to be used
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        )
        {
//            ProfileDetails()
            val user = userViewModel.user.value
            ProfileDetails(user, userViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetails(user: User, userViewModel: UserViewModel)
{
    var firstName by remember { mutableStateOf(user.firstName ) }
    var lastName by remember { mutableStateOf(user.lastName) }
    var phone by remember { mutableStateOf(user.phone) }
    val context = LocalContext.current


    // For the date picker
    // Handle date safely
    val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ROOT)
    var dateOfBirth by remember {
        mutableStateOf(
            try {
                formatter.parse(user.dateOfBirth)?.time ?: System.currentTimeMillis()
            } catch (e: Exception) {
                Log.e(TAG, "Invalid date format or value: ${user.dateOfBirth}")
                System.currentTimeMillis()
            }
        )
    }

    var showDatePicker by remember { mutableStateOf(false) }
    // Get today's date as the maximum allowable date.
    val today = System.currentTimeMillis()

    // Remember the state of the date picker.
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateOfBirth)
    var isNameValid by remember { mutableStateOf(false) }
    var isSecondNameValid by remember { mutableStateOf(false) }
    var isPhoneValid by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Top
    )
    {
        Spacer(modifier = Modifier.height(10.dp))
        Box()
        {
            Row(
                Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically)
            {
                Image(
                    modifier = Modifier.size(100.dp),
                    imageVector = Icons.Filled.AccountCircle,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    contentDescription = null
                )
                Column(Modifier.padding(10.dp))
                {
                    Text(text = "$firstName $lastName", fontWeight = FontWeight.Bold, fontSize = 30.sp,
                        color = MaterialTheme.colorScheme.primary)
                    Text("Registered User", fontWeight = FontWeight.Light,
                        fontSize = 15.sp, color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
        Log.d(TAG, "User data in profile: $user")

        Spacer(modifier = Modifier.height(15.dp))
        Text("Email Address:", fontWeight = FontWeight.Bold, fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(10.dp))
        Text(user.email, Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(15.dp))

        Text("First Name:", fontWeight = FontWeight.Bold, fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(10.dp))
        TextField(
            value = firstName ,
            onValueChange = {newValue -> firstName = newValue; isNameValid = !isValidName(firstName)},
            Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = isNameValid,
            supportingText = {
                if (isNameValid) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Invalid Name.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                if (isNameValid)
                    Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colorScheme.error)
            }
        )

        Spacer(modifier = Modifier.height(15.dp))
        // Editable name field
        Text("Last Name:",fontWeight = FontWeight.Bold,fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(10.dp))
        TextField(
            value = lastName,
            onValueChange = { newValue -> lastName = newValue; isSecondNameValid = !isValidName(lastName)},
            Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = isSecondNameValid,
            supportingText = {
                if (isSecondNameValid) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Invalid Name.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                if (isSecondNameValid)
                    Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colorScheme.error)
            }
        )
        Spacer(modifier = Modifier.height(15.dp))

        Text("Phone Number:", fontWeight = FontWeight.Bold, fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(10.dp))
        TextField(
            value = phone,
            onValueChange = {newValue -> phone = newValue; isPhoneValid = !isValidPhoneNumber(phone)},
            Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = isPhoneValid,
            supportingText = {
                if (isPhoneValid) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Invalid Phone Number, only australian phone number are supported.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                if (isPhoneValid)
                    Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colorScheme.error)
            })
        Spacer(modifier = Modifier.height(15.dp))

        // Date of Birth Field
        Text("Date of Birth:", fontWeight = FontWeight.Bold, fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(10.dp))
        TextField(
            value = formatter.format(Date(dateOfBirth)),
            onValueChange = { /* Read Only Field */ },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = "Select Date"
                    )
                }
            }
        )
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        if (datePickerState.selectedDateMillis!! <= today) {
                            dateOfBirth = datePickerState.selectedDateMillis!!
                            showDatePicker = false
                        } else {
                            Toast.makeText(context, "Cannot select a future date!", Toast.LENGTH_SHORT).show()
                            showDatePicker = false
                        }
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Spacer(modifier = Modifier.height(15.dp))
        val userInput = !isNameValid && !isSecondNameValid && !isPhoneValid
        // Save Button
        Button(
            onClick = {
                if(userInput)
                //Check if all conditions are met.
                {
                    userViewModel.updateUserToRoomDatabase(user.id,
                        firstName = firstName,
                        lastName = lastName,
                        email = user.email,
                        phone = phone,
                        dateOfBirth = formatter.format(Date(dateOfBirth)),
                        password = user.password){ success ->
                        if (success) {
                            Toast.makeText(context, "User data updated successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to update user data.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else
                {
                    //If some of the conditions are not met, give the user a warning.
                    Toast.makeText(context, "Failed to update user data. Please check your inputs.", Toast.LENGTH_SHORT).show()
                }
                      },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text("Save Changes")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row {
            Text("Need Help?", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(10.dp))
            Text("Contact Supports", fontStyle = FontStyle.Italic, textDecoration =
            TextDecoration.Underline, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
        }
    }
}
