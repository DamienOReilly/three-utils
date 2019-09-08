package org.damienoreilly.threeutils.repository

/**
 * Based on https://github.com/google/iosched/blob/master/shared/src/main/java/com/google/samples/apps/iosched/shared/data/prefs/PreferenceStorage.kt
 */

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


interface PreferenceStorage {
    var alertInternetExpiring: Boolean
    var my3UserName: String?
    var my3Password: String?
}

class SharedPreferenceStorage(context: Context) :
        PreferenceStorage {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)


    override var alertInternetExpiring by BooleanPreference(prefs, ALERT_INTERNET_EXPIRING, false)

    override var my3UserName by StringPreference(prefs, MY3_USERNAME, null)

    override var my3Password by StringPreference(prefs, MY3_PASSWORD, null)


    companion object {
        const val PREFS_NAME = "3utils"
        const val ALERT_INTERNET_EXPIRING = "alert_internet_expiring"
        const val MY3_USERNAME = "my3_username"
        const val MY3_PASSWORD = "my3_password"
    }
}

class BooleanPreference(
        private val preferences: SharedPreferences,
        private val name: String,
        private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.edit { putBoolean(name, value) }
    }
}

class StringPreference(
        private val preferences: SharedPreferences,
        private val name: String,
        private val defaultValue: String?
) : ReadWriteProperty<Any, String?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return preferences.getString(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.edit { putString(name, value) }
    }
}
