package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.interactors.base.BaseCallbackInteractor
import com.robert.android.lostpets.domain.interactors.base.Interactor
import com.robert.android.lostpets.domain.model.Ad

/**
 * Interfaz del interactor que se encarga de recuperar todos los anuncios de mascotas perdidas.
 *
 * @author Robert Ene
 */
interface AdsInteractor : Interactor {

    /**
     * Interfaz que es utilizada por el interactor para notificar los resultados de las operaciones
     * que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface Callback : BaseCallbackInteractor {

        /**
         * Método que notifica que los anuncios de mascotas perdidas han sido recuperadas.
         *
         * @param ads los anuncios de mascotas perdidas.
         */
        fun onAdsRetrieved(ads: List<Ad>)

        /**
         * Método que notifica que no existen anuncios de mascotas perdidas.
         */
        fun onAdsEmpty()
    }
}
