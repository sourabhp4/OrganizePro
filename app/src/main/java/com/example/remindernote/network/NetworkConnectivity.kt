package com.example.remindernote.network

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast

class NetworkConnectivity {
    enum class NetworkType {
        NONE, WIFI, CELLULAR
    }

    fun getActiveNetworkType(context: Context): NetworkType {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork
        if (activeNetwork != null) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            if (networkCapabilities != null) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return NetworkType.WIFI
                }
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return NetworkType.CELLULAR
                }
            }
        }

        return NetworkType.NONE
    }

    fun showNoInternetPopup(context: Context) {
        Log.d("myTag", "No Internet Popup")
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("No Internet Connection")
        alertDialogBuilder.setMessage("Please check your internet connection and try again.")
        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            Toast.makeText(context, "Remember to come back... :)", Toast.LENGTH_LONG).show()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}