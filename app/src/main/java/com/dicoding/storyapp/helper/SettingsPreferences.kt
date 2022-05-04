package com.dicoding.storyapp.helper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//Preferences to store user login session
class SettingsPreferences private constructor(private val dataStore: DataStore<Preferences>){

    private val SESSION_USERNAME = stringPreferencesKey("session_username")
    private val SESSION_TOKEN = stringPreferencesKey("session_token")

    fun getSessionInfo() : Flow<String> {
        return dataStore.data.map{ preferences ->
            preferences[SESSION_USERNAME] ?: ""
            preferences[SESSION_TOKEN] ?: ""
        }
    }

    fun getSessionToken(): Flow<String>{
        return dataStore.data.map{ preferences ->
            preferences[SESSION_TOKEN] ?: ""
        }
    }

    suspend fun saveSessionInfo(username: String, token : String){
        dataStore.edit{ preferences ->
            preferences[SESSION_USERNAME] = username
            preferences[SESSION_TOKEN] = token
        }
    }

    suspend fun deleteSessionInfo(){
        dataStore.edit{ preferences ->
            preferences[SESSION_USERNAME] = ""
            preferences[SESSION_TOKEN] = ""
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: SettingsPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>) : SettingsPreferences {
            return INSTANCE ?: synchronized(this){
                val instance = SettingsPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}