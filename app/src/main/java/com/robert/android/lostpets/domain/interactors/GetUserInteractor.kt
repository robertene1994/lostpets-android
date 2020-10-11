package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.interactors.base.BaseCallbackInteractor
import com.robert.android.lostpets.domain.interactors.base.Interactor
import com.robert.android.lostpets.domain.model.User

/**
 * Interfaz del interactor que se encarga de obtener los datos del usuario que ha iniciado sesión
 * en la aplicación.
 *
 * @author Robert Ene
 */
interface GetUserInteractor : Interactor {

    /**
     * Interfaz que es utilizada por el interactor para notificar los resultados de las operaciones
     * que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface Callback : BaseCallbackInteractor {

        /**
         * Método que notifica que se ha recuperado los datos de la sesión del usuario.
         *
         * @param user los datos del usuario que tiene la sesión iniciada.
         */
        fun onUserRetrieved(user: User)
    }
}
