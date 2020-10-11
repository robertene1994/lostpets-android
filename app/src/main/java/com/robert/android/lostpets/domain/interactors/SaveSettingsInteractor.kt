package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.interactors.base.Interactor

/**
 * Interfaz del interactor que se encarga de guardar las preferencias (ajustes de la aplicación)
 * del usuario.
 *
 * @author Robert Ene
 */
interface SaveSettingsInteractor : Interactor {

    /**
     * Interfaz que es utilizada por el interactor para notificar los resultados de las operaciones
     * que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface Callback {

        /**
         * Método que notifica que las preferencias (ajustes de la aplicación) han sido guardadas
         * correctamente.
         */
        fun onSavedSettings()
    }
}
