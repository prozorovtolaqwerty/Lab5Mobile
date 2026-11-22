package com.example.flightsearch.ui

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flightsearch.R
import com.example.flightsearch.domain.model.Flight

class FlightViewHolder(
    itemView: View,
    private val onFavoriteClick: (Flight) -> Unit,
) : RecyclerView.ViewHolder(itemView) {

    private val routeText: TextView = itemView.findViewById(R.id.routeId)
    private val departureText: TextView = itemView.findViewById(R.id.departureText)
    private val arrivalText: TextView = itemView.findViewById(R.id.arrivalText)
    private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
    private var flight: Flight? = null

    init {
        favoriteButton.setOnClickListener {
            flight?.let(onFavoriteClick)
        }
    }

    fun bind(item: Flight) {
        flight = item
        val context = itemView.context
        routeText.text = context.getString(R.string.flight_id, item.routeId)
        departureText.text = context.getString(R.string.airport_line, item.departureCode, item.departureName)
        arrivalText.text = context.getString(R.string.airport_line, item.arrivalCode, item.arrivalName)
        favoriteButton.setImageResource(
            if (item.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
        favoriteButton.contentDescription = context.getString(
            if (item.isFavorite) R.string.remove_favorite else R.string.add_favorite
        )
    }
}
