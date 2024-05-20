package com.example.autopark.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cars",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("ownerEmail"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Car(
    @PrimaryKey var plateId: String= "", // "4JD321" or "DAWNFM"  note: Rego can not exceed 6 digits
    var brand: String= "", //BMW
    val state: String= "", //VIC
    val modelName: String= "", // X5 M50
    val yearMake: Int= 0, //2003
    val ownerEmail: String= "" // Foreign key linking to the User's ID
)
