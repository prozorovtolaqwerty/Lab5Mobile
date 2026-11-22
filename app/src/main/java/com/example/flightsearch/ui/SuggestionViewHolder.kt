package com.example.flightsearch.ui

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flightsearch.R
import com.example.flightsearch.domain.model.Airport

class SuggestionViewHolder(
    itemView: View,
    private val onClick: (Airport) -> Unit,
) : RecyclerView.ViewHolder(itemView) {

    private val codeText: TextView = itemView.findViewById(R.id.suggestionCode)
    private val nameText: TextView = itemView.findViewById(R.id.suggestionName)
    private var airport: Airport? = null

    init {
        itemView.setOnClickListener {
            airport?.let(onClick)
        }
    }

    fun bind(item: Airport) {
        airport = item
        codeText.text = item.iataCode
        nameText.text = item.name
    }
}
