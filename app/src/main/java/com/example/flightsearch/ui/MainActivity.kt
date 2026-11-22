package com.example.flightsearch.ui

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flightsearch.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: FlightSearchViewModel by viewModels()
    private var updatingText = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val queryInput = findViewById<TextInputEditText>(R.id.queryInput)
        val searchButton = findViewById<MaterialButton>(R.id.searchButton)
        val sectionTitle = findViewById<TextView>(R.id.sectionTitle)
        val emptyText = findViewById<TextView>(R.id.emptyText)
        val errorText = findViewById<TextView>(R.id.errorText)
        val progress = findViewById<ProgressBar>(R.id.progress)
        val recycler = findViewById<RecyclerView>(R.id.resultsRecycler)
        recycler.layoutManager = LinearLayoutManager(this)

        val suggestionAdapter = SuggestionAdapter { airport ->
            viewModel.selectAirport(airport)
        }
        val flightAdapter = FlightAdapter { flight ->
            viewModel.toggleFavorite(flight)
        }
        val favoriteAdapter = FavoriteAdapter { favorite ->
            viewModel.removeFavorite(favorite)
        }

        queryInput.doAfterTextChanged { text ->
            if (!updatingText) {
                viewModel.onQueryChanged(text?.toString().orEmpty())
            }
        }
        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.submitSearch()
                true
            } else {
                false
            }
        }
        searchButton.setOnClickListener {
            viewModel.submitSearch()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (queryInput.text?.toString() != state.query) {
                        updatingText = true
                        queryInput.setText(state.query)
                        queryInput.setSelection(state.query.length)
                        updatingText = false
                    }
                    progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    errorText.visibility = if (state.errorMessage.isNullOrBlank()) View.GONE else View.VISIBLE
                    errorText.text = state.errorMessage?.let { getString(R.string.database_error, it) }

                    when {
                        state.isLoading -> {
                            recycler.visibility = View.GONE
                            sectionTitle.visibility = View.GONE
                            emptyText.visibility = View.GONE
                        }
                        state.showFavorites -> {
                            recycler.adapter = favoriteAdapter
                            favoriteAdapter.submit(state.favorites)
                            recycler.visibility = if (state.favorites.isEmpty()) View.GONE else View.VISIBLE
                            sectionTitle.visibility = View.VISIBLE
                            sectionTitle.text = getString(R.string.favorites_title)
                            emptyText.visibility = if (state.favorites.isEmpty()) View.VISIBLE else View.GONE
                            emptyText.text = getString(R.string.favorites_empty)
                        }
                        state.showFlights -> {
                            recycler.adapter = flightAdapter
                            flightAdapter.submit(state.flights)
                            recycler.visibility = if (state.flights.isEmpty()) View.GONE else View.VISIBLE
                            sectionTitle.visibility = View.VISIBLE
                            sectionTitle.text = getString(R.string.flights_title, state.selectedCode)
                            emptyText.visibility = if (state.flights.isEmpty()) View.VISIBLE else View.GONE
                            emptyText.text = getString(R.string.flights_empty)
                        }
                        else -> {
                            recycler.adapter = suggestionAdapter
                            suggestionAdapter.submit(state.suggestions)
                            recycler.visibility = if (state.suggestions.isEmpty()) View.GONE else View.VISIBLE
                            sectionTitle.visibility = View.VISIBLE
                            sectionTitle.text = getString(R.string.suggestions_title)
                            emptyText.visibility = if (state.suggestions.isEmpty()) View.VISIBLE else View.GONE
                            emptyText.text = getString(R.string.suggestions_empty)
                        }
                    }
                }
            }
        }
    }
}
