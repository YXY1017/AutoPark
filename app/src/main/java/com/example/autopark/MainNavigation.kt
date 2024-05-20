package com.example.autopark

import LocationViewModel
import UserViewModel
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.autopark.view.About
import com.example.autopark.view.AddCarScreen
import com.example.autopark.view.AddCardScreen
import com.example.autopark.view.CarListScreen
import com.example.autopark.view.ConfirmationScreen
import com.example.autopark.view.DrawerContent
import com.example.autopark.view.DuringSessionScreen
import com.example.autopark.view.EditCarScreen
import com.example.autopark.view.HistoryScreen
import com.example.autopark.view.Home
import com.example.autopark.view.LoginScreen
import com.example.autopark.view.PaymentScreen
import com.example.autopark.view.Profile
import com.example.autopark.view.Register
import com.example.autopark.view.ReportScreen
import com.example.autopark.view.SettingsScreen
import com.example.autopark.view.menus
import com.example.autopark.viewmodel.MapViewModel
import com.example.autopark.viewmodel.NavigationViewModel
import com.example.autopark.viewmodel.ParkingSessionViewModel
import com.github.mikephil.charting.utils.Utils.init
import kotlinx.coroutines.launch
@Composable
fun MainNavigation (navViewModel: NavigationViewModel,userViewModel: UserViewModel,mapViewModel: MapViewModel,locationViewModel:LocationViewModel,parkingSessionViewModel: ParkingSessionViewModel) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(
        initialValue =
        DrawerValue.Closed
    )
    //Checks if the user has logged in or not. If not the first page will be login page,
    // if the user has logged in, show homepage instead.
    val startDestination = if (userViewModel.isLoggedIn()) Routes.Home.value else Routes.Login.value
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerSheet {
                userViewModel.loadCurrentUser()
                DrawerContent(menus, userViewModel) { route ->
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    navController.navigate(route)
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(0.dp)
        ) {
            composable(Routes.Home.value) {
                Home(drawerState, navViewModel,navController, mapViewModel,locationViewModel,parkingSessionViewModel,userViewModel)
            }
            composable(Routes.Payments.value) {
                PaymentScreen(userViewModel,drawerState, navController)//navViewModel,navController)//navViewModel,
            }
            composable(Routes.Setting.value) {
                SettingsScreen(drawerState , navController )
            }
            composable(Routes.Profile.value) {
                Profile(drawerState, navViewModel, userViewModel)
            }
            composable(Routes.About.value) {
                About(navViewModel, drawerState)
            }
            composable(Routes.Register.value){
                Register(navController,userViewModel)
            }
            composable(Routes.AddCard.value)
            {
                AddCardScreen(navViewModel, navController,userViewModel)
            }
            composable(Routes.Login.value)
            {
                LoginScreen(navController,userViewModel)
            }
            composable(Routes.ConfirmationScreen.value){
                ConfirmationScreen(drawerState,navViewModel,navController,locationViewModel,parkingSessionViewModel,userViewModel)
            }

            composable(Routes.DuringSession.value){
                DuringSessionScreen(drawerState,navViewModel,navController,parkingSessionViewModel, locationViewModel)
            }

            composable(Routes.CarListScreen.value){
                CarListScreen(drawerState,navViewModel,navController,userViewModel)
            }
            composable(Routes.ReportScreen.value){
                ReportScreen(drawerState,parkingSessionViewModel,locationViewModel)
            }
            composable(Routes.HistoryScreen.value){
                HistoryScreen(drawerState, parkingSessionViewModel, userViewModel, locationViewModel)
            }

            composable(Routes.AddCarScreen.value){
                AddCarScreen(navViewModel, navController,userViewModel)
            }

            composable(
                route = Routes.EditCarScreen.value,
                arguments = listOf(navArgument("plateId") { type = NavType.StringType })
            ) { backStackEntry ->
                val plateId = backStackEntry.arguments?.getString("plateId")
                if (plateId != null) {
                    EditCarScreen(plateId = plateId, navController = navController, userViewModel = userViewModel)
                } else {
                    Log.e("Navigation", "Plate ID is missing in the navigation argument.")
                }
            }

        }
    }
}