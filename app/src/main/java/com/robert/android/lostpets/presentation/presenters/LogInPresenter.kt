package com.robert.android.lostpets.presentation.presenters

import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.presentation.presenters.base.BasePresenter
import com.robert.android.lostpets.presentation.presenters.base.BaseViewPresenter

/**
 * Interfaz del presenter que se encarga de coordinar la vista asociada al inicio de sesión del
 * usuario.
 *
 * @author Robert Ene
 */
interface LogInPresenter : BasePresenter {

    /**
     * Interfaz que es utilizada por el presenter para notificar los resultados de las
     * operaciones que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface View : BaseViewPresenter {

        /**
         * Método que notifica que no se ha podido iniciar sesión.
         */
        fun onFailureLogIn()

        /**
         * Método que notifica que el usuario que intenta iniciar sesión no tiene el rol de
         * usuario.
         */
        fun onRoleNotAllowedLogIn()

        /**
         * Método que notifica que se ha iniciado sesión con éxito.
         *
         * @param user los datos del usuario que ha iniciado sesión.
         */
        fun onSuccessLogIn(user: User)

        /**
         * Método que notifica que el usuario tiene una sesión iniciada en la aplicación.
         */
        fun onUserLoggedIn()

        /**
         * Método que notifica que la sesión iniciada por el usuario ha caducado.
         */
        fun onUserSessionExpired()

        /**
         * Método que notifica que la cuenta del usuario ha sido inhabilitada.
         */
        fun onUserDisabled()
    }

    /**
     * Método que comprueba si el usuario ya tiene una sesión iniciada en la aplicación.
     */
    fun checkUserSession()

    /**
     * Método que permite al usuario iniciar sesión en la aplicación a partir de sus credenciales.
     *
     * @param email el correo electrónico del usuario.
     * @param password la contraseña del usuario.
     */
    fun logIn(email: String, password: String)
}
