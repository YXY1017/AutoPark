package com.example.autopark.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.autopark.Routes

data class DrawerMenu(val icon: ImageVector, val title: String, val
route: String)

val menus = arrayOf(
    DrawerMenu(Icons.Filled.Home, "Home", Routes.Home.value),
    DrawerMenu(Icons.Filled.DirectionsCar, "Vehicle Management", Routes.CarListScreen.value),
    DrawerMenu(Icons.Filled.CreditCard, "Payment Methods", Routes.Payments.value),
    DrawerMenu(Icons.Filled.CalendarMonth, "History", Routes.HistoryScreen.value),
    DrawerMenu(Icons.Filled.AutoGraph, "Report", Routes.ReportScreen.value),
    DrawerMenu(Icons.Filled.Settings, "Settings", Routes.Setting.value),
    DrawerMenu(Icons.Filled.Info, "About", Routes.About.value)
)
