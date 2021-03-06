package com.robert.android.lostpets.domain.interactors

/**
 * Interfaz del interactor síncrono que se encarga obtener la preferencia del usuario que
 * indica el idioma de la aplicación. Se utiliza únicamente para establecer el idioma de la
 * aplicación (no es posible utilizar métodos asíncronos durante la creación de las activities de
 * la aplicación).
 *
 * @author Robert Ene
 */
interface GetLanguageSynchronousInteractor {

    /**
     * Método que recupera el idioma establecido por el usuario como preferencia.
     *
     * @return el idioma de la aplicación establecido como preferencia por el usuario.
     */
    fun getLanguage(): String
}
