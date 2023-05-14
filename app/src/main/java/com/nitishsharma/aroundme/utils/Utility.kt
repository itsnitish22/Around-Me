package com.nitishsharma.aroundme.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

object Utility {
    fun Context.toast(message: String) {
        Toast.makeText(
            this, message,
            if (message.length <= 25) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
        ).show()
    }

    fun Fragment.toast(msg: String) {
        requireContext().toast(msg)
    }

    fun Context.isLocationPermissionNotGiven(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    }

    fun askLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            Constants.LOCATION_PERMISSION_REQUEST_CODE
        )
    }
}