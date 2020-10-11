package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.interactors.base.BaseCallbackInteractor
import com.robert.android.lostpets.domain.interactors.base.Interactor

/**
 * Interfaz del interactor que se encarga de cerrar la sesión del usuario.
 *
 * @author Robert Ene
 */
interface LogOutInteractor : Interactor {

    /**
     * Interfaz que es utilizada por el interactor para notificar los resultados de las operaciones
     * que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface Callback : BaseCallbackInteractor {

        /**
         * Método que notifica que la sesión del usuario se ha cerrado correctamente.
         */
        fun onLogOut()
    }
}
