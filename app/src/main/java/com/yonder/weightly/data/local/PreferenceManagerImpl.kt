package com.yonder.weightly.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferenceManagerImpl(context: Context) : PreferenceManager {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> prefs.getString(key, defaultValue) as T
            is Int -> prefs.getInt(key, defaultValue) as T
            is Long -> prefs.getLong(key, defaultValue) as T
            is Float -> prefs.getFloat(key, defaultValue) as T
            is Boolean -> prefs.getBoolean(key, defaultValue) as T
            is Double -> java.lang.Double.longBitsToDouble(
                prefs.getLong(key, java.lang.Double.doubleToRawLongBits(defaultValue))
            ) as T
            else -> throw IllegalArgumentException("Unsupported type: ${defaultValue::class.java}")
        }
    }

    override fun <T : Any> put(key: String, value: T) {
        prefs.edit().apply {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is Boolean -> putBoolean(key, value)
                is Double -> putLong(key, java.lang.Double.doubleToRawLongBits(value))
                else -> throw IllegalArgumentException("Unsupported type: ${value::class.java}")
            }
            apply()
        }
    }

    override fun delete(key: String) {
        prefs.edit { remove(key) }
    }

    override fun deleteAll() {
        prefs.edit { clear() }
    }

    override fun contains(key: String): Boolean {
        return prefs.contains(key)
    }

    companion object {
        private const val PREFS_NAME = "weightly_prefs"
    }
}
