package com.robert.android.lostpets.domain.interactors

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.google.gson.Gson
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.impl.LogInInteractorImpl
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.Role
import com.robert.android.lostpets.domain.model.types.UserStatus
import com.robert.android.lostpets.domain.repository.SessionRepository
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.timeout
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.verifyZeroInteractions
import org.mockito.junit.MockitoJUnitRunner

/**
 * Clase test para la clase LogInInteractorImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.impl.LogInInteractorImpl
 */
@RunWith(MockitoJUnitRunner::class)
class LogInInteractorTest {

    private companion object {
        const val SERVER_PORT = 64395
        const val RESPONSE_TIMEOUT = 3000L
    }

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: LogInInteractor.Callback
    @Mock
    private lateinit var mSessionRepository: SessionRepository
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

    private lateinit var mUserName: String
    private lateinit var mPassword: String
    private lateinit var mUser: User

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()
        mGson = GsonSerializer.instance
        mMockWebServer = MockWebServer()
        mMockWebServer.start(SERVER_PORT)
        mService = MockServiceGenerator.createService(mMockWebServer, UserService::class.java)

        mUserName = "username@email.com"
        mPassword = "username"

        mUser = User(95, mUserName,
                mPassword, Role.USER, UserStatus.ENABLED,
                "669910272", "LastName", "UserName")
    }

    @Test
    @Throws(Exception::class)
    fun testSuccessLogIn() {
        val token = "Bearer token"
        var response = MockResponse()
        response.addHeader("Authorization", token)
        mMockWebServer.enqueue(response)
        response = MockResponse()
        response.setBody(mGson.toJson(mUser))
        mMockWebServer.enqueue(response)

        LogInInteractorImpl(mExecutor, mMainThread, mCallback, mContext,
                mService, mSessionRepository, mUserName, mPassword).run()

        verify(mSessionRepository, timeout(RESPONSE_TIMEOUT)).saveUser(mUser)
        verify(mSessionRepository, timeout(RESPONSE_TIMEOUT)).saveToken(token)
        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onSuccessLogIn(mUser)
        verifyNoMoreInteractions(mSessionRepository, mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testRoleNotAllowedLogIn() {
        val token = "Bearer token"
        var response = MockResponse()
        response.addHeader("Authorization", token)
        mMockWebServer.enqueue(response)
        response = MockResponse()
        mUser.role = Role.ADMIN
        response.setBody(mGson.toJson(mUser))
        mMockWebServer.enqueue(response)

        LogInInteractorImpl(mExecutor, mMainThread, mCallback, mContext,
                mService, mSessionRepository, mUserName, mPassword).run()

        verify(mSessionRepository, timeout(RESPONSE_TIMEOUT)).saveToken(token)
        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onRoleNotAllowedLogIn()
        verifyNoMoreInteractions(mSessionRepository, mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testFailureLogIn() {
        val response = MockResponse()
        mMockWebServer.enqueue(response)

        LogInInteractorImpl(mExecutor, mMainThread, mCallback, mContext,
                mService, mSessionRepository, mUserName, mPassword).run()

        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onFailureLogIn()
        verifyNoMoreInteractions(mCallback)
        verifyZeroInteractions(mSessionRepository)
    }

    @Test
    @Throws(Exception::class)
    fun testNoInternetConnection() {
        val response = MockResponse()
        response.socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST
        mMockWebServer.enqueue(response)

        LogInInteractorImpl(mExecutor, mMainThread, mCallback, mContext,
                mService, mSessionRepository, mUserName, mPassword).run()

        `when`(mContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(mConnectivityManager)
        `when`(mConnectivityManager.activeNetworkInfo).thenReturn(mNetworkInfo)
        `when`(mNetworkInfo.isConnected).thenReturn(false)

        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onNoInternetConnection()
        verifyNoMoreInteractions(mCallback)
        verifyZeroInteractions(mSessionRepository)
    }

    @Test
    @Throws(Exception::class)
    fun testServiceNotAvailable() {
        val response = MockResponse()
        response.socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST
        mMockWebServer.enqueue(response)

        LogInInteractorImpl(mExecutor, mMainThread, mCallback, mContext,
                mService, mSessionRepository, mUserName, mPassword).run()

        `when`(mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mConnectivityManager)
        `when`(mConnectivityManager.activeNetworkInfo).thenReturn(mNetworkInfo)
        `when`(mNetworkInfo.isConnected).thenReturn(true)

        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onServiceNotAvailable()
        verifyNoMoreInteractions(mCallback)
        verifyZeroInteractions(mSessionRepository)
    }

    @Test
    @Throws(Exception::class)
    fun testLogInGetUserDetailsServiceNotAvailable() {
        val token = "Bearer token"
        var response = MockResponse()
        response.addHeader("Authorization", token)
        mMockWebServer.enqueue(response)
        response = MockResponse()
        mUser.role = Role.USER
        response.setBody(mGson.toJson(mUser))
        response.socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST
        mMockWebServer.enqueue(response)

        LogInInteractorImpl(mExecutor, mMainThread, mCallback, mContext,
                mService, mSessionRepository, mUserName, mPassword).run()

        `when`(mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mConnectivityManager)
        `when`(mConnectivityManager.activeNetworkInfo).thenReturn(mNetworkInfo)
        `when`(mNetworkInfo.isConnected).thenReturn(true)

        verify(mSessionRepository, timeout(RESPONSE_TIMEOUT)).saveToken(token)
        verify(mCallback, timeout(RESPONSE_TIMEOUT * 2)).onServiceNotAvailable()
        verifyNoMoreInteractions(mSessionRepository, mCallback)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        mMockWebServer.shutdown()
    }
}
