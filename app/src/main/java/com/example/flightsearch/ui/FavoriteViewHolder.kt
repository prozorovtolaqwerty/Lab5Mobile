package com.example.flightsearch.ui

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flightsearch.R
import com.example.flightsearch.domain.model.FavoriteFlight

class FavoriteViewHolder(
    itemView: View,
    private val onRemoveClick: (FavoriteFlight) -> Unit,
) : RecyclerView.ViewHolder(itemView) {

    private val routeText: TextView = itemView.findViewById(R.id.favoriteRoute)
    private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
    private var favorite: FavoriteFlight? = null

    init {
        favoriteButton.setOnClickListener {
            favorite?.let(onRemoveClick)
        }
    }

    fun bind(item: FavoriteFlight) {
        favorite = item
        val context = itemView.context
        routeText.text = context.getString(
            R.string.favorite_line,
            item.departureCode,
            item.destinationCode,
        )
        favoriteButton.contentDescription = context.getString(R.string.remove_favorite)
    }
}
