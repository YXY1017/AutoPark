package com.example.autopark.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
@Entity(tableName = "locations")
data class Location(
    @PrimaryKey(autoGenerate = true) val locationId: Int,
    val areaCode: String, //3070
    val name: String, // Monash-01
    val address: String, // 308 Woodside road
    val longitude: Double,
    val latitude: Double,
    val rateDollarPerHour: Double, //4.5 per hour
    val maxStayInHours: Int, // 2hour,3hour,4hour... up to you
    val timeStart: String, // Charged parking starts at "HH:mm" format
    val timeEnd: String // Charged parking ends at "HH:mm" format
)
*/


@Entity(tableName = "locations")
data class Location(
    @PrimaryKey(autoGenerate = true) val locationId: Int = 0,
    val areaCode: String = "",
    val name: String = "",
    val address: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val rateDollarPerHour: Double = 0.0,
    val maxStayInHours: Int = 0,
    val timeStart: String = "",
    val timeEnd: String = ""
)

