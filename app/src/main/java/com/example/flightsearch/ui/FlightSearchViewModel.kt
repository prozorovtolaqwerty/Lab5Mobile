package com.example.flightsearch.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightsearch.data.datastore.SearchPreferences
import com.example.flightsearch.data.local.FlightDatabase
import com.example.flightsearch.data.repository.FlightRepositoryImpl
import com.example.flightsearch.domain.FlightRepository
import com.example.flightsearch.domain.model.Airport
import com.example.flightsearch.domain.model.FavoriteFlight
import com.example.flightsearch.domain.model.Flight
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class FlightSearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FlightRepository = FlightRepositoryImpl(
        FlightDatabase.getInstance(application),
    )
    private val preferences = SearchPreferences(application)

    private val query = MutableStateFlow("")
    private val suggestions = MutableStateFlow<List<Airport>>(emptyList())
    private val selected = MutableStateFlow<Airport?>(null)
    private val loading = MutableStateFlow(true)
    private val error = MutableStateFlow<String?>(null)

    private val flights = selected.flatMapLatest { airport ->
        if (airport == null) {
            flowOf(emptyList())
        } else {
            repository.observeFlights(airport.iataCode)
        }
    }.catch { throwable ->
        error.value = throwable.message
        emit(emptyList())
    }

    private val favorites = repository.observeFavorites().catch { throwable ->
        error.value = throwable.message
        emit(emptyList())
    }

    val uiState: StateFlow<SearchUiState> = combine(
        combine(query, suggestions, selected) { currentQuery, currentSuggestions, currentAirport ->
            Triple(currentQuery, currentSuggestions, currentAirport)
        },
        combine(flights, favorites) { currentFlights, currentFavorites ->
            currentFlights to currentFavorites
        },
        combine(loading, error) { isLoading, errorMessage ->
            isLoading to errorMessage
        },
    ) { search, results, status ->
        val (currentQuery, currentSuggestions, currentAirport) = search
        val (currentFlights, currentFavorites) = results
        val (isLoading, errorMessage) = status
        val favoriteKeys = currentFavorites
            .map { favorite -> favorite.departureCode to favorite.destinationCode }
            .toSet()
        SearchUiState(
            query = currentQuery,
            suggestions = currentSuggestions,
            flights = currentFlights.map { flight ->
                flight.copy(
                    isFavorite = (flight.departureCode to flight.arrivalCode) in favoriteKeys,
                )
            },
            favorites = currentFavorites,
            selectedCode = currentAirport?.iataCode,
            isLoading = isLoading,
            errorMessage = errorMessage,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUiState())

    init {
        viewModelScope.launch {
            try {
                restore(preferences.lastQuery.first())
            } catch (throwable: Exception) {
                error.value = throwable.message
            } finally {
                loading.value = false
            }
        }
        viewModelScope.launch {
            query.debounce(300).collect { text ->
                val selectedCode = selected.value?.iataCode
                if (text.isBlank() || text.equals(selectedCode, ignoreCase = true)) {
                    suggestions.value = emptyList()
                    return@collect
                }
                try {
                    suggestions.value = repository.searchAirports(text)
                    error.value = null
                } catch (throwable: Exception) {
                    suggestions.value = emptyList()
                    error.value = throwable.message
                }
            }
        }
    }

    fun onQueryChanged(text: String) {
        if (text == query.value) {
            return
        }
        query.value = text
        val selectedCode = selected.value?.iataCode
        if (!text.equals(selectedCode, ignoreCase = true)) {
            selected.value = null
        }
        if (text.isBlank()) {
            viewModelScope.launch {
                preferences.saveQuery("")
            }
        }
    }

    fun selectAirport(airport: Airport) {
        query.value = airport.iataCode
        selected.value = airport
        suggestions.value = emptyList()
        viewModelScope.launch {
            try {
                preferences.saveQuery(airport.iataCode)
                error.value = null
            } catch (throwable: Exception) {
                error.value = throwable.message
            }
        }
    }

    fun submitSearch() {
        viewModelScope.launch {
            val text = query.value.trim()
            if (text.isBlank()) {
                selected.value = null
                preferences.saveQuery("")
                return@launch
            }
            try {
                val exact = repository.airportByCode(text)
                val airport = exact ?: repository.searchAirports(text).firstOrNull()
                if (airport == null) {
                    selected.value = null
                    suggestions.value = emptyList()
                } else {
                    selectAirport(airport)
                }
                error.value = null
            } catch (throwable: Exception) {
                error.value = throwable.message
            }
        }
    }

    fun toggleFavorite(flight: Flight) {
        viewModelScope.launch {
            try {
                if (flight.isFavorite) {
                    repository.removeFavorite(flight.departureCode, flight.arrivalCode)
                } else {
                    repository.addFavorite(flight.departureCode, flight.arrivalCode)
                }
                error.value = null
            } catch (throwable: Exception) {
                error.value = throwable.message
            }
        }
    }

    fun removeFavorite(favorite: FavoriteFlight) {
        viewModelScope.launch {
            try {
                repository.removeFavoriteById(favorite.id)
                error.value = null
            } catch (throwable: Exception) {
                error.value = throwable.message
            }
        }
    }

    private suspend fun restore(saved: String) {
        if (saved.isBlank()) {
            return
        }
        val airport = repository.airportByCode(saved)
        if (airport != null) {
            query.value = airport.iataCode
            selected.value = airport
        }
    }
}
