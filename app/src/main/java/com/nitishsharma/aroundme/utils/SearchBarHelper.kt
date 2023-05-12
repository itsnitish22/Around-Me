package com.nitishsharma.aroundme.utils

import com.nitishsharma.aroundme.R

class SearchBarHelper {
    companion object {
        val PLACES_TO_GO = mapOf<Int, String>(
            R.drawable.ic_hotel to "Hotel",
            R.drawable.ic_atm to "Atm",
            R.drawable.ic_coffee to "Coffee",
            R.drawable.ic_restaurant to "Restaurant",
            R.drawable.ic_shop to "Shop",
            R.drawable.ic_park to "Park"
        ).toList().map { Pair(it.first, it.second) }
    }
}