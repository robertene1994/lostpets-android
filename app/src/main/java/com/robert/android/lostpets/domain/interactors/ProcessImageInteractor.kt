package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.interactors.base.BaseCallbackInteractor
import com.robert.android.lostpets.domain.interactors.base.Interactor
import java.io.File

/**
 * Interfaz del interactor que se encarga de procesar la imagen de la mascota perdida
 * perteneciente a un determinado usuario.
 *
 * @author Robert Ene
 */
interface ProcessImageInteractor : Interactor {

    /**
     * Interfaz que es utilizada por el interactor para notificar los resultados de las
     * operaciones que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface Callback : BaseCallbackInteractor {

        /**
         * Método que notifica que la imagen de la mascota perdida ha sido procesada.
         *
         * @param file el fichero de la imagen procesada.
         */
        fun onProcessedImage(file: File)
    }
}
