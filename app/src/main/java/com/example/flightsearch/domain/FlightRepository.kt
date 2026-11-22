package com.example.flightsearch.domain

import com.example.flightsearch.domain.model.Airport
import com.example.flightsearch.domain.model.FavoriteFlight
import com.example.flightsearch.domain.model.Flight
import kotlinx.coroutines.flow.Flow

interface FlightRepository {
    suspend fun searchAirports(text: String): List<Airport>

    suspend fun airportByCode(code: String): Airport?

    fun observeFlights(iataCode: String): Flow<List<Flight>>

    fun observeFavorites(): Flow<List<FavoriteFlight>>

    suspend fun addFavorite(departureCode: String, destinationCode: String)

    suspend fun removeFavorite(departureCode: String, destinationCode: String)

    suspend fun removeFavoriteById(id: Int)
}
