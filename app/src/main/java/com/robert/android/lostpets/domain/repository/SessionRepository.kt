package com.robert.android.lostpets.domain.repository

import com.robert.android.lostpets.domain.model.User

/**
 * Interfaz del respositorio para la sesión iniciada por los usuarios dentro de la aplicación.
 *
 * @author Robert Ene
 */
interface SessionRepository {

    /**
     * Método que devuelve los datos del usuario que tiene la sesión iniciada.
     *
     * @return los datos del usuario.
     */
    fun getUser(): User?

    /**
     * Método que guarda los datos del usuario que tiene la sesión iniciada.
     *
     * @param user los datos del usuario.
     */
    fun saveUser(user: User)

    /**
     * Método que elimina los datos del usuario que tiene la sesión iniciada.
     */
    fun deleteUser()

    /**
     * Método que devuelve el token que valida la sesión del usuario.
     *
     * @return el token de la sesión.
     */
    fun getToken(): String?

    /**
     * Método que guarda el token que valida la sesión del usuario.
     *
     * @param token el token de la sesión.
     */
    fun saveToken(token: String)

    /**
     * Método que elimina el token que valida la sesión del usuario.
     */
    fun deleteToken()
}
