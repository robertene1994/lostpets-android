package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.interactors.base.BaseCallbackInteractor
import com.robert.android.lostpets.domain.interactors.base.Interactor
import com.robert.android.lostpets.domain.model.User

/**
 * Interfaz del interactor que se encarga de iniciar la sesión de un usuario a partir de sus
 * credenciales.
 *
 * @author Robert Ene
 */
interface LogInInteractor : Interactor {

    /**
     * Interfaz que es utilizada por el interactor para notificar los resultados de las
     * operaciones que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface Callback : BaseCallbackInteractor {

        /**
         * Método que notifica que se ha iniciado sesión con éxito utilizando las credenciales
         * del usuario.
         *
         * @param user los datos del usuario que ha iniciado sesión.
         */
        fun onSuccessLogIn(user: User)

        /**
         * Método que notifica que no se ha podido iniciar sesión utilizando las credenciales del
         * usuario dado o que son inválidos.
         */
        fun onFailureLogIn()

        /**
         * Método que notifica que el usuario que intenta iniciar sesión no tiene el rol de usuario
         * (puede ser el administrador).
         */
        fun onRoleNotAllowedLogIn()

        /**
         * Método que notifica que la cuenta del usuario ha sido inhabilitada.
         */
        fun onUserDisabled()
    }
}
