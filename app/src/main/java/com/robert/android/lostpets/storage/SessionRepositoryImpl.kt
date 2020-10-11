package com.robert.android.lostpets.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.repository.SessionRepository
import com.robert.android.lostpets.network.converter.GsonSerializer

/**
 * Clase que implementa la interfaz SessionRepository haciendo uso de las preferencias
 * compartidas proporcionadas por el framework de Android.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.repository.SessionRepository
 */
class SessionRepositoryImpl(context: Context) : SessionRepository {

    private companion object {
        const val PREFS_NAME = "LostPets"
        const val TOKEN = "TOKEN"
        const val USER = "USER"
    }

    private val mSettings: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val mEditor: SharedPreferences.Editor = mSettings.edit()
    private val mGson: Gson = GsonSerializer.instance

    override fun getUser(): User? {
        val json = mSettings.getString(USER, "")
        return mGson.fromJson(json, User::class.java)
    }

    override fun saveUser(user: User) {
        val json = mGson.toJson(user)
        mEditor.putString(USER, json)
        mEditor.commit()
    }

    override fun deleteUser() {
        mEditor.remove(USER)
        mEditor.commit()
    }

    override fun getToken(): String? {
        return mSettings.getString(TOKEN, null)
    }

    override fun saveToken(token: String) {
        mEditor.putString(TOKEN, token)
        mEditor.commit()
    }

    override fun deleteToken() {
        mEditor.remove(TOKEN)
        mEditor.commit()
    }
}
