package com.robert.android.lostpets.presentation.presenters

import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.presentation.presenters.base.BasePresenter
import com.robert.android.lostpets.presentation.presenters.base.BaseViewPresenter

/**
 * Interfaz del presenter que se encarga de coordinar la vista asociada a la recuperación de
 * los anuncios de mascotas perdidas de un determinado usuario.
 *
 * @author Robert Ene
 */
interface UserAdsPresenter : BasePresenter {

    /**
     * Interfaz que es utilizada por el presenter para notificar los resultados de las operaciones
     * que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface View : BaseViewPresenter {

        /**
         * Método que notifica que los anuncios de mascotas perdidas de un determinado usuario se
         * pueden mostrar.
         *
         * @param ads los anuncios de mascotas perdidas del usuario establecido.
         */
        fun showUserAds(ads: List<Ad>)

        /**
         * Método que notifica al usuario que no tiene anuncios de mascotas perdidas.
         */
        fun onUserAdsEmpty()

        /**
         * Método que notifica al usuario de que tiene anuncios publicados que han sido
         * inhabilitados.
         */
        fun onUserDisabledAds()
    }

    /**
     * Método que recupera los anuncios de mascotas perdidas de un determinado usuario.
     */
    fun getUserAds()
}
