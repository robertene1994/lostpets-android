package com.robert.android.lostpets.presentation.ui.broadcasts.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.robert.android.lostpets.domain.model.Message
import com.robert.android.lostpets.presentation.ui.broadcasts.MessageBroadcastReceiver

/**
 * Clase que extiende la clase BroadcastReceiver e implementa la interfaz MessageBroadcastReceiver.
 * Se encarga de transmitir mensajes a la vista de los chats o del detalle de un determinado chat,
 * siempre que se encuentre suscrito.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.ui.broadcasts.MessageBroadcastReceiver
 */
class MessageBroadcastReceiverImpl(messageBroadcastReceiver: MessageBroadcastReceiver) :
        BroadcastReceiver() {

    companion object {
        const val ACTION = "MessageBroadcastReceiverImpl::Action"
        const val CONNECTION_ERROR = "MessageBroadcastReceiverImpl::ConnectionError"
        const val FAILED_SERVER_HEARTBEAT = "MessageBroadcastReceiverImpl::FailedServerHeartbeat"
        const val MESSAGE_RECEIVED = "MessageBroadcastReceiverImpl::MessageReceived"
        const val MESSAGE_RECEIVED_ERROR = "MessageBroadcastReceiverImpl::MessageReceivedError"
    }

    private val mMessageBroadcastReceiver: MessageBroadcastReceiver = messageBroadcastReceiver

    override fun onReceive(context: Context?, intent: Intent?) {
        with(intent) {
            this?.let {
                getStringExtra(CONNECTION_ERROR)?.let {
                    mMessageBroadcastReceiver.onConnectionError()
                }
                getStringExtra(FAILED_SERVER_HEARTBEAT)?.let {
                    mMessageBroadcastReceiver.onFailedServerHeartbeat()
                }
                getParcelableExtra<Message>(MESSAGE_RECEIVED)?.let {
                    mMessageBroadcastReceiver.onMessage(it)
                }
                getStringExtra(MESSAGE_RECEIVED_ERROR)?.let {
                    mMessageBroadcastReceiver.onMessageReceivedError()
                }
            }
        }
    }
}
