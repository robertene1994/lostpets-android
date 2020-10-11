package com.robert.android.lostpets.presentation.presenters.impl

import android.content.Context
import android.os.Handler
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.ChatDetailInteractor
import com.robert.android.lostpets.domain.interactors.SendMessageInteractor
import com.robert.android.lostpets.domain.interactors.impl.ChatDetailInteractorImpl
import com.robert.android.lostpets.domain.interactors.impl.SendMessageInteractorImpl
import com.robert.android.lostpets.domain.model.Chat
import com.robert.android.lostpets.domain.model.Message
import com.robert.android.lostpets.domain.model.types.MessageStatus
import com.robert.android.lostpets.network.message.MessagingService
import com.robert.android.lostpets.network.service.MessageService
import com.robert.android.lostpets.presentation.presenters.ChatDetailPresenter
import com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter

/**
 * Clase que extiende la clase AbstractPresenter e implementa la interfaz ChatDetailPresenter.
 * Implementa las interfaces de los callbacks de los interactors ChatDetailInteractor,
 * SendMessageInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter
 * @see com.robert.android.lostpets.presentation.presenters.ChatDetailPresenter
 * @see com.robert.android.lostpets.domain.interactors.ChatDetailInteractor.Callback
 * @see com.robert.android.lostpets.domain.interactors.SendMessageInteractor.Callback
 */
class ChatDetailPresenterImpl(
    executor: Executor,
    mainThread: MainThread,
    view: ChatDetailPresenter.View,
    context: Context,
    service: MessageService,
    chat: Chat
) :
    AbstractPresenter(executor, mainThread), ChatDetailPresenter, ChatDetailInteractor.Callback,
        SendMessageInteractor.Callback {

    private val mView: ChatDetailPresenter.View = view
    private val mContext: Context = context
    private val mService: MessageService = service
    private val mChat: Chat = chat
    private var mViewIsVisible: Boolean = false

    override fun resume() {
        getChatMessages()
        mViewIsVisible = true
    }

    override fun pause() {
        mViewIsVisible = false
    }

    override fun stop() {
        mViewIsVisible = false
    }

    override fun destroy() {
        mViewIsVisible = false
    }

    override fun getChatMessages() {
        mView.showProgress()
        if (mChat.id != null)
            ChatDetailInteractorImpl(mExecutor, mMainThread,
                    this, mContext, mService, mChat).execute()
        else
            onChatMessagesEmpty()
    }

    override fun sendMessage(message: Message) {
        val stompClient = MessagingService.getStompClient()
        SendMessageInteractorImpl(mExecutor, mMainThread, stompClient,
                this, message, mChat.toUser).execute()
    }

    override fun onChatMessagesRetrieved(messages: List<Message>) {
        mView.hideProgress()
        mView.showChatMessages(messages)
    }

    override fun onChatMessagesEmpty() {
        mView.hideProgress()
        mView.onChatMessagesEmpty()
    }

    override fun onMessageReceived(message: Message) {
        val stompClient = MessagingService.getStompClient()
        if (mChat.code == message.chat?.code) {
            if (mChat.fromUser.id == message.toUser.id &&
                    message.messageStatus == MessageStatus.SENT) {
                mView.messageReceived(message)

                message.messageStatus = MessageStatus.DELIVERED
                SendMessageInteractorImpl(mExecutor, mMainThread, stompClient,
                        this, message, mChat.toUser).execute()

                if (mViewIsVisible) {
                        message.messageStatus = MessageStatus.READ
                    Handler().postDelayed({
                        SendMessageInteractorImpl(mExecutor, mMainThread, stompClient,
                                this, message, mChat.toUser).execute()
                    }, 300)
                } else
                    mView.messageReceivedForOtherChat(message)
            } else
                mView.messageStatusUpdated(message)
        } else {
            if (message.messageStatus == MessageStatus.SENT)
                mView.messageReceivedForOtherChat(message)
        }
    }

    override fun onMessageSent() {
        // mensaje enviado
    }

    override fun onMessageSentError(message: Message) {
        mView.messageSentError()
        sendMessage(message)
    }

    override fun onNoInternetConnection() {
        mView.hideProgress()
        mView.noInternetConnection()
    }

    override fun onServiceNotAvailable() {
        mView.hideProgress()
        mView.serviceNotAvailable()
    }
}
