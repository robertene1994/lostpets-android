package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.interactors.base.BaseCallbackInteractor
import com.robert.android.lostpets.domain.interactors.base.Interactor
import com.robert.android.lostpets.domain.model.User

/**
 * Interfaz del interactor que se encarga de registrar un usuario en el sistema.
 *
 * @author Robert Ene
 */
interface SignUpInteractor : Interactor {

    /**
     * Interfaz que es utilizada por el interactor para notificar los resultados de las
     * operaciones que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface Callback : BaseCallbackInteractor {

        /**
         * Método que notifica que el usuario se ha registrado con éxito en el sistema.
         *
         * @param user los datos del usuario que se ha registrado.
         */
        fun onSignUp(user: User)
    }
}
