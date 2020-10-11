package com.robert.android.lostpets.presentation.ui.activities

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import com.robert.android.lostpets.R
import com.robert.android.lostpets.domain.executor.impl.ThreadExecutor
import com.robert.android.lostpets.domain.model.Chat
import com.robert.android.lostpets.domain.model.Message
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.MessageStatus
import com.robert.android.lostpets.network.ServiceGenerator
import com.robert.android.lostpets.network.service.MessageService
import com.robert.android.lostpets.presentation.presenters.ChatDetailPresenter
import com.robert.android.lostpets.presentation.presenters.impl.ChatDetailPresenterImpl
import com.robert.android.lostpets.presentation.ui.activities.base.AbstractActivity
import com.robert.android.lostpets.presentation.ui.adapters.MessagesListAdapter
import com.robert.android.lostpets.presentation.ui.broadcasts.MessageBroadcastReceiver
import com.robert.android.lostpets.presentation.ui.broadcasts.impl.MessageBroadcastReceiverImpl
import com.robert.android.lostpets.presentation.ui.broadcasts.impl.MessagingServiceBroadcastReceiverImpl
import com.robert.android.lostpets.presentation.ui.notifications.NotificationsManager
import com.robert.android.lostpets.presentation.ui.utils.CodeGeneratorUtil
import com.robert.android.lostpets.presentation.ui.utils.SnackbarUtil
import com.robert.android.lostpets.threading.MainThreadImpl
import kotlinx.android.synthetic.main.activity_chat_detail.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.progress_bar.*
import java.util.*

/**
 * Activity que extiende la clase AbstractActivity e implementa la interfaz del callback del
 * presenter ChatDetailPresenter. Además, esta activity implementa la interfaz ChatDetailReceiver
 * con el objetivo de recibir mensajes procedentes del servicio correspondiente. Es el controlador
 * que se encarga de manejar la vista correspondiente al detalle de los chats de los usuarios.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.ui.activities.base.AbstractActivity
 * @see com.robert.android.lostpets.presentation.presenters.ChatDetailPresenter.View
 * @see com.robert.android.lostpets.presentation.ui.broadcasts.MessageBroadcastReceiver
 */
class ChatDetailActivity : AbstractActivity(), ChatDetailPresenter.View, MessageBroadcastReceiver {

    companion object {
        const val CHAT = "ChatDetailActivity::Chat"
        const val USER = "ChatDetailActivity::User"
    }

