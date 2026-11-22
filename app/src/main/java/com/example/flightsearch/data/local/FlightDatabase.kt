package com.example.flightsearch.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flightsearch.data.local.dao.AirportDao
import com.example.flightsearch.data.local.dao.FavoriteDao
import com.example.flightsearch.data.local.entity.AirportEntity
import com.example.flightsearch.data.local.entity.FavoriteEntity

@Database(
    entities = [AirportEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class FlightDatabase : RoomDatabase() {
    abstract fun airportDao(): AirportDao

    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var instance: FlightDatabase? = null

        fun getInstance(context: Context): FlightDatabase {
            return instance ?: synchronized(this) {
                instance ?: build(context).also { instance = it }
            }
        }

        private fun build(context: Context): FlightDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                FlightDatabase::class.java,
                "flight_search.db",
            )
                .createFromAsset("database/flight_search.db")
                .build()
        }
    }
}
