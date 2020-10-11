package com.robert.android.lostpets.presentation.presenters

import com.robert.android.lostpets.domain.model.Message
import com.robert.android.lostpets.presentation.presenters.base.BasePresenter
import com.robert.android.lostpets.presentation.presenters.base.BaseViewPresenter

/**
 * Interfaz del presenter que se encarga de coordinar la vista asociada a la recuperación y
 * coordinación de un chat (junto a los mensajes pertenecientes al mismo) entre dos usuarios.
 *
 * @author Robert Ene
 */
interface ChatDetailPresenter : BasePresenter {

    /**
     * Interfaz que es utilizada por el presenter para notificar los resultados de las operaciones
     * que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface View : BaseViewPresenter {

        /**
         * Método que notifica que los mensajes de un determinado chat se pueden mostrar.
         *
         * @param messages los mensajes del chat establecido.
         */
        fun showChatMessages(messages: List<Message>)

        /**
         * Método que notifica al usuario que un determinado chat no tiene mensajes asociados.
         */
        fun onChatMessagesEmpty()

        /**
         * Método que notifica al usuario de que ha recibido un nuevo mensaje.
         *
         * @param message el mensaje recibido.
         */
        fun messageReceived(message: Message)

        /**
         * Método que notifica al usuario de que ha recibido un mensaje que se debe actualizar en
         * el chat.
         *
         * @param message el mensaje recibido que se debe actualizar en el chat.
         */
        fun messageStatusUpdated(message: Message)

        /**
         * Método que notifica al usuario de que ha recibido un mensaje perteneciente a otro chat
         * diferente del que se encuentra actualmente.
         *
         * @param message el mensaje recibido que pertenece a otro chat diferente al actual.
         */
        fun messageReceivedForOtherChat(message: Message)

        /**
         * Método que notifica al usuario de que ha ocurrdio un error durante el proceso de envío
         * del último mensaje enviado.
         */
        fun messageSentError()
    }

    /**
     * Método que recupera los mensajes de un determinado chat.
     */
    fun getChatMessages()

    /**
     * Método que envía un mensaje a un determinado chat.
     *
     * @param message el mensaje que se envía.
     */
    fun sendMessage(message: Message)

    /**
     * Método que procesa un determinado mensaje que se ha recibido.
     *
     * @param message el mensaje que se ha recibido.
     */
    fun onMessageReceived(message: Message)
}
