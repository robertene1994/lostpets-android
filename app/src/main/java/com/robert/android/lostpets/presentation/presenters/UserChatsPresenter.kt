package com.robert.android.lostpets.presentation.presenters

import com.robert.android.lostpets.domain.model.Chat
import com.robert.android.lostpets.presentation.presenters.base.BasePresenter
import com.robert.android.lostpets.presentation.presenters.base.BaseViewPresenter

/**
 * Interfaz del presenter que se encarga de coordinar la vista asociada a la recuperación de
 * los chats de un determinado usuario.
 *
 * @author Robert Ene
 */
interface UserChatsPresenter : BasePresenter {

    /**
     * Interfaz que es utilizada por el presenter para notificar los resultados de las operaciones
     * que tiene asignadas.
     *
     * @author Robert Ene
     */
    interface View : BaseViewPresenter {

        /**
         * Método que notifica que los chats de un determinado usuario se pueden mostrar.
         *
         * @param chats los chats del usuario establecido.
         */
        fun showUserChats(chats: List<Chat>)

        /**
         * Método que notifica al usuario que no tiene chats.
         */
        fun onUserChatsEmpty()
    }

    /**
     * Método que recupera los chats de un determinado usuario.
     */
    fun getUserChats()
}