    private lateinit var mChatDetailPresenter: ChatDetailPresenter
    private lateinit var mMessageBroadcastReceiver: BroadcastReceiver
    private lateinit var mChat: Chat
    private lateinit var mUser: User
    private lateinit var mMessages: MutableList<Message>
    private lateinit var mMessage: Message

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_detail)
        loadExtras()
        init()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(mMessageBroadcastReceiver,
                IntentFilter(MessageBroadcastReceiverImpl.ACTION))
        mChatDetailPresenter.resume()
    }

    override fun onPause() {
        super.onPause()
        mChatDetailPresenter.pause()
    }

    override fun onStop() {
        super.onStop()
        mChatDetailPresenter.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMessageBroadcastReceiver)
        mChatDetailPresenter.destroy()
    }

    override fun showChatMessages(messages: List<Message>) {
        setRecyclerView(messages)
        messageEditText.apply {
            requestFocus()
            performClick()
        }
    }

    override fun onChatMessagesEmpty() {
        setRecyclerView()
        messageEditText.apply {
            requestFocus()
            performClick()
        }
    }

    override fun messageReceived(message: Message) {
        if (mMessages.indexOfFirst { m -> m.code == message.code } == -1) {
            mMessages.add(message.copy())
            chatDetailRecyclerView.adapter!!.notifyDataSetChanged()
            chatDetailRecyclerView.smoothScrollToPosition(mMessages.size - 1)
        }
    }

    override fun messageStatusUpdated(message: Message) {
        val messagePosition = mMessages.indexOfFirst { m -> m.code == message.code }
        mMessages[messagePosition].messageStatus = message.messageStatus
        chatDetailRecyclerView.adapter.notifyItemChanged(messagePosition)
    }

    override fun messageReceivedForOtherChat(message: Message) {
        sendBroadcast(Intent(MessagingServiceBroadcastReceiverImpl.ACTION).apply {
            putExtra(MessagingServiceBroadcastReceiverImpl.MESSAGE, message)
        })
    }

    override fun messageSentError() {
        SnackbarUtil.makeShort(activityChatDetailLayout, R.string.msg_messaging_message_sent_error)
    }

    override fun onMessage(message: Message) {
        mChatDetailPresenter.onMessageReceived(message)
    }

    override fun onConnectionError() {
        SnackbarUtil.makeShort(activityChatDetailLayout, R.string.msg_messaging_connection_error)
    }

    override fun onFailedServerHeartbeat() {
        SnackbarUtil.makeShort(activityChatDetailLayout, R.string.msg_messaging_failed_server_heartbeat)
    }

    override fun onMessageReceivedError() {
        SnackbarUtil.makeShort(activityChatDetailLayout,
                R.string.msg_messaging_message_received_error)
    }

    override fun noInternetConnection() {
        SnackbarUtil.makeShort(activityChatDetailLayout, R.string.msg_no_internet_connection)
    }

    override fun serviceNotAvailable() {
        SnackbarUtil.makeLong(activityChatDetailLayout, R.string.msg_service_not_avabile)
    }

    private fun loadExtras() {
        with(intent) {
            mChat = getParcelableExtra(CHAT)
            mUser = getParcelableExtra(USER)
        }
    }

    private fun init() {
        toolbar.title = String.format(
                getString(R.string.nav_user_profile_info),
                mChat.toUser.lastName, mChat.toUser.firstName)
        toolbar.setNavigationIcon(R.drawable.ic_user_account_color_white)
        setSupportActionBar(toolbar)

        setProgressBarLayout(progressBarConstraintLayout)
        addViewsToHide(chatDetailRecyclerView, messageEditText, sendMessageImageButton)

        mMessageBroadcastReceiver = MessageBroadcastReceiverImpl(this)

        mChatDetailPresenter = ChatDetailPresenterImpl(ThreadExecutor.instance,
                MainThreadImpl.instance, this, getContext(),
                ServiceGenerator.createService(this, MessageService::class.java), mChat)
        mChatDetailPresenter.getChatMessages()

        mMessage = Message(null, null, null, Date(),
                MessageStatus.SENT, mUser, mChat.toUser, mChat)

        sendMessageImageButton.isEnabled = false
        sendMessageImageButton.setOnClickListener { onClickSendMessage() }
        messageEditText.addTextChangedListener(onMessageTextChanged())
        messageEditText.setOnClickListener { onClickMessageText() }

        NotificationsManager.removeMessageNotification(mChat.toUser.id!!)
        with(NotificationManagerCompat.from(this)) {
            cancel(mChat.toUser.id!!.toInt())
        }
    }

    private fun onClickSendMessage() {
        val message = messageEditText.text.toString().trim()
        if (validateMessageContent(message)) {
            mMessage.apply {
                code = CodeGeneratorUtil.random()
                content = message
                date = Date()
            }
            mMessages.add(mMessage.copy())

            chatDetailRecyclerView.adapter!!.notifyDataSetChanged()
            chatDetailRecyclerView.smoothScrollToPosition(mMessages.size - 1)
            mChatDetailPresenter.sendMessage(mMessage)
            messageEditText.text.clear()
        }
    }

    private fun onMessageTextChanged(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sendMessageImageButton.isEnabled = s.toString().trim().isNotEmpty()
            }
        }
    }

    private fun onClickMessageText() {
        if (::mMessages.isInitialized && mMessages.isNotEmpty()) {
            Handler().postDelayed({
                chatDetailRecyclerView.smoothScrollToPosition(mMessages.size - 1)
            }, 200)
        }
    }

    private fun validateMessageContent(message: String): Boolean {
        return when {
            message.isEmpty() -> false
            else -> true
        }
    }

    private fun setRecyclerView(messages: List<Message>? = null) {
        mMessages = messages?.toMutableList() ?: mutableListOf()
        chatDetailRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatDetailActivity)
            adapter = MessagesListAdapter(mMessages, mUser)
            scrollToPosition(mMessages.size - 1)
        }
    }
}
