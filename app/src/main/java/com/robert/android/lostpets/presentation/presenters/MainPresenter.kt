package com.robert.android.lostpets.presentation.presenters

import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.presentation.presenters.base.BasePresenter
import com.robert.android.lostpets.presentation.presenters.base.BaseViewPresenter

/**
 * Interfaz del presenter que se encarga de coordinar la vista asociada al menú deslizable.
 *
 * @author Robert Ene
 */
interface MainPresenter : BasePresenter {

    /**
     * Interfaz que es utilizada por el presenter para notificar los resultados de las operaciones
     * que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface View : BaseViewPresenter {

        /**
         * Método que notifica que los datos del usuario con la sesión iniciada han sido
         * recuperados.
         *
         * @param user los datos del usuario con la sesión iniciada.
         */
        fun onUserRetrieved(user: User)

        /**
         * Método que notifica que la sesión del usuario se ha cerrado correctamente.
         */
        fun onLogOut()
    }

    /**
     * Método que recupera los datos de la sesión del usuario.
     */
    fun getUser()

    /**
     * Método que inicia el proceso para cerrar la sesión del usuario.
     */
    fun logOut()
}
