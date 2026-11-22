package com.example.flightsearch.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.searchDataStore by preferencesDataStore(name = "search_preferences")

class SearchPreferences(private val context: Context) {
    val lastQuery: Flow<String> = context.searchDataStore.data.map { preferences ->
        preferences[KEY_QUERY].orEmpty()
    }

    suspend fun saveQuery(query: String) {
        context.searchDataStore.edit { preferences ->
            preferences[KEY_QUERY] = query
        }
    }

    companion object {
        private val KEY_QUERY = stringPreferencesKey("last_search")
    }
}
