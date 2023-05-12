package com.nitishsharma.aroundme.utils

import com.nitishsharma.aroundme.R

class SearchBarHelper {
    companion object {
        val PLACES_TO_GO: ArrayList<Pair<Pair<Int, String>, String>> = arrayListOf(
            Pair(Pair(R.drawable.ic_hotel, "Hotel"), "hotel"),
            Pair(Pair(R.drawable.ic_atm, "Atm"), "atm"),
            Pair(Pair(R.drawable.ic_coffee, "Coffee"), "cafe"),
            Pair(Pair(R.drawable.ic_restaurant, "Restaurant"), "restaurant"),
            Pair(Pair(R.drawable.ic_park, "Park"), "amusement_park")
        )
    }
}