package com.example.flightsearch.ui

import com.example.flightsearch.domain.model.Airport
import com.example.flightsearch.domain.model.FavoriteFlight
import com.example.flightsearch.domain.model.Flight

data class SearchUiState(
    val query: String = "",
    val suggestions: List<Airport> = emptyList(),
    val flights: List<Flight> = emptyList(),
    val favorites: List<FavoriteFlight> = emptyList(),
    val selectedCode: String? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
) {
    val showFavorites: Boolean
        get() = query.isBlank()

    val showFlights: Boolean
        get() = selectedCode != null && query.equals(selectedCode, ignoreCase = true)
}
