package com.example.autopark.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.autopark.model.ParkingSession
import com.example.autopark.model.User


@Dao
interface ParkingSessionDao {
    @Insert
    suspend fun insertParkingSession(parkingSession: ParkingSession):Long

    @Delete
    suspend fun deleteParkingSession(parkingSession: ParkingSession): Int

    @Query("SELECT * FROM parking_sessions WHERE sessionId = :sessionId")
    suspend fun getParkingSessionBySessionId(sessionId: Int): List<ParkingSession>
}