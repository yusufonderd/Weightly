package com.yonder.weightly.data.local

interface PreferenceManager {
    fun <T : Any> get(key: String, defaultValue: T): T
    fun <T : Any> put(key: String, value: T)
    fun delete(key: String)
    fun deleteAll()
    fun contains(key: String): Boolean
}
