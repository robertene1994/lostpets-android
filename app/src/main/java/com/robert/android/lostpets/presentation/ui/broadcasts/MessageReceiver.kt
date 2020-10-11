package com.robert.android.lostpets.presentation.ui.broadcasts

import com.robert.android.lostpets.domain.model.Message

/**
 * Interfaz base para todos los broadcasts de la aplicación. Su función consiste en transmitir
 * mensajes a los componentes que se encuentran suscritos.
 *
 * @author Robert Ene
 */
interface MessageReceiver {

    /**
     * Método que transmite un determinado mensaje.
     *
     * @param message el mensaje que se debe transmitir.
     */
    fun onMessage(message: Message)
}
