package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.interactors.base.BaseCallbackInteractor
import com.robert.android.lostpets.domain.interactors.base.Interactor

/**
 * Interfaz del interactor que se encarga de manejar el ciclo de vida de la conexión basada en el
 * protocolo STOMP.
 *
 * @author Robert Ene
 */
interface HandleStompConnectionInteractor : Interactor {

    /**
     * Interfaz que es utilizada por el interactor para notificar los resultados de las
     * operaciones que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface Callback : BaseCallbackInteractor {

        /**
         * Método que notifica que se ha producido un error durante la conexión mediante el
         * protocolo STOMP.
         */
        fun onConnectionError()

        /**
         * Método que notifica que la conexión basada en el protocolo STOMP con el servidor se ha
         * interrumpido.
         */
        fun onFailedServerHeartbeat()
    }
}
