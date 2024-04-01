package com.abhishek.calendar.utils

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utility {

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }

                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }

                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            return if (connectivityManager?.activeNetworkInfo != null) {
                connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting == true
            } else ({
                connectivityManager?.isDefaultNetworkActive
            }) as Boolean
        }
        return false
    }

    fun showSnackBar(view: View, message: String) {
        try {
            val snackBar = Snackbar.make(
                view, message, Snackbar.LENGTH_LONG
            )
            snackBar.view.setBackgroundColor(Color.parseColor("#0E3F6C"))
            snackBar.setTextColor(Color.parseColor("#FFFFFF"))
            snackBar.setTextMaxLines(3)
            snackBar.view.minimumHeight = 100 // here
            snackBar.show()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

    }

    fun showToast(context: Context?, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun getFormattedDate(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
    }
}