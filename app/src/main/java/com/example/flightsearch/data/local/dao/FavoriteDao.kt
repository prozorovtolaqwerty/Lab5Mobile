package com.example.flightsearch.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.flightsearch.data.local.FavoriteRow
import com.example.flightsearch.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query(
        """
        SELECT f.id AS id,
               f.departure_code AS departureCode,
               f.destination_code AS destinationCode,
               d.name AS departureName,
               a.name AS destinationName
        FROM favorite AS f
        JOIN airport AS d ON d.iata_code = f.departure_code
        JOIN airport AS a ON a.iata_code = f.destination_code
        ORDER BY f.id DESC
        """
    )
    fun observeAll(): Flow<List<FavoriteRow>>

    @Query(
        """
        SELECT * FROM favorite
        WHERE departure_code = :departureCode
          AND destination_code = :destinationCode
        LIMIT 1
        """
    )
    suspend fun find(departureCode: String, destinationCode: String): FavoriteEntity?

    @Insert
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorite WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query(
        """
        DELETE FROM favorite
        WHERE departure_code = :departureCode
          AND destination_code = :destinationCode
        """
    )
    suspend fun deletePair(departureCode: String, destinationCode: String)
}
