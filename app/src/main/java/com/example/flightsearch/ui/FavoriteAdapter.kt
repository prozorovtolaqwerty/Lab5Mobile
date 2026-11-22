package com.example.flightsearch.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flightsearch.R
import com.example.flightsearch.domain.model.FavoriteFlight

class FavoriteAdapter(
    private val onRemoveClick: (FavoriteFlight) -> Unit,
) : RecyclerView.Adapter<FavoriteViewHolder>() {

    private val items = mutableListOf<FavoriteFlight>()

    fun submit(favorites: List<FavoriteFlight>) {
        items.clear()
        items.addAll(favorites)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite, parent, false)
        return FavoriteViewHolder(view, onRemoveClick)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
