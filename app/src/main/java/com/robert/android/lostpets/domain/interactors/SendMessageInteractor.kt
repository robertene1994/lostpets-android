package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.interactors.base.BaseCallbackInteractor
import com.robert.android.lostpets.domain.interactors.base.Interactor
import com.robert.android.lostpets.domain.model.Message

/**
 * Interfaz del interactor que se encarga de manejar el proceso mediante el cual se realiza el
 * envío de un determinado mensaje basado en el protocolo STOMP.
 *
 * @author Robert Ene
 */
interface SendMessageInteractor : Interactor {

    /**
     * Interfaz que es utilizada por el interactor para notificar los resultados de las
     * operaciones que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface Callback : BaseCallbackInteractor {

        /**
         * Método que notifica que el mensaje se ha enviado correctamente a un determinado
         * tema ("topic").
         */
        fun onMessageSent()

        /**
         * Método que notifica que se ha producido un error durante el proceso de envío de un
         * determinado mensaje.
         *
         * @param message el mensaje que no ha sido enviado.
         */
        fun onMessageSentError(message: Message)
    }
}
