package com.example.autopark.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.autopark.data.BankCardDao
import com.example.autopark.data.ParkingSessionDao
import com.example.autopark.data.UserDao
import com.example.autopark.model.User
import com.example.autopark.utils.Converters
import com.example.autopark.model.BankCard
import com.example.autopark.model.ParkingSession
import com.example.autopark.viewmodel.ParkingSessionViewModel

@Database(entities = [User::class, BankCard::class, ParkingSession::class], version = 13, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    //define the rest of the database schema here
    abstract fun userDao(): UserDao
    abstract fun bankCardDao(): BankCardDao

    abstract fun parkingSessionDao(): ParkingSessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "autopark_db"
                ).fallbackToDestructiveMigration() // Handle migrations
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
