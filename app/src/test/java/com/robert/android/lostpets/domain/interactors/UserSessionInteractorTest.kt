package com.robert.android.lostpets.domain.interactors

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.google.gson.Gson
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.impl.UserSessionInteractorImpl
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
import org.mockito.junit.MockitoJUnitRunner

/**
 * Clase test para la clase UserSessionInteractorImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.impl.UserSessionInteractorImpl
 */
@RunWith(MockitoJUnitRunner::class)
class UserSessionInteractorTest {

    private companion object {
        const val SERVER_PORT = 1000
        const val RESPONSE_TIMEOUT = 3000L
    }

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: UserSessionInteractor.Callback
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

    private var mUser: User? = null
    private var mToken: String? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()
        mGson = GsonSerializer.instance
        mMockWebServer = MockWebServer()
        mMockWebServer.start(SERVER_PORT)
        mService = MockServiceGenerator.createService(mMockWebServer, UserService::class.java)

        mUser = User(99, "username@email.com",
                "username", Role.USER, UserStatus.ENABLED,
                "669910272", "LastName", "UserName")
    }

    @Test
    @Throws(Exception::class)
    fun testUserLoggedIn() {
        val response = MockResponse()
        response.setBody(mGson.toJson("ENABLED"))
        mMockWebServer.enqueue(response)

        mToken = "eyJhbGciOiJIUzI1NiJ9" +
                ".eyJzdWIiOiJqb3NlQGVtYWlsLmNvbSIsImV4cCI6MTY4NjEwMTg5NCwicm9sZSI6IlVTRVIifQ" +
                ".BdAf2obyEm0pZOE_IQkZ7B2afQPiTCpBTGZTl-wEp90"

        `when`(mSessionRepository.getUser()).thenReturn(mUser)
        `when`(mSessionRepository.getToken()).thenReturn(mToken)

        UserSessionInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mSessionRepository).run()

        verify(mSessionRepository, timeout(RESPONSE_TIMEOUT)).getUser()
        verify(mSessionRepository, timeout(RESPONSE_TIMEOUT)).getToken()
        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onUserLoggedIn()
        verifyNoMoreInteractions(mSessionRepository, mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testUserSessionExpiredNotNullToken() {
        mToken = "Bearer token"

        `when`(mSessionRepository.getUser()).thenReturn(mUser)
        `when`(mSessionRepository.getToken()).thenReturn(mToken)

        UserSessionInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mSessionRepository).run()

        verify(mSessionRepository).getUser()
        verify(mSessionRepository).getToken()
        verify(mCallback).onUserSessionExpired()
        verifyNoMoreInteractions(mSessionRepository, mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testUserSessionExpiredEmptyToken() {
        mToken = "   "

        `when`(mSessionRepository.getUser()).thenReturn(mUser)
        `when`(mSessionRepository.getToken()).thenReturn(mToken)

        UserSessionInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mSessionRepository).run()

        verify(mSessionRepository).getUser()
        verify(mSessionRepository).getToken()
        verify(mCallback).onUserSessionExpired()
        verifyNoMoreInteractions(mSessionRepository, mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testUserSessionExpiredInvalidToken() {
        mToken = "Bearer eyJhbGciOiJIUzI1NiJ9" +
                ".eyJzdWIiOiJqb3NlQGVtYWlsLmNvbSIsImV4cCI6MTQ4NjEwMTg5NCwicm9sZSI6IlVTRVIifQ" +
                ".e_SBLTZ9_wTJrMsZWWhi37Yo1TbofyyrudVowbuXeCQ"

        `when`(mSessionRepository.getUser()).thenReturn(mUser)
        `when`(mSessionRepository.getToken()).thenReturn(mToken)

        UserSessionInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mSessionRepository).run()

        verify(mSessionRepository).getUser()
        verify(mSessionRepository).getToken()
        verify(mCallback).onUserSessionExpired()
        verifyNoMoreInteractions(mSessionRepository, mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testUserNotLoggedIn() {
        mUser = null
        mToken = null

        `when`(mSessionRepository.getUser()).thenReturn(mUser)
        `when`(mSessionRepository.getToken()).thenReturn(mToken)

        UserSessionInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mSessionRepository).run()

        verify(mSessionRepository).getUser()
        verify(mSessionRepository).getToken()
        verify(mCallback).onUserNotLoggedIn()
        verifyNoMoreInteractions(mSessionRepository, mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testNoInternetConnection() {
        mToken = "eyJhbGciOiJIUzI1NiJ9" +
                ".eyJzdWIiOiJqb3NlQGVtYWlsLmNvbSIsImV4cCI6MTY4NjEwMTg5NCwicm9sZSI6IlVTRVIifQ" +
                ".BdAf2obyEm0pZOE_IQkZ7B2afQPiTCpBTGZTl-wEp90"

        val response = MockResponse()
        response.setBody(mGson.toJson(false))
        response.socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST
        mMockWebServer.enqueue(response)

        `when`(mSessionRepository.getUser()).thenReturn(mUser)
        `when`(mSessionRepository.getToken()).thenReturn(mToken)

        UserSessionInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mSessionRepository).run()

        `when`(mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mConnectivityManager)
        `when`(mConnectivityManager.activeNetworkInfo).thenReturn(mNetworkInfo)
        `when`(mNetworkInfo.isConnected).thenReturn(false)

        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onNoInternetConnection()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testServiceNotAvailable() {
        mToken = "eyJhbGciOiJIUzI1NiJ9" +
                ".eyJzdWIiOiJqb3NlQGVtYWlsLmNvbSIsImV4cCI6MTY4NjEwMTg5NCwicm9sZSI6IlVTRVIifQ" +
                ".BdAf2obyEm0pZOE_IQkZ7B2afQPiTCpBTGZTl-wEp90"

        val response = MockResponse()
        response.setBody(mGson.toJson(false))
        response.socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST
        mMockWebServer.enqueue(response)

        `when`(mSessionRepository.getUser()).thenReturn(mUser)
        `when`(mSessionRepository.getToken()).thenReturn(mToken)

        UserSessionInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mSessionRepository).run()

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
