package com.robert.android.lostpets.presentation.presenters

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.model.Chat
import com.robert.android.lostpets.domain.model.Message
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.MessageStatus
import com.robert.android.lostpets.domain.model.types.Role
import com.robert.android.lostpets.domain.model.types.UserStatus
import com.robert.android.lostpets.network.service.MessageService
import com.robert.android.lostpets.presentation.presenters.impl.ChatDetailPresenterImpl
import com.robert.android.lostpets.utilTest.threading.TestMainThread
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

/**
 * Clase test para la clase ChatDetailPresenterImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.impl.ChatDetailPresenterImpl
 */
@RunWith(MockitoJUnitRunner::class)
class ChatDetailPresenterTest {

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: ChatDetailPresenter.View
    @Mock
    private lateinit var mContext: Context
    @Mock
    private lateinit var mService: MessageService
    @Mock
    private lateinit var mMessages: List<Message>

    private lateinit var mMainThread: MainThread
    private lateinit var mFromUser: User
    private lateinit var mToUser: User
    private lateinit var mChat: Chat
    private lateinit var mMessage: Message

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()

        mFromUser = User(23, "from@email.com",
                "from", Role.USER, UserStatus.ENABLED,
                "610029505", "From", "User")
        mToUser = User(24, "to@email.com",
                "to", Role.USER, UserStatus.ENABLED,
                "669910272", "To", "user")
        mChat = Chat(29, "COD29", mFromUser, mToUser, null, 0)
        mMessage = Message(23, "CODE23", "¡Buenos días!", Date(),
                MessageStatus.SENT, mToUser, mFromUser, mChat)
    }

    @Test
    @Throws(Exception::class)
    fun testChatMessagesRetrieved() {
        val presenter = ChatDetailPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mChat)

        spy(presenter).pause()
        spy(presenter).resume()
        spy(presenter).onChatMessagesRetrieved(mMessages)
        spy(presenter).stop()
        spy(presenter).destroy()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).showChatMessages(mMessages)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testChatMessagesEmptyNullChatId() {
        mChat.id = null

        val presenter = ChatDetailPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mChat)
        presenter.getChatMessages()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).onChatMessagesEmpty()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testMessageSent() {
        val presenter = ChatDetailPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mChat)
        presenter.sendMessage(mMessage)

        spy(presenter).onMessageSent()

        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testMessageSentError() {
        val presenter = ChatDetailPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mChat)
        presenter.sendMessage(mMessage)

        spy(presenter).onMessageSentError(mMessage)

        verify(mCallback).messageSentError()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testMessageReceivedMessageStatusSentForOtherChat() {

        val presenter = ChatDetailPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mChat)
        presenter.onMessageReceived(mMessage)

        verify(mCallback).messageReceived(mMessage)
        verify(mCallback).messageReceivedForOtherChat(mMessage)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testMessageReceivedMessageStatusReadViewIsVisible() {
        val presenter = ChatDetailPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mChat)

        presenter.resume()
        spy(presenter).onChatMessagesRetrieved(mMessages)

        presenter.onMessageReceived(mMessage)

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).showChatMessages(mMessages)
        verify(mCallback).messageReceived(mMessage)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testMessageStatusUpdatedSent() {
        mMessage.fromUser = mFromUser
        mMessage.toUser = mToUser

        val presenter = ChatDetailPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mChat)
        presenter.onMessageReceived(mMessage)

        verify(mCallback).messageStatusUpdated(mMessage)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testMessageStatusUpdatedDelivered() {
        mMessage.messageStatus = MessageStatus.DELIVERED

        val presenter = ChatDetailPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mChat)
        presenter.onMessageReceived(mMessage)

        verify(mCallback).messageStatusUpdated(mMessage)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testMessageStatusUpdatedRead() {
        mMessage.messageStatus = MessageStatus.READ

        val presenter = ChatDetailPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mChat)
        presenter.onMessageReceived(mMessage)

        verify(mCallback).messageStatusUpdated(mMessage)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testMessageReceivedForOtherChatSentMessageStatus() {
        mMessage.chat = Chat(101, "COD101", mToUser, mFromUser, null, 0)

        val presenter = ChatDetailPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mChat)
        presenter.onMessageReceived(mMessage)

        verify(mCallback).messageReceivedForOtherChat(mMessage)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testMessageReceivedForOtherChatDeliveredMessageStatus() {
        mMessage.messageStatus = MessageStatus.DELIVERED
        mMessage.chat = Chat(101, "COD101", mToUser, mFromUser, null, 0)

        val presenter = ChatDetailPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mChat)
        presenter.onMessageReceived(mMessage)

        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testMessageReceivedForOtherChatReadMessageStatus() {
        mMessage.messageStatus = MessageStatus.READ
        mMessage.chat = Chat(101, "COD101", mToUser, mFromUser, null, 0)

        val presenter = ChatDetailPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mChat)
        presenter.onMessageReceived(mMessage)

        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testNoInternetConnection() {
        val presenter = ChatDetailPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mChat)
        presenter.sendMessage(mMessage)

        spy(presenter).onNoInternetConnection()

        verify(mCallback).hideProgress()
        verify(mCallback).noInternetConnection()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testServiceNotAvailable() {
        val presenter = ChatDetailPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mChat)
        presenter.sendMessage(mMessage)

        spy(presenter).onServiceNotAvailable()

        verify(mCallback).hideProgress()
        verify(mCallback).serviceNotAvailable()
        verifyNoMoreInteractions(mCallback)
    }
}
