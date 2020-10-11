package com.robert.android.lostpets.network.service

import com.robert.android.lostpets.domain.model.Chat
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interfaz que contiene las operaciones de los chats, mediante las cuales la aplicación se
 * comunica con la API.
 *
 * @author Robert Ene
 */
interface ChatService {

    /**
     * Método que recupera todos los chats perteneciendes a un determinado usuario.
     *
     * @param userId el id del usuario.
     * @return la llamada asíncrona a la API.
     */
    @GET("chat/user/{userId}")
    fun getUserChats(@Path("userId") userId: Long): Call<List<Chat>>
}
