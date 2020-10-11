package com.robert.android.lostpets.domain.repository

/**
 * Interfaz del respositorio para las preferencias (ajustes de la aplicación) que el usuario
 * puede establecer para la aplicación.
 *
 * @author Robert Ene
 */
interface SettingsRepository {

    /**
     * Método que devuelve la preferencia del usuario sobre el idioma de la aplicación.
     *
     * @return la preferencia del usuario sobre el idioma de la aplicación.
     */
    fun getLanguage(): String?

    /**
     * Método que guarda la preferencia del usuario sobre el idioma de la aplicación.
     *
     * @param language la preferencia del usuario sobre el idioma de la aplicación.
     */
    fun saveLanguage(language: String)
}
