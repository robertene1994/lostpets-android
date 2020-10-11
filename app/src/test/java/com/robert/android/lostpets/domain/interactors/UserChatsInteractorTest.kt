package com.robert.android.lostpets.domain.interactors

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.google.gson.Gson
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.impl.UserChatsInteractorImpl
import com.robert.android.lostpets.domain.model.Chat
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.Role
import com.robert.android.lostpets.domain.model.types.UserStatus
import com.robert.android.lostpets.network.MockServiceGenerator
import com.robert.android.lostpets.network.converter.GsonSerializer
import com.robert.android.lostpets.network.service.ChatService
import com.robert.android.lostpets.utilTest.threading.TestMainThread
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
 * Clase test para la clase UserChatsInteractorImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.impl.UserChatsInteractorImpl
 */
@RunWith(MockitoJUnitRunner::class)
class UserChatsInteractorTest {

    private companion object {
        const val SERVER_PORT = 64395
        const val RESPONSE_TIMEOUT = 3000L
    }

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: UserChatsInteractor.Callback
    @Mock
    private lateinit var mConnectivityManager: ConnectivityManager
    @Mock
    private lateinit var mContext: Context
    @Mock
    private lateinit var mNetworkInfo: NetworkInfo

    private lateinit var mMainThread: MainThread
    private lateinit var mGson: Gson
    private lateinit var mMockWebServer: MockWebServer
    private lateinit var mService: ChatService

    private lateinit var mUser: User
    private lateinit var mChats: List<Chat>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()
        mGson = GsonSerializer.instance
        mMockWebServer = MockWebServer()
        mMockWebServer.start(SERVER_PORT)
        mService = MockServiceGenerator.createService(mMockWebServer, ChatService::class.java)

        mUser = User(23, "from@email.com",
                "from", Role.USER, UserStatus.ENABLED,
                "610029505", "From", "User")
        val toUser = User(24, "to@email.com",
                "to", Role.USER, UserStatus.ENABLED,
                "669910272", "To", "user")
        val chat = Chat(29, "COD29", mUser, toUser, null, 0)

        mChats = arrayListOf(chat)
    }

    @Test
    @Throws(Exception::class)
    fun testUserChatsRetrieved() {
        val response = MockResponse()
        response.setBody(mGson.toJson(mChats))
        mMockWebServer.enqueue(response)

        UserChatsInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mUser).run()

        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onUserChatsRetrieved(mChats)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testUserChatsEmpty() {
        val response = MockResponse()
        mChats = arrayListOf()
        response.setBody(mGson.toJson(mChats))
        mMockWebServer.enqueue(response)

        UserChatsInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mUser).run()

        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onUserChatsEmpty()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testChatsAdsNull() {
        val response = MockResponse()
        response.setBody(mGson.toJson(null))
        mMockWebServer.enqueue(response)

        UserChatsInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mUser).run()

        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onUserChatsEmpty()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testNoInternetConnection() {
        val response = MockResponse()
        response.socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST
        mMockWebServer.enqueue(response)

        UserChatsInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mUser).run()

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

        UserChatsInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mUser).run()

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
