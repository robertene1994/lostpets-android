package com.robert.android.lostpets.domain.interactors

import com.google.gson.Gson
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.impl.HandleUserTopicConnectionInteractorImpl
import com.robert.android.lostpets.domain.model.Chat
import com.robert.android.lostpets.domain.model.Message
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.MessageStatus
import com.robert.android.lostpets.domain.model.types.Role
import com.robert.android.lostpets.domain.model.types.UserStatus
import com.robert.android.lostpets.domain.repository.SessionRepository
import com.robert.android.lostpets.network.converter.GsonSerializer
import com.robert.android.lostpets.utilTest.threading.TestMainThread
import io.reactivex.BackpressureStrategy
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
 * Clase test para la clase HandleUserTopicConnectionInteractorImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.impl.HandleUserTopicConnectionInteractorImpl
 */
@RunWith(MockitoJUnitRunner::class)
class HandleUserTopicConnectionInteractorTest {

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: HandleUserTopicConnectionInteractor.Callback
    @Mock
    private lateinit var mSessionRepository: SessionRepository
    @Mock
    private lateinit var mStompClient: StompClient

    private lateinit var mMessageObservable: Observable<StompMessage>
    private lateinit var mMainThread: MainThread
    private lateinit var mGson: Gson

    private lateinit var mUser: User
    private lateinit var mMessage: Message

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()
        mGson = GsonSerializer.instance

        val fromUser = User(101, "from@email.com",
                "from", Role.USER, UserStatus.ENABLED,
                "610029505", "From", "User")
        mUser = User(102, "to@email.com",
                "to", Role.USER, UserStatus.ENABLED,
                "669910272", "To", "user")

        val chat = Chat(1, "COD1", fromUser, mUser, null, 12)

        mMessage = Message(3, "CODE7", "¡Hola!", Date(),
                MessageStatus.SENT, fromUser, mUser, chat)
    }

    @Test
    @Throws(Exception::class)
    fun testMessageReceived() {
        mMessageObservable = Observable.just(
                StompMessage(StompCommand.MESSAGE, null, mGson.toJson(mMessage)))

        `when`(mSessionRepository.getUser()).thenReturn(mUser)
        `when`(mStompClient.topic("/exchange/chatMessage/${mUser.email}"))
                .thenReturn(mMessageObservable.toFlowable(BackpressureStrategy.BUFFER))

        HandleUserTopicConnectionInteractorImpl(mExecutor, mMainThread,
                mStompClient, mSessionRepository, mCallback).run()

        verify(mCallback).onMessageReceived(mMessage)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testMessageReceivedError() {
        mMessageObservable = Observable.just(
                StompMessage(StompCommand.MESSAGE, null, null))

        `when`(mSessionRepository.getUser()).thenReturn(mUser)
        `when`(mStompClient.topic("/exchange/chatMessage/${mUser.email}"))
                .thenReturn(mMessageObservable.toFlowable(BackpressureStrategy.BUFFER))

        HandleUserTopicConnectionInteractorImpl(mExecutor, mMainThread,
                mStompClient, mSessionRepository, mCallback).run()

        verify(mCallback).onMessageReceivedError()
        verifyNoMoreInteractions(mCallback)
    }
}
