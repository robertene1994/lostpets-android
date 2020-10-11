package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.interactors.base.BaseCallbackInteractor
import com.robert.android.lostpets.domain.interactors.base.Interactor
import com.robert.android.lostpets.domain.model.Message

/**
 * Interfaz del interactor que se encarga de recuperar los mensajes de un determinado chat.
 *
 * @author Robert Ene
 */
interface ChatDetailInteractor : Interactor {

    /**
     * Interfaz que es utilizada por el interactor para notificar los resultados de las operaciones
     * que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface Callback : BaseCallbackInteractor {

        /**
         * Método que notifica que los mensajes de un determinado chat han sido recuperados.
         *
         * @param messages los mensajes del chat establecido.
         */
        fun onChatMessagesRetrieved(messages: List<Message>)

        /**
         * Método que notifica que un determinado chat no tiene mensajes asociados.
         */
        fun onChatMessagesEmpty()
    }
}
