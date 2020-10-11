package com.robert.android.lostpets.presentation.ui.broadcasts.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.robert.android.lostpets.domain.model.Message
import com.robert.android.lostpets.presentation.ui.broadcasts.MessageReceiver

/**
 * Clase que extiende la clase BroadcastReceiver e implementa la interfaz MessageReceiver. Se
 * encarga de transmitir mensajes al servicio de mensajería, siempre que se encuentre suscrito.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.ui.broadcasts.MessageReceiver
 */
class MessagingServiceBroadcastReceiverImpl(messageReceiver: MessageReceiver) : BroadcastReceiver() {

    companion object {
        const val ACTION = "MessagingServiceBroadcastReceiverImpl::Action"
        const val MESSAGE = "MessagingServiceBroadcastReceiverImpl::Message"
    }

    private val mMessageReceiver: MessageReceiver = messageReceiver

    override fun onReceive(context: Context?, intent: Intent?) {
        with(intent) {
            this?.getParcelableExtra<Message>(MESSAGE)?.let {
                mMessageReceiver.onMessage(it)
            }
        }
    }
}
