package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.interactors.base.BaseCallbackInteractor
import com.robert.android.lostpets.domain.interactors.base.Interactor
import com.robert.android.lostpets.domain.model.Chat

/**
 * Interfaz del interactor que se encarga de recuperar los chats de un determinado usuario.
 *
 * @author Robert Ene
 */
interface UserChatsInteractor : Interactor {

    /**
     * Interfaz que es utilizada por el interactor para notificar los resultados de las operaciones
     * que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface Callback : BaseCallbackInteractor {

        /**
         * Método que notifica que los chats de un determinado usuario han sido recuperados.
         *
         * @param chats los chats del usuario establecido.
         */
        fun onUserChatsRetrieved(chats: List<Chat>)

        /**
         * Método que notifica que un determinado usuario no tiene chats.
         */
        fun onUserChatsEmpty()
    }
}
