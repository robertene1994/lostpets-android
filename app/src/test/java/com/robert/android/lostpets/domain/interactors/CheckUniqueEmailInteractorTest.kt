package com.robert.android.lostpets.domain.interactors

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.google.gson.Gson
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.impl.CheckUniqueEmailInteractorImpl
import com.robert.android.lostpets.network.MockServiceGenerator
import com.robert.android.lostpets.network.converter.GsonSerializer
import com.robert.android.lostpets.network.service.UserService
import com.robert.android.lostpets.utilTest.threading.TestMainThread
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

/**
 * Clase test para la clase CheckUniqueEmailInteractorImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.impl.CheckUniqueEmailInteractorImpl
 */
@RunWith(MockitoJUnitRunner::class)
class CheckUniqueEmailInteractorTest {

    private companion object {
        const val SERVER_PORT = 1000
        const val RESPONSE_TIMEOUT = 3000L
    }

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: CheckUniqueEmailInteractor.Callback
    @Mock
    private lateinit var mConnectivityManager: ConnectivityManager
    @Mock
    private lateinit var mContext: Context
    @Mock
    private lateinit var mNetworkInfo: NetworkInfo

    private lateinit var mMainThread: MainThread
    private lateinit var mGson: Gson
    private lateinit var mMockWebServer: MockWebServer
    private lateinit var mService: UserService

    private lateinit var mEmail: String

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()
        mGson = GsonSerializer.instance
        mMockWebServer = MockWebServer()
        mMockWebServer.start(SERVER_PORT)
        mService = MockServiceGenerator.createService(mMockWebServer, UserService::class.java)

        mEmail = "user@email.com"
    }

    @Test
    @Throws(Exception::class)
    fun testCheckUniqueEmailSuccess() {
        val response = MockResponse()
        response.setBody(mGson.toJson(true))
        mMockWebServer.enqueue(response)

        CheckUniqueEmailInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mEmail).run()

        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onCheckUniqueEmail(true)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testCheckUniqueEmailFailure() {
        val response = MockResponse()
        response.setBody(mGson.toJson(false))
        mMockWebServer.enqueue(response)

        CheckUniqueEmailInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mEmail).run()

        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onCheckUniqueEmail(false)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testNoInternetConnection() {
        val response = MockResponse()
        response.setBody(mGson.toJson(false))
        response.socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST
        mMockWebServer.enqueue(response)

        CheckUniqueEmailInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mEmail).run()

        `when`(mContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(mConnectivityManager)
        `when`(mConnectivityManager.activeNetworkInfo).thenReturn(mNetworkInfo)
        `when`(mNetworkInfo.isConnected).thenReturn(false)

        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onNoInternetConnection()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun testServiceNotAvailable() {
        val response = MockResponse()
        response.setBody(mGson.toJson(false))
        response.socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST
        mMockWebServer.enqueue(response)

        CheckUniqueEmailInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mEmail).run()

        `when`(mContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(mConnectivityManager)
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
