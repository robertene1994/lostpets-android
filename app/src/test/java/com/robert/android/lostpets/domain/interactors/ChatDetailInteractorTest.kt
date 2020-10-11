package com.robert.android.lostpets.domain.interactors

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.google.gson.Gson
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.impl.ChatDetailInteractorImpl
import com.robert.android.lostpets.domain.model.Chat
import com.robert.android.lostpets.domain.model.Message
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.MessageStatus
import com.robert.android.lostpets.domain.model.types.Role
import com.robert.android.lostpets.domain.model.types.UserStatus
import com.robert.android.lostpets.network.MockServiceGenerator
import com.robert.android.lostpets.network.converter.GsonSerializer
import com.robert.android.lostpets.network.service.MessageService
import com.robert.android.lostpets.utilTest.threading.TestMainThread
import java.util.Date
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.timeout
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner

/**
 * Clase test para la clase ChatDetailInteractorImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.impl.ChatDetailInteractorImpl
 */
@RunWith(MockitoJUnitRunner::class)
class ChatDetailInteractorTest {

    private companion object {
        const val SERVER_PORT = 64395
        const val RESPONSE_TIMEOUT = 3000L
    }

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: ChatDetailInteractor.Callback
    @Mock
    private lateinit var mConnectivityManager: ConnectivityManager
    @Mock
    private lateinit var mContext: Context
    @Mock
    private lateinit var mNetworkInfo: NetworkInfo

    private lateinit var mMainThread: MainThread
    private lateinit var mGson: Gson
    private lateinit var mMockWebServer: MockWebServer
    private lateinit var mService: MessageService

    private lateinit var mChat: Chat
    private lateinit var mMessages: List<Message>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()
        mGson = GsonSerializer.instance
        mMockWebServer = MockWebServer()
        mMockWebServer.start(SERVER_PORT)
        mService = MockServiceGenerator.createService(mMockWebServer, MessageService::class.java)

        val fromUser = User(98, "from@email.com",
                "from", Role.USER, UserStatus.ENABLED,
                "610029505", "From", "User")
        val toUser = User(99, "to@email.com",
                "to", Role.USER, UserStatus.ENABLED,
                "669910272", "To", "user")

        mChat = Chat(1, "COD1", fromUser, toUser, null, 2)

        val firstMessage = Message(7, "CODE7", "¡Hey!", Date(),
                MessageStatus.READ, fromUser, toUser, mChat)
        val secondMessage = Message(8, "CODE8", "¡Hola!", Date(),
                MessageStatus.DELIVERED, toUser, fromUser, mChat)
        val thirdMessage = Message(8, "CODE9", "¿Qué tal?", Date(),
                MessageStatus.SENT, fromUser, toUser, mChat)

        mMessages = arrayListOf(firstMessage, secondMessage, thirdMessage)
    }

    @Test
    @Throws(Exception::class)
    fun testChatMessagesRetrieved() {
        val response = MockResponse()
        response.setBody(mGson.toJson(mMessages))
        mMockWebServer.enqueue(response)

        ChatDetailInteractorImpl(mExecutor, mMainThread, mCallback, mContext, mService, mChat).run()

        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onChatMessagesRetrieved(mMessages)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testChatMessagesEmpty() {
        val response = MockResponse()
        mMessages = arrayListOf()
        response.setBody(mGson.toJson(mMessages))
        mMockWebServer.enqueue(response)

        ChatDetailInteractorImpl(mExecutor, mMainThread, mCallback, mContext, mService, mChat).run()

        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onChatMessagesEmpty()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testChatMessagesNull() {
        val response = MockResponse()
        response.setBody(mGson.toJson(null))
        mMockWebServer.enqueue(response)

        ChatDetailInteractorImpl(mExecutor, mMainThread, mCallback, mContext, mService, mChat).run()

        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onChatMessagesEmpty()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testNoInternetConnection() {
        val response = MockResponse()
        response.socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST
        mMockWebServer.enqueue(response)

        ChatDetailInteractorImpl(mExecutor, mMainThread, mCallback, mContext, mService, mChat).run()

        `when`(mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mConnectivityManager)
        `when`(mConnectivityManager.activeNetworkInfo).thenReturn(mNetworkInfo)
        `when`(mNetworkInfo.isConnected).thenReturn(false)

        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onNoInternetConnection()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testServiceNotAvailable() {
        val response = MockResponse()
        response.socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST
        mMockWebServer.enqueue(response)

        ChatDetailInteractorImpl(mExecutor, mMainThread, mCallback, mContext, mService, mChat).run()

        `when`(mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mConnectivityManager)
        `when`(mConnectivityManager.activeNetworkInfo).thenReturn(mNetworkInfo)
        `when`(mNetworkInfo.isConnected).thenReturn(true)

        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onServiceNotAvailable()
        verifyNoMoreInteractions(mCallback)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        mMockWebServer.shutdown()
    }
}
