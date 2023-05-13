package com.nitishsharma.aroundme.utils

import com.nitishsharma.aroundme.R

class SearchBarHelper {
    companion object {
        val PLACES_TO_GO: ArrayList<Pair<Pair<Int, String>, String>> = arrayListOf(
            Pair(Pair(R.drawable.ic_hotel, "Hotel"), "hotel"),
            Pair(Pair(R.drawable.ic_atm, "Atm"), "atm"),
            Pair(Pair(R.drawable.ic_coffee, "Coffee"), "cafe"),
            Pair(Pair(R.drawable.ic_restaurant, "Restaurant"), "restaurant"),
            Pair(Pair(R.drawable.ic_carrental, "Car Rental"), "car_rental"),
            Pair(Pair(R.drawable.ic_chemist, "Chemist"), "drugstore"),
            Pair(Pair(R.drawable.ic_electrician, "Electrical"), "electrician"),
            Pair(Pair(R.drawable.ic_cake, "Bakery"), "bakery"),
            Pair(Pair(R.drawable.ic_shop, "Grocery"), "department_store"),
        )
    }
}