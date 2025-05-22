# AutoPark

AutoPark is an intelligent parking management application that helps users easily find, book, and pay for parking spaces.

## Features

- **User Authentication System**: Registration, login, and user profile management
- **Map Integration**: View nearby parking lots and real-time location
- **Parking Session Management**: Create, monitor, and end parking sessions
- **Vehicle Management**: Add, edit, and manage multiple vehicles
- **Payment System**: Manage bank cards and pay for parking fees
- **Parking History**: View past parking records and generate reports

## Technology Stack

- **Development Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **Map Services**: Google Maps API and Mapbox
- **Databases**:
  - Firebase Realtime Database (Cloud storage)
  - Room (Local storage)
- **Authentication**: Firebase Authentication
- **Network Requests**: Retrofit

## Project Structure

```
app/src/main/
├── java/com/example/autopark/
│   ├── data/           # Data processing classes
│   ├── database/       # Database related classes
│   ├── model/          # Data models
│   │   ├── User.kt     # User model
│   │   ├── Car.kt      # Vehicle model
│   │   ├── BankCard.kt # Bank card model
│   │   ├── Location.kt # Location model
│   │   └── ParkingSession.kt # Parking session model
│   ├── repository/     # Data repositories
│   ├── ui/             # UI related classes
│   ├── utils/          # Utility classes
│   ├── view/           # View components
│   │   ├── Home.kt     # Home page
│   │   ├── Login.kt    # Login page
│   │   ├── Register.kt # Registration page
│   │   └── ...         # Other pages
│   ├── viewmodel/      # ViewModel classes
│   ├── MainActivity.kt # Application entry point
│   ├── MainNavigation.kt # Navigation control
│   └── Routes.kt       # Route definitions
└── res/                # Resource files
```

## Setup and Running

### Prerequisites

- Latest version of Android Studio
- JDK 8 or higher
- Android SDK API level 34
- Google Maps API key
- Firebase project configuration

### Steps to Run

1. Clone the repository to your local machine
   ```
   git clone [repository URL]
   ```

2. Open the project in Android Studio

3. Ensure the `google-services.json` file is correctly configured and placed in the `app/` directory

4. Ensure the Google Maps API key is correctly configured in `AndroidManifest.xml`

5. Sync Gradle and build the project

6. Run the application on an emulator or physical device

## Required Permissions

- Internet access
- Fine location
- Coarse location

## Dependencies

Main external dependencies:

- Jetpack Compose UI components
- Google Firebase (Authentication, Realtime Database)
- Google Maps & Location Services
- Mapbox
- Retrofit & Gson
- Room Database
- MPAndroidChart (Chart generation)

## Developers

- Development Team: [Fill in developer information]

## Version Information

Current version: 0.1.0 α

## License

[Add license information] 
