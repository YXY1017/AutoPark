package com.example.autopark.view

import UserViewModel
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create

import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog

import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.autopark.Routes
import com.example.autopark.model.User
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Calendar
import java.util.Date
import java.util.Locale


//A composable function to display the register page.
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(0)
@Composable
fun Register(navController: NavController,userViewModel: UserViewModel) {

    var firstName by remember { mutableStateOf("") }
    var secondName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }
    var password1 by remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()
    calendar.set(2024, 0, 1) // month (0) is January
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    var showDatePicker by remember {
        mutableStateOf(false)
    }
    var selectedDate by remember {
        mutableStateOf(calendar.timeInMillis)
    }
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
    var dateOfBrith = formatter.format(Date(selectedDate))
    val context = LocalContext.current
    var emailIsLegit by remember { mutableStateOf(false) }
    var nameIsLegit by remember { mutableStateOf(false) }
    var secondNameIsLegit by remember { mutableStateOf(false) }
    var passwordIsLegit by remember { mutableStateOf(false) }
    var passwordMatches by remember { mutableStateOf(false) }
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
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Routes.Login.value)
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                    }  },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =
                    MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),  title = { Text(text = "Register") },
            )
        },

        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding))
            {

                Column(modifier = Modifier
                    .absolutePadding(left = 20.dp)
                    .absolutePadding(right = 20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),) {
                    Text(
                        text = "Email Address",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp),
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = userEmail,
                        onValueChange = { userEmail = it; emailIsLegit = !isValidEmail(userEmail) },
                        singleLine = true,
                        trailingIcon = {
                            if (userEmail.isNotEmpty()) {
                                IconButton(onClick = { userEmail = "" }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        isError = emailIsLegit,
                        supportingText = {
                            if (emailIsLegit) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Invalid Email Address.",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                    Text(
                        text = "First Name",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp),
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = firstName,
                        onValueChange = { firstName = it; nameIsLegit = !isValidName(firstName) },
                        singleLine = true,
                        trailingIcon = {
                            if (firstName.isNotEmpty()) {
                                IconButton(onClick = { firstName = "" }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        isError = nameIsLegit,
                        supportingText = {
                            if (nameIsLegit) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Invalid Name.",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                    Text(
                        text = "Second Name",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp),
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = secondName,
                        onValueChange = { secondName = it; secondNameIsLegit = !isValidName(secondName) },
                        singleLine = true,
                        trailingIcon = {
                            if (secondName.isNotEmpty()) {
                                IconButton(onClick = { secondName = "" }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        isError = secondNameIsLegit,
                        supportingText = {
                            if (secondNameIsLegit) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Invalid Name.",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                    Text(
                        text = "Password",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp),
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password,
                        onValueChange = { password = it; passwordIsLegit = !isValidPassword(password) },
                        singleLine = true,
                        trailingIcon = {
                            if (password.isNotEmpty()) {
                                IconButton(onClick = { password = "" }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = passwordIsLegit,
                        supportingText = {
                            if (passwordIsLegit) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Invalid Password. At least 1 Uppercase, At least 1 digit, length must be at least 8 ",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                    Text(
                        text = "Confirm Password",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp),
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password1,
                        onValueChange = { password1 = it; passwordMatches = password1 != password},
                        singleLine = true,
                        trailingIcon = {
                            if (password1.isNotEmpty()) {
                                IconButton(onClick = { password1 = "" }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = passwordMatches,
                        supportingText = {
                            if (passwordMatches) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Password does not match.",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                    if (showDatePicker){
                        DatePickerDialog(
                            onDismissRequest = {showDatePicker = false},
                            confirmButton = {
                                TextButton(onClick = {
                                    showDatePicker = false
                                    selectedDate = datePickerState.selectedDateMillis!!
                                })
                                {Text(text = "OK")}
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showDatePicker = false
                                }) {
                                    Text(text = "Cancel")
                                }
                            })
                        {
                            DatePicker(state = datePickerState)
                        }
                    }
                    Text(
                        text = "Date Of Birth",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp),
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = dateOfBrith,
                        onValueChange = { dateOfBrith = it },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Outlined.DateRange,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            //Check if all the conditions are met.
            val validUserInput = !emailIsLegit && !nameIsLegit && !secondNameIsLegit && !passwordIsLegit && !passwordMatches
            Log.d("Results", "${!emailIsLegit}, ${!nameIsLegit}, ${!secondNameIsLegit}, ${!passwordIsLegit}, ${!passwordMatches}")


            ExtendedFloatingActionButton(
                onClick = {
                    if(validUserInput){
                        //If all conditions are met, register account.
                        userViewModel.isEmailRegistered(userEmail) { isRegistered ->
                            Log.d("FirebaseAuth", "isRegistered: $isRegistered for email: $userEmail")
                            if (isRegistered) {
                                Toast.makeText(context, "Email is already in use. Please use a different email.", Toast.LENGTH_LONG).show()
                            } else {
                                val userPhone = userViewModel.getPhoneNumber()
                                val user = User("", userEmail, password, firstName, secondName, dateOfBrith, userPhone)
                                userViewModel.registerUser(user) { success ->
                                    if (success) {
                                        navController.navigate(Routes.Home.value)
                                        Log.d("Register results", "Success")
                                    } else {
                                        //Send out a toast message to asking the user check their details again.
                                        Log.e("Register results", "Failed")
                                        Toast.makeText(context, "Failed to register, please try again.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                    else{
                        Toast.makeText(context, "Failed to register, please check your inputs.", Toast.LENGTH_SHORT).show()
                    }
                },
                icon = { Icon(Icons.Default.Create, "Register") },
                text = { Text(text = "Register") }
            )



        }
    )
}

//Validates if the user inputs a correct email address, if not return false.
fun isValidEmail(email: String): Boolean {
    return email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

//Validates if the user provides the correct name. (Without numbers and symbols.)
fun isValidName(name: String): Boolean {
    return Regex("^[a-zA-Z]+$").matches(name)
}


//Validates if the user uses a correct password. Must include a uppercase letter, a lowercase
// letter, and a number, and have to be longer than 8 characters.
fun isValidPassword(password: String): Boolean {
    if (password.length < 8) return false

    //Set up all the values.
    var hasUppercase = false
    var hasLowercase = false
    var hasDigit = false

    //Check if all the conditions are met.
    for (char in password) {
        when {
            char.isUpperCase() -> hasUppercase = true
            char.isLowerCase() -> hasLowercase = true
            char.isDigit() -> hasDigit = true
        }
    }

    return hasUppercase && hasLowercase && hasDigit
}

//Validates if the user provided a correct australian phone number.
fun isValidPhoneNumber(phone: String): Boolean {
    val phonePattern = Regex("^04\\d{8}$")
    return phonePattern.matches(phone)
}