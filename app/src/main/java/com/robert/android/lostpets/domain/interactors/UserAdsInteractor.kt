package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.interactors.base.BaseCallbackInteractor
import com.robert.android.lostpets.domain.interactors.base.Interactor
import com.robert.android.lostpets.domain.model.Ad

/**
 * Interfaz del interactor que se encarga de recuperar los anuncios de mascotas perdidas de un
 * determinado usuario.
 *
 * @author Robert Ene
 */
interface UserAdsInteractor : Interactor {

    /**
     * Interfaz que es utilizada por el interactor para notificar los resultados de las operaciones
     * que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface Callback : BaseCallbackInteractor {

        /**
         * Método que notifica que los anuncios de mascotas perdidas de un determinado usuario
         * han sido recuperados.
         *
         * @param ads los anuncios de mascotas perdidas del usuario establecido.
         */
        fun onUserAdsRetrieved(ads: List<Ad>)

        /**
         * Método que notifica que un determinado usuario no tiene anuncios de mascotas perdidas.
         */
        fun onUserAdsEmpty()
    }
}
