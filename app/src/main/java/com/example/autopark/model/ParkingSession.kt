package com.example.autopark.model
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "parking_sessions",
    indices = [Index(value = ["userEmail", "carId", "creditCardId","sessionId"])],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("userEmail"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class ParkingSession(
    @PrimaryKey var sessionId: String,
    val startTime: String,
    val endTime: String,
    val userEmail: String,
    val carId: String,
    val creditCardId: String,
    val cost: Double,
    val locationId: Int
) {
    // No-argument constructor required by Firebase
    constructor() : this(
        sessionId = "",
        startTime = "",
        endTime = "",
        userEmail = "",
        carId = "",
        creditCardId = "",
        cost = 0.0,
        locationId = 0
    )
}