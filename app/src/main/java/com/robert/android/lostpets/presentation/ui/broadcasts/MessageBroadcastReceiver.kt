package com.robert.android.lostpets.presentation.ui.broadcasts

/**
 * Interfaz que extiende la interfaz MessageReceiver. Su función consiste en transmitir
 * mensajes a los componentes que se encuentran suscritos.
 *
 * @see com.robert.android.lostpets.presentation.ui.broadcasts.MessageReceiver
 */
interface MessageBroadcastReceiver : MessageReceiver {

    /**
     * Método que notifica que se ha producido un error durante la conexión mediante STOMP.
     */
    fun onConnectionError()

    /**
     * Método que notifica que la conexión mediante STOMP con el servidor se ha interrumpido.
     */
    fun onFailedServerHeartbeat()

    /**
     * Método que notifica que se ha producido un error con la suscripción de un usuario a un
     * determinado tema ("topic). Este error se produce también si el usuario asociado a dicho
     * tema no puede recibir mensajes.
     */
    fun onMessageReceivedError()
}
