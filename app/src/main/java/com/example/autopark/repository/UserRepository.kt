package com.example.autopark.repository

import com.example.autopark.data.UserDao
import com.example.autopark.model.User
import com.example.autopark.data.BankCardDao
import com.example.autopark.model.BankCard

class UserRepository(private val userDao: UserDao, private val bankCardDao: BankCardDao) {
    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun updateUser(user: User) {
        val numberOfUpdatedRows = userDao.updateUser(user)
        if (numberOfUpdatedRows > 0) {
            println("Update successful: $numberOfUpdatedRows rows updated.")
        } else {
            println("Update failed: User not found.")
        }
    }

    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)

    suspend fun insertCard(bankCard: BankCard) = bankCardDao.insertCard(bankCard)
    //suspend fun getAllCardsForUser(userEmail:String) = bankCardDao.getAllCardsForUser(userEmail)
    suspend fun getAllCardsForUser(userEmail: String): List<BankCard> {
        return bankCardDao.getAllCardsForUser(userEmail) ?: emptyList()
    }

    suspend fun deleteCard(card: BankCard): Int {
        return bankCardDao.deleteCard(card)
    }
}
