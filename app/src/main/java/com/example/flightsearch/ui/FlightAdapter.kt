package com.example.flightsearch.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flightsearch.R
import com.example.flightsearch.domain.model.Flight

class FlightAdapter(
    private val onFavoriteClick: (Flight) -> Unit,
) : RecyclerView.Adapter<FlightViewHolder>() {

    private val items = mutableListOf<Flight>()

    fun submit(flights: List<Flight>) {
        items.clear()
        items.addAll(flights)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flight, parent, false)
        return FlightViewHolder(view, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
