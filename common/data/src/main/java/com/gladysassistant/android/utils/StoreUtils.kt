package com.gladysassistant.android.utils

import android.content.Context
import androidx.car.app.CarContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import com.gladysassistant.android.auto.data.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

data class UserPreferences(
    val userId: String,
    val userPassword: String
)
data class TokenPreferences (
    val accessToken: String?,
    val refreshToken: String?
)

class StoreUtils(dataStore: DataStore<Preferences>) {

    //private val Context.dataStore by preferencesDataStore(name = Constants.PREFERENCE_NAME)

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data.map { preferences ->
        val userId = preferences[PreferencesKeys.USER_ID] ?: "pochet.romuald@gmail.com"
        val userPassword = preferences[PreferencesKeys.USER_PASSWORD] ?: "8Vyr7acpromuald"
        UserPreferences(userId, userPassword)
    }

    val tokenPreferencesFlow: Flow<TokenPreferences> = dataStore.data.map { preferences ->
        val accessToken = preferences[PreferencesKeys.ACCESS_TOKEN] ?: null
        val refreshToken = preferences[PreferencesKeys.REFRESH_TOKEN] ?: null
        TokenPreferences(accessToken, refreshToken)
    }

    companion object {
        @Volatile
        private var INSTANCE: StoreUtils? = null

        fun getInstance(dataStore: DataStore<Preferences>): StoreUtils {
            return INSTANCE ?: synchronized(this) {
                INSTANCE?.let {
                    return it
                }

                val instance = StoreUtils(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    private object PreferencesKeys {
        val USER_ID = stringPreferencesKey(Constants.USER_ID)
        val USER_PASSWORD = stringPreferencesKey(Constants.USER_PASSWORD)
        val ACCESS_TOKEN = stringPreferencesKey(Constants.PARAM_ACCESS_TOKEN)
        val REFRESH_TOKEN = stringPreferencesKey(Constants.PARAM_REFRESH_TOKEN)
    }

}