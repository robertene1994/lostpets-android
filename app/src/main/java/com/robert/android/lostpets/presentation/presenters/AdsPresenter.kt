package com.robert.android.lostpets.presentation.presenters

import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.presentation.presenters.base.BasePresenter
import com.robert.android.lostpets.presentation.presenters.base.BaseViewPresenter

/**
 * Interfaz del presenter que se encarga de coordinar la vista asociada a la recuperación de
 * todos los anuncios de mascotas perdidas.
 *
 * @author Robert Ene
 */
interface AdsPresenter : BasePresenter {

    /**
     * Interfaz que es utilizada por el presenter para notificar los resultados de las operaciones
     * que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface View : BaseViewPresenter {

        /**
         * Método que notifica que los anuncios de mascotas perdidas se pueden mostrar.
         *
         * @param ads los anuncios de mascotas perdidas.
         */
        fun showAds(ads: List<Ad>)

        /**
         * Método que notifica al usuario que no existen anuncios de mascotas perdidas.
         */
        fun onAdsEmpty()
    }

    /**
     * Método que recupera todos los anuncios de mascotas perdidas.
     */
    fun getAds()
}
