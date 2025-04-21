package com.rbs.iso8583lib.utils.cores

import android.content.Context
import android.content.SharedPreferences

object PrefManager {
    private const val PREF_NAME = "rbs_iso8583_prefs"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun putAny(key: String, value: Any) {
        with(prefs.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
            }
            apply()
        }
    }

    fun getString(key: String, def: String? = null) = prefs.getString(key, def)
    fun getInt(key: String, def: Int = 0) = prefs.getInt(key, def)
    fun getBoolean(key: String, def: Boolean = false) = prefs.getBoolean(key, def)
    fun contains(key: String) = prefs.contains(key)
    fun remove(key: String) = prefs.edit().remove(key).apply()
    fun clearAll() = prefs.edit().clear().apply()
}
