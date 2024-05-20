package com.example.autopark.view

import UserViewModel
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.autopark.Routes
import com.example.autopark.viewmodel.NavigationViewModel
import com.example.autopark.model.BankCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(navViewModel: NavigationViewModel,
                  navController: NavController,
                  userViewModel: UserViewModel) {
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
                title = { Text(text = "Add New Cards") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =
                    MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Routes.Payments.value)
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

            payments(userViewModel = userViewModel,navController = navController)
        }
    }
}

@RequiresApi(64)
@Composable
fun payments(userViewModel: UserViewModel, navController: NavController) {
    val cardNumberLength = 16
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    val cards by remember { mutableStateOf(listOf<BankCard>()) }
    var showingValidationDialog by remember { mutableStateOf(false) }
    val cardNumberFocusRequester = remember { FocusRequester() }
    val expiryDateFocusRequester = remember { FocusRequester() }
    val cvvFocusRequester = remember { FocusRequester() }
    var cardNumberIsLegit by remember { mutableStateOf(false) }
    var cvvIsLegit by remember { mutableStateOf(false) }
    var cardYearIsLegit by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Manage Your Bank Cards", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        // Display existing cards
        cards.forEach { card ->
            CardInfo(card)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Input for new card details
        OutlinedTextField(
            value = cardNumber,
            onValueChange = {cardNumber = it; cardNumberIsLegit = isCreditCard(cardNumber)
                if (cardNumber.length == cardNumberLength) {
                    expiryDateFocusRequester.requestFocus()
                }
                            },
            label = { Text("Card Number (16 digits)") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(cardNumberFocusRequester),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { expiryDateFocusRequester.requestFocus() }),
            isError = cardNumberIsLegit,
            supportingText = {
                if (cardNumberIsLegit) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Invalid Card number.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                if (cardNumberIsLegit)
                    Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colorScheme.error)
            }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = expiryDate,
            onValueChange = { input ->
                val digits = input.filter { it.isDigit() }
                expiryDate = when {
                    // If the user deletes the slash, keep the numbers as they are
                    digits.length <= 2 -> digits
                    // Avoid adding a slash if the user is backspacing over the slash
                    digits.length == 3 && expiryDate.length == 5 -> digits.substring(0, 2)
                    // Only add a slash after the user has entered four digits
                    digits.length == 4 && !expiryDate.contains('/') -> "${digits.substring(0, 2)}/${digits.substring(2)}"
                    // If the user tries to enter more than four digits, keep the old value
                    digits.length > 4 -> expiryDate
                    else -> input
                }
                // Move the focus to the CVV field if the expiry date is complete
                if (expiryDate.length == 5) {
                    cvvFocusRequester.requestFocus()
                }
                cardYearIsLegit = isExpiryDate(expiryDate)
            },
            label = { Text("Expiry Date (MM/YY)") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(expiryDateFocusRequester),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { cvvFocusRequester.requestFocus() }),
            isError = cardYearIsLegit,
            supportingText = {
                if (cardYearIsLegit) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Invalid expiry date.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                if (cardYearIsLegit)
                    Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colorScheme.error)
            }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = cvv,
            onValueChange = { if (it.length <= 3) cvv = it; cvvIsLegit = isCVV(cvv) },
            label = { Text("CVV") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(cvvFocusRequester),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { expiryDateFocusRequester.requestFocus()}),
            isError = cvvIsLegit,
            supportingText = {
                if (cvvIsLegit) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Invalid CVV.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                if (cvvIsLegit)
                    Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colorScheme.error)
            }
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Validation dialog
        if (showingValidationDialog) {
            AlertDialog(
                onDismissRequest = { showingValidationDialog = false },
                confirmButton = {
                    TextButton(onClick = { showingValidationDialog = false }) {
                        Text("OK")
                    }
                },
                title = { Text("Invalid Input") },
                text = { Text("Please check your card details and try again.") }
            )
        }
        val context = LocalContext.current
        val isInputLegit = !cardNumberIsLegit && !cardYearIsLegit && !cvvIsLegit
        // Add card button
        Button(
            onClick = {
                if(isInputLegit)
                {
                    if (validateCardInput(cardNumber, expiryDate, cvv)) {
                        val userEmail = userViewModel.getUserEmail()
                        val bankCard = BankCard(
                            cardNumber = cardNumber,
                            expiryDate = expiryDate,
                            cvv = cvv,
                            userEmail = userEmail
                        )

                        userViewModel.addCard(bankCard) { success ->
                            if (success) {
                                cardNumber = ""
                                expiryDate = ""
                                cvv = ""
                                navController.popBackStack()

                            } else {
                                showingValidationDialog = true
                            }
                        }
                    } else {
                        showingValidationDialog = true
                    }
                }
                else
                {
                    Toast.makeText(context, "Failed to add card, please check your inputs.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Card")
        }
    }
}


@Composable
fun CardInfo(card: BankCard) {
    Text("Card ending in **** ${card.cardNumber.takeLast(4)}")
    Spacer(modifier = Modifier.height(4.dp))
}

//Validate if the user provided the correct card info.
fun validateCardInput(cardNumber: String, expiryDate: String, cvv: String): Boolean {
    val cardNumberIsValid = cardNumber.length == 16 && cardNumber.all { it.isDigit() }
    val expiryDateIsValid = expiryDate.matches(Regex("\\d{2}/\\d{2}"))
    val cvvIsValid = cvv.length == 3 && cvv.all { it.isDigit() }
    return cardNumberIsValid && expiryDateIsValid && cvvIsValid
}


//Validate if the user provided the correct card number, if the card starts with 2/4/5(Visa & Master) and is a 16 digit number,
// return false.
fun isCreditCard(cardNumber: String): Boolean {
    val regex = "^[245][0-9]{15}$".toRegex()
    return !regex.matches(cardNumber)
}


//Validate if the user provided a valid expiry date, if not return true.
fun isExpiryDate(expiryDate: String): Boolean {
    val regex = "^(0[1-9]|1[0-2])/\\d{2}$".toRegex()
    return !regex.matches(expiryDate)
}

//Validate if the user provided a legit CVV, if not return true.
fun isCVV(cvv: String): Boolean {
    val regex = "^\\d{3}$".toRegex()
    return !regex.matches(cvv)
}