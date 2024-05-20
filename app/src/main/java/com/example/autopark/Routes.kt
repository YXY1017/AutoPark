package com.example.autopark

enum class Routes(val value: String) {
    Home("Home"),
    Payments("Payments"),
    Profile("Profile"),
    About("About"),
    Register("Register"),
    AddCard("Cards"),
    Login("Login"),
    Setting("Settings"),
    ConfirmationScreen("Confirmation"),
    DuringSession("DuringSession"),
    CarListScreen("CarList"),
    ReportScreen("Report"),
    HistoryScreen("History"),
    AddCarScreen("AddCar"),
    EditCarScreen("EditCar/{plateId}")
}