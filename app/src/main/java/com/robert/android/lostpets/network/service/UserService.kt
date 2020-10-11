package com.robert.android.lostpets.network.service

import com.robert.android.lostpets.domain.model.AccountCredentials
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.UserStatus
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Interfaz que contiene las operaciones de los usuarios, mediante las cuales la aplicación se
 * comunica con la API.
 *
 * @author Robert Ene
 */
interface UserService {

    /**
     * Método mediante el cual el usuario inicia sesión en la aplicación.
     *
     * @param account las credenciales del usuario.
     * @return la llamada asíncrona a la API.
     */
    @POST("user/logIn")
    fun logIn(@Body account: AccountCredentials): Call<String>

    /**
     * Método que recupera los detalles del usuario que inicia sesión.
     *
     * @param email el correo electrónico de usuario para el que se recupera los detalles.
     * @return la llamada asíncrona a la API.
     */
    @GET("user/userDetails")
    fun getUserDetails(@Query("email") email: String): Call<User>

    /**
     * Método que comprueba si un correo electrónico proporcionado por el usuario es único.
     *
     * @param email el correo electrónico proporcionado por el usuario.
     * @return la llamada asíncrona a la API.
     */
    @GET("user/uniqueEmail")
    fun checkUniqueEmail(@Query("email") email: String): Call<Boolean>

    /**
     * Método que comprueba el estado de la cuenta de un determinado usuario.
     *
     * @param email el correo electrónico proporcionado por el usuario.
     * @return la llamada asíncrona a la API.
     */
    @GET("user/userStatus")
    fun checkUserStatus(@Query("email") email: String): Call<UserStatus>

    /**
     * Método mediante el cual un usuario se registra en el sistema.
     *
     * @param user los datos del usuario.
     * @return la llamada asíncrona a la API.
     */
    @POST("user")
    fun signUp(@Body user: User): Call<User>
}
