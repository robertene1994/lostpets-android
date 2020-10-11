package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.interactors.base.BaseCallbackInteractor
import com.robert.android.lostpets.domain.interactors.base.Interactor

/**
 * Interfaz del interactor que se encarga de comprobar el estado de la sesión del usuario.
 *
 * @author Robert Ene
 */
interface UserSessionInteractor : Interactor {

    /**
     * Interfaz que es utilizada por el interactor para notifica los resultados de las
     * operaciones que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface Callback : BaseCallbackInteractor {

        /**
         * Método que notifica que el usuario tiene una sesión iniciada en la aplicación.
         */
        fun onUserLoggedIn()

        /**
         * Método que notifica que no hay ningún usuario con una sesión iniciada en la
         * aplicación.
         */
        fun onUserNotLoggedIn()

        /**
         * Método que notifica que la sesión iniciada por el usuario ha caducado.
         */
        fun onUserSessionExpired()

        /**
         * Método que notifica que la cuenta del usuario ha sido inhabilitada.
         */
        fun onUserDisabled()
    }
}
