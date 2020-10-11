package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.interactors.base.Interactor

/**
 * Interfaz del interactor que se encarga obtener la preferencia del usuario que indica el idioma
 * de la aplicación.
 *
 * @author Robert Ene
 */
interface GetLanguageInteractor : Interactor {

    /**
     * Interfaz que es utilizada por el interactor para notificar los resultados de las operaciones
     * que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface Callback {

        /**
         * Método que notifica que se ha recuperado la preferencia del usuario.
         *
         * @param language la preferencia del usuario sobre el idioma de la aplicación.
         */
        fun onLanguageRetrieved(language: String)
    }
}
