package com.example.autopark.repository

import com.example.autopark.data.BankCardDao
import com.example.autopark.data.ParkingSessionDao
import com.example.autopark.data.UserDao
import com.example.autopark.model.BankCard
import com.example.autopark.model.ParkingSession
import com.example.autopark.model.User

class ParkingSessionRepository(private val parkingSessionDao: ParkingSessionDao) {
    suspend fun insertParkingSession(parkingSession: ParkingSession) = parkingSessionDao.insertParkingSession(parkingSession)
    suspend fun getParkingSessionBySessionId(sessionId: Int) = parkingSessionDao.getParkingSessionBySessionId(sessionId)
}