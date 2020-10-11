package com.robert.android.lostpets.network.service

import com.robert.android.lostpets.domain.model.Message
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interfaz que contiene las operaciones de los mensajes pertenecientes a los chats, mediante las
 * cuales la aplicación se comunica con la API.
 *
 * @author Robert Ene
 */
interface MessageService {

    /**
     * Método que recupera todos los mensajes perteneciendes a un determinado chat al mismo
     * tiempo que marca como leídos los mensajes nuevos.
     *
     * @param chatCode el código del chat.
     * @param userEmail el email del usuario del chat.
     * @return la llamada asíncrona a la API.
     */
    @GET("message/markAsRead/{chatCode}")
    fun getChatMessagesAndMarkAsRead(@Path("chatCode") chatCode: String,
                                     @Query("userEmail") userEmail: String): Call<List<Message>>
}
