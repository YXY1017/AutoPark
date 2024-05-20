package com.example.autopark.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.autopark.model.Location
import com.example.autopark.model.ParkingSession
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter
import kotlin.math.round


class ParkingSessionViewModel(application: Application) : AndroidViewModel(application) {

    private var locationId: Int = 0

    //retreive from location.ratePerHour
    private var ratePerHour: Double = 0.0

    private var currentSession: ParkingSession? = null

    private val _userParkingSessions = MutableLiveData<List<ParkingSession>>()
    val userParkingSessions: LiveData<List<ParkingSession>> = _userParkingSessions

    //used for dialog information display
    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location



    // This will ensure maxStayInHours is safely handled and a default value is given if Location is null.

    fun setRatePerHous(rate:Double){
        this.ratePerHour=rate
    }

    fun setLocationId(locationId: Int) {
        this.locationId = locationId
    }


    fun getLocation(): Int {
        //used for display location id in the confirmation dialog, current showing location id, but if location area code could be better
        //todo return location name instead of location id
        return locationId
    }

    fun getLocationInstance() {
        val locationId = getLocation()  // Assuming getLocation() retrieves the locationId you want to query
        val databaseRef = FirebaseDatabase.getInstance().getReference("locations")

        databaseRef.child(locationId.toString()).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val location = task.result?.getValue(Location::class.java)
                _location.value = location
                Log.d("ParkingSessionViewModel", "Successfully retrieved location with ID $locationId")
            } else {
                val errorMessage = task.exception?.message ?: "Unknown error"
                Log.e("ParkingSessionViewModel", "Error retrieving location: $errorMessage")
                _location.value = null
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startParking(email: String, plate: String, cardId:String) {
        val sessionId = FirebaseDatabase.getInstance().getReference("parkingSessions").push().key ?: return
        val startTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        currentSession = ParkingSession(
            sessionId = sessionId,
            startTime = startTime,
            endTime = "Ongoing",
            userEmail = email,
            carId = plate,
            creditCardId = cardId,
            cost = 0.0,
            locationId = locationId
        )
        currentSession?.let {
            FirebaseDatabase.getInstance().getReference("parkingSessions").child(it.sessionId).setValue(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun stopParking() {
        val endTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        currentSession?.let { session ->
            val durationInSeconds = ChronoUnit.SECONDS.between(LocalDateTime.parse(session.startTime), LocalDateTime.now())
            val cost = calculateCost(durationInSeconds)
            val updatedSession = session.copy(
                endTime = endTime,
                cost = cost
            )
            FirebaseDatabase.getInstance().getReference("parkingSessions").child(session.sessionId).setValue(updatedSession)
            currentSession = updatedSession
        }
    }


    //Get all parking sessions the user have in the firebase.
    fun getAllUserParkingSessions(email: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("parkingSessions")
        databaseRef.orderByChild("userEmail").equalTo(email).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val sessions = mutableListOf<ParkingSession>()
                task.result?.children?.forEach { snapshot ->
                    val session = snapshot.getValue(ParkingSession::class.java)
                    //Load sessions from firebase.
                    session?.let { sessions.add(it) }
                }
                _userParkingSessions.value = sessions
                Log.d("ParkingSessionViewModel", "Successfully retrieved ${sessions.size} sessions for $email")
            } else {
                val errorMessage = task.exception?.message ?: "Unknown error"
                Log.e("ParkingSessionViewModel", "Error retrieving sessions: $errorMessage")
                _userParkingSessions.value = emptyList()
            }
        }
    }

    private fun calculateCost(durationInMinutes: Long): Double {
        val durationInHours = durationInMinutes / 3600.0
        //PRINT LOG
        Log.d("Minuts","$durationInMinutes")
        Log.d("Duration time","$durationInHours")
        Log.d("Rate","$ratePerHour")

        val cost = durationInHours * ratePerHour

        return round(cost * 100) / 100
    }

    fun getFinalSessionToDisplay(): ParkingSession? {
        return currentSession
    }





}