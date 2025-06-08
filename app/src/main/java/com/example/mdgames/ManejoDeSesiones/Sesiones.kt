package com.example.mdgames.ManejoDeSesiones

import android.content.Context
import android.content.SharedPreferences

// Singleton object to manage user session data
object Sesiones {
    private const val PREF_NAME = "SesionPrefs"
    private const val KEY_USER_ID = "user_id"

    // Save the user ID to SharedPreferences
    fun guardarSesion(context: Context, userId: Int) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }

    // Retrieve the saved user ID, or -1 if not found
    fun obtenerSesion(context: Context): Int {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_USER_ID, -1)
    }

    // Remove the user ID to log out
    fun cerrarSesion(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_USER_ID).apply()
    }
}