package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.interactors.base.BaseCallbackInteractor
import com.robert.android.lostpets.domain.interactors.base.Interactor

/**
 * Interfaz del interactor que se encarga de comprobar que el correo electrónico proporcionado por
 * el usuario es único.
 *
 * @author Robert Ene
 */
interface CheckUniqueEmailInteractor : Interactor {

    /**
     * Interfaz que es utilizada por el interactor para notificar los resultados de las
     * operaciones que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface Callback : BaseCallbackInteractor {

        /**
         * Método que notifica si el correo electrónico proporcionado por el usuario es único.
         *
         * @param isUnique true si el correo electrónico es único, false de lo contrario.
         */
        fun onCheckUniqueEmail(isUnique: Boolean)
    }
}
