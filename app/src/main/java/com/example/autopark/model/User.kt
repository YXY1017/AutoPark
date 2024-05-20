package com.example.autopark.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
    )
data class User(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @PrimaryKey var id: String, // Set to Firebase unique key
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val phone: String
)

