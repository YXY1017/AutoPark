package com.example.autopark.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bank_cards",
    indices = [Index(value = ["userEmail"])],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("email"),
            childColumns = arrayOf("userEmail"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class BankCard(
    @PrimaryKey(autoGenerate = true) val cardId: Int = 0,
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String,
    val userEmail: String,
)