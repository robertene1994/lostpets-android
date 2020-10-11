package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.interactors.base.BaseCallbackInteractor
import com.robert.android.lostpets.domain.interactors.base.Interactor

/**
 * Interfaz del interactor que se encarga de publicar un nuevo anuncio de una mascota perdida
 * perteneciente a un determinado usuario.
 *
 * @author Robert Ene
 */
interface AddAdInteractor : Interactor {

    /**
     * Interfaz que es utilizada por el interactor para notificar los resultados de las
     * operaciones que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface Callback : BaseCallbackInteractor {

        /**
         * Método que notifica que el anuncio de la mascota perdida ha sido publicado.
         *
         * @param adAdded true si el anuncio ha sido publicado, false de lo contrario.
         */
        fun onAddedAd(adAdded: Boolean)
    }
}
