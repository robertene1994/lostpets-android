package com.robert.android.lostpets.presentation.presenters

import android.net.Uri
import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.presentation.presenters.base.BasePresenter
import com.robert.android.lostpets.presentation.presenters.base.BaseViewPresenter
import java.io.File

/**
 * Interfaz del interactor que se encarga de publicar un nuevo anuncio de una mascota perdida
 * perteneciente a un determinado usuario.
 *
 * @author Robert Ene
 */
interface AddAdPresenter : BasePresenter {

    /**
     * Interfaz que es utilizada por el presenter para notificar los resultados de las operaciones
     * que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface View : BaseViewPresenter {

        /**
         * Método que notifica que el anuncio de la mascota perdida ha sido publicado.
         *
         * @param adAdded true si el anuncio ha sido publicado, false de lo contrario.
         */
        fun onAdAdded(adAdded: Boolean)

        /**
         * Método que notifica que la imágen de la mascota perdida ha sido procesada.
         *
         * @param file el fichero de la imágen procesada.
         */
        fun onProcessedImage(file: File)
    }

    /**
     * Método que procesa la imagen de la mascota perdida.
     *
     * @param uri la ubicación en la que se ha almacenado la imagen de la mascota perdida.
     */
    fun processImage(uri: Uri)

    /**
     * Método que publica un nuevo anuncio de una mascota perdida de un usuario.
     *
     * @param ad el anuncio que se debe almacenar.
     * @param image la imagen de la mascota perdida.
     */
    fun addAd(ad: Ad, image: File)
}
