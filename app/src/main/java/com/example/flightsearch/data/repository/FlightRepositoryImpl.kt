package com.example.flightsearch.data.repository

import com.example.flightsearch.data.local.FlightDatabase
import com.example.flightsearch.data.local.entity.FavoriteEntity
import com.example.flightsearch.domain.FlightRepository
import com.example.flightsearch.domain.model.Airport
import com.example.flightsearch.domain.model.FavoriteFlight
import com.example.flightsearch.domain.model.Flight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FlightRepositoryImpl(
    private val database: FlightDatabase,
) : FlightRepository {

    override suspend fun searchAirports(text: String): List<Airport> {
        return database.airportDao().search(text.trim()).map { entity ->
            Airport(entity.id, entity.iataCode, entity.name, entity.passengers)
        }
    }

    override suspend fun airportByCode(code: String): Airport? {
        return database.airportDao().getByCode(code.trim())?.let { entity ->
            Airport(entity.id, entity.iataCode, entity.name, entity.passengers)
        }
    }

    override fun observeFlights(iataCode: String): Flow<List<Flight>> {
        return database.airportDao().observeFlights(iataCode).map { rows ->
            rows.map { row ->
                Flight(
                    departureCode = row.departureCode,
                    departureName = row.departureName,
                    arrivalCode = row.arrivalCode,
                    arrivalName = row.arrivalName,
                )
            }
        }
    }

    override fun observeFavorites(): Flow<List<FavoriteFlight>> {
        return database.favoriteDao().observeAll().map { rows ->
            rows.map { row ->
                FavoriteFlight(
                    id = row.id,
                    departureCode = row.departureCode,
                    destinationCode = row.destinationCode,
                    departureName = row.departureName,
                    destinationName = row.destinationName,
                )
            }
        }
    }

    override suspend fun addFavorite(departureCode: String, destinationCode: String) {
        if (database.favoriteDao().find(departureCode, destinationCode) != null) {
            return
        }
        database.favoriteDao().insert(
            FavoriteEntity(
                departureCode = departureCode,
                destinationCode = destinationCode,
            )
        )
    }

    override suspend fun removeFavorite(departureCode: String, destinationCode: String) {
        database.favoriteDao().deletePair(departureCode, destinationCode)
    }

    override suspend fun removeFavoriteById(id: Int) {
        database.favoriteDao().deleteById(id)
    }
}
