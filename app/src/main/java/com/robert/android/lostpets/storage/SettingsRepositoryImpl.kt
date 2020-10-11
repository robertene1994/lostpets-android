package com.robert.android.lostpets.storage

import android.content.Context
import android.content.SharedPreferences
import com.robert.android.lostpets.domain.repository.SettingsRepository

/**
 * Clase que implementa la interfaz SettingsRepository haciendo uso de las preferencias
 * compartidas proporcionadas por el framework de Android.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.repository.SettingsRepository
 */
class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    private companion object {
        const val PREFS_NAME = "LostPets"
        const val LANGUAGE = "LANGUAGE"
    }

    private val mSettings: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val mEditor: SharedPreferences.Editor = mSettings.edit()

    override fun getLanguage(): String? {
        return if (!mSettings.contains(LANGUAGE))
            null
        else
            mSettings.getString(LANGUAGE, "")
    }

    override fun saveLanguage(language: String) {
        mEditor.putString(LANGUAGE, language)
        mEditor.commit()
    }
}
