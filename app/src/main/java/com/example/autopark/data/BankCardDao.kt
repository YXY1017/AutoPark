package com.example.autopark.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.autopark.model.BankCard



@Dao
interface BankCardDao {
    @Insert
    suspend fun insertCard(bankCard: BankCard):Long

    @Delete
    suspend fun deleteCard(card: BankCard): Int

    @Query("SELECT * FROM bank_cards WHERE userEmail = :email")
    suspend fun getAllCardsForUser(email: String): List<BankCard>
}