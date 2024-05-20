package com.example.autopark.view


import UserViewModel
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.material3.*

import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.example.autopark.R
import com.example.autopark.Routes
import com.example.autopark.model.BankCard
import kotlinx.coroutines.launch


data class BankCard(
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String
)


//A composable function to display all the cards added by the user.
@RequiresApi(64)
@Composable
fun PaymentScreen(userViewModel: UserViewModel,
                  drawerState: DrawerState,
                  //navViewModel: NavigationViewModel,
                  navController: NavController) {
    val userEmail = userViewModel.getUserEmail()

    LaunchedEffect(userEmail) {
        userViewModel.getCardList(userEmail) {
        }
    }
    Scaffold(
        topBar = {
            TopAppBarComponent(drawerState = drawerState, navController = navController)
        }
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues))
        {
            //DisplayCards(userViewModel.bankCards)
            DisplayCards(bankCards = userViewModel.bankCards, userViewModel = userViewModel)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarComponent(drawerState: DrawerState, navController: NavController) {
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
        title = { Text(text = "Payment Methods") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            IconButton(onClick = {
                coroutineScope.launch { drawerState.open() }
            }) {
                Icon(Icons.Filled.Menu,
                    contentDescription = "Open navigation drawer",
                    tint = MaterialTheme.colorScheme.primary)
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate(Routes.AddCard.value) }) {
                Icon(
                    imageVector = Icons.Filled.AddCard,
                    contentDescription = "Add a new card",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Composable
fun DisplayCards(bankCards: List<BankCard>, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(bankCards) { card ->
            BankCardItem(
                bankCard = card,
                onDelete = {
                    userViewModel.removeCard(card) { success ->
                        scope.launch {
                            if (success) {
                                Toast.makeText(context, "Card deleted successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to delete card", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun BankCardItem(bankCard: BankCard, onDelete: () -> Unit) {
    val cardType = when (bankCard.cardNumber.firstOrNull()) {
        '4' -> "VISA"
        '2', '5' -> "Master"
        else -> "Unknown"
    }
    Box(Modifier.padding(25.dp, 10.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Using the bankCard.type property to determine the image to show
            when (cardType) {
                "VISA" -> {
                    Image(
                        modifier = Modifier.size(55.dp),
                        painter = painterResource(id = R.drawable.visa),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                        contentDescription = "VISA Card"
                    )
                }
                "Master" -> {
                    Image(
                        modifier = Modifier.size(55.dp),
                        painter = painterResource(id = R.drawable.master),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                        contentDescription = "MasterCard"
                    )
                }
                else -> {
                }
            }
            Spacer(modifier = Modifier.width(30.dp))

            Column {
                Text(
                    text = bankCard.cardNumber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = bankCard.expiryDate,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = { onDelete() }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}











