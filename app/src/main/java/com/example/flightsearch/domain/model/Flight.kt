package com.example.flightsearch.domain.model

data class Flight(
    val departureCode: String,
    val departureName: String,
    val arrivalCode: String,
    val arrivalName: String,
    val isFavorite: Boolean = false,
) {
    val routeId: String
        get() = "$departureCode-$arrivalCode"
}
