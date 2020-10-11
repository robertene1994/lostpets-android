package com.robert.android.lostpets.presentation.presenters

import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.presentation.presenters.base.BasePresenter
import com.robert.android.lostpets.presentation.presenters.base.BaseViewPresenter

/**
 * Interfaz del presenter que se encarga de coordinar la vista asociada al registro del usuario.
 *
 * @author Robert Ene
 */
interface SignUpPresenter : BasePresenter {

    /**
     * Interfaz que es utilizada por el presenter para notificar los resultados de las
     * operaciones que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface View : BaseViewPresenter {

        /**
         * Método que notifica si el correo electrónico proporcionado por el usuario es único.
         *
         * @param isUnique true si el correo electrónico es único, false de lo contrario.
         */
        fun onCheckUniqueEmail(isUnique: Boolean)

        /**
         * Método que notifica el usuario se ha registrado con éxito.
         *
         * @param user los datos del usuario que se ha registrado.
         */
        fun onSignUp(user: User)
    }

    /**
     * Método que comprueba si el correo electrónico proporcionado por el usuario es único.
     *
     * @param email el correo electrónico proporcionado por el usuario.
     */
    fun checkUniqueEmail(email: String)

    /**
     * Método que inicia sesión en la aplicación a partir de sus credenciales.
     *
     * @param user los datos del usuario que se registra.
     */
    fun signUp(user: User)
}
