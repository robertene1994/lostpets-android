package com.robert.android.lostpets.domain.interactors

import com.google.gson.Gson
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.impl.SendMessageInteractorImpl
import com.robert.android.lostpets.domain.model.Chat
import com.robert.android.lostpets.domain.model.Message
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.MessageStatus
import com.robert.android.lostpets.domain.model.types.Role
import com.robert.android.lostpets.domain.model.types.UserStatus
import com.robert.android.lostpets.network.converter.GsonSerializer
import com.robert.android.lostpets.utilTest.threading.TestMainThread
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.Date
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.StompCommand
import ua.naiksoftware.stomp.dto.StompMessage

/**
 * Clase test para la clase SendMessageInteractorImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.impl.SendMessageInteractorImpl
 */

@RunWith(MockitoJUnitRunner::class)
class SendMessageInteractorTest {

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: SendMessageInteractor.Callback
    @Mock
    private lateinit var mStompClient: StompClient

    private lateinit var mMessageObservable: Observable<StompMessage>
    private lateinit var mMainThread: MainThread
    private lateinit var mGson: Gson

    private lateinit var mMessage: Message

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()
        mGson = GsonSerializer.instance

        val fromUser = User(101, "from@email.com",
                "from", Role.USER, UserStatus.ENABLED,
                "610029505", "From", "User")
        val toUser = User(102, "to@email.com",
                "to", Role.USER, UserStatus.ENABLED,
                "669910272", "To", "user")

        val chat = Chat(1, "COD1", fromUser, toUser, null, 4)

        mMessage = Message(3, "CODE7", "¡Hola!", Date(),
                MessageStatus.SENT, fromUser, toUser, chat)
    }

    @Test
    @Throws(Exception::class)
    fun testMessageSent() {
        mMessageObservable = Observable.just(
                StompMessage(StompCommand.MESSAGE, null, mGson.toJson(mMessage)))

        `when`(mStompClient.send("/send/chatMessage/${mMessage.toUser.email}",
                mGson.toJson(mMessage))).thenReturn(Completable.fromAction { })

        SendMessageInteractorImpl(mExecutor, mMainThread, mStompClient,
                mCallback, mMessage, mMessage.toUser).run()

        verify(mCallback).onMessageSent()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testMessageSentError() {
        mMessageObservable = Observable.just(
                StompMessage(StompCommand.MESSAGE, null, mGson.toJson(mMessage)))

        `when`(mStompClient.send("/send/chatMessage/${mMessage.toUser.email}",
                mGson.toJson(mMessage))).thenReturn(Completable.fromAction {
            throw Exception("¡Error producido durante el proceso de envío del mensaje!")
        })

        SendMessageInteractorImpl(mExecutor,
                mMainThread, mStompClient, mCallback, mMessage, mMessage.toUser).run()

        verify(mCallback).onMessageSentError(mMessage)
        verifyNoMoreInteractions(mCallback)
    }
}
