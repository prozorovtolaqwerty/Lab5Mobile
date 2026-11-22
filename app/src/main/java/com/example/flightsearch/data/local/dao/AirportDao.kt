package com.example.flightsearch.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.flightsearch.data.local.FlightRow
import com.example.flightsearch.data.local.entity.AirportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query(
        """
        SELECT * FROM airport
        WHERE iata_code LIKE '%' || :text || '%'
           OR name LIKE '%' || :text || '%'
        ORDER BY passengers DESC
        """
    )
    suspend fun search(text: String): List<AirportEntity>

    @Query("SELECT * FROM airport WHERE iata_code = :code COLLATE NOCASE LIMIT 1")
    suspend fun getByCode(code: String): AirportEntity?

    @Query(
        """
        SELECT d.iata_code AS departureCode,
               d.name AS departureName,
               a.iata_code AS arrivalCode,
               a.name AS arrivalName
        FROM airport AS d
        JOIN airport AS a ON a.iata_code != d.iata_code
        WHERE d.iata_code = :code
        ORDER BY a.passengers DESC
        """
    )
    fun observeFlights(code: String): Flow<List<FlightRow>>
}
