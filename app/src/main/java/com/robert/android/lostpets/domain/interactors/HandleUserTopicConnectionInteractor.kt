package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.interactors.base.BaseCallbackInteractor
import com.robert.android.lostpets.domain.interactors.base.Interactor
import com.robert.android.lostpets.domain.model.Message

/**
 * Interfaz del interactor que se encarga de manejar la conexión del usuario basada en el protocolo
 * STOMP para un determinado tema ("topic") al que se encuentra suscrito.
 *
 * @author Robert Ene
 */
interface HandleUserTopicConnectionInteractor : Interactor {

    /**
     * Interfaz que es utilizada por el interactor para notificar los resultados de las
     * operaciones que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface Callback : BaseCallbackInteractor {

        /**
         * Método que notifica que se ha recibido un mensaje para un determinado tema ("topic").
         *
         * @param message el contenido del mensaje recibido.
         */
        fun onMessageReceived(message: Message)

        /**
         * Método que notifica que se ha producido un error con la suscripción del usuario a un
         * determinado tema ("topic). Este error se produce también si el usuario asociado a dicho
         * tema no puede recibir mensajes.
         */
        fun onMessageReceivedError()
    }
}
