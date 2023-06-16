package com.kitchenspal.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.kitchenspal.response.LoginResponse

class MySharedPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveString(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun saveBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun saveLoginResponse(loginResponse: LoginResponse) {
        val gson = Gson()
        val loginResponseJson = gson.toJson(loginResponse)
        saveString("login_response", loginResponseJson)
    }

    fun getLoginResponse(): LoginResponse? {
        val loginResponseJson = getString("login_response", "token")
        return try {
            val gson = Gson()
            gson.fromJson(loginResponseJson, LoginResponse::class.java)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }

    fun clearLoginResponse() {
        editor.remove("login_response")
        editor.apply()
    }

    companion object {
        const val KEY_LOGIN_STATUS = "login_status"
    }
}
