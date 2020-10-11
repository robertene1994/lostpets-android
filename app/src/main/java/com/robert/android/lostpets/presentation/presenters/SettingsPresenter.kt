package com.robert.android.lostpets.presentation.presenters

import android.widget.SpinnerAdapter
import com.robert.android.lostpets.presentation.presenters.base.BasePresenter
import com.robert.android.lostpets.presentation.presenters.base.BaseViewPresenter

/**
 * Interfaz del presenter que se encarga de coordinar la vista asociada a la las preferencias
 * (ajustes) del usuario para la aplicación.
 *
 * @author Robert Ene
 */
interface SettingsPresenter : BasePresenter {

    /**
     * Interfaz que es utilizada por el presenter para notificar los resultados de las operaciones
     * que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface View : BaseViewPresenter {

        /**
         * Método que notifica que se ha recuperado la preferencia del usuario.
         *
         * @param language la preferencia del usuario sobre el idioma de la aplicación.
         */
        fun onLanguageRetrieved(language: String)
    }

    /**
     * Método que devuelve el adapter para los idiomas disponibles en la aplicación en función del
     * idioma establecido.
     *
     * @param locale el idioma establecido por el usuario.
     * @return el adapter de los idiomas disponibles.
     */
    fun getLanguagesAdapter(locale: String): SpinnerAdapter

    /**
     * Método que devuelve el índice dentro de una lista de un determinado idioma.
     *
     * @param locale el idioma establecido.
     * @return el índice del idioma establecido.
     */
    fun getIndexByLocale(locale: String): Int

    /**
     * Método que recupera la preferencia del usuario que indica el idioma de la aplicación.
     */
    fun getLanguage()

    /**
     * Método que guarda la preferencia del usuario que indica el idioma de la aplicación y
     * comprueba el índice de la misma dentro de una lista (para decidir si se actualiza el
     * idioma de la aplicación o no).
     *
     * @param language la preferencia del usuario sobre el idioma de la aplicación.
     * @param index el índice del idioma dentro de la lista.
     * @return true si la aplicación debe actualizar el idioma, false de lo contrario.
     */
    fun saveLanguageByIndex(language: String, index: Int): Boolean
}
