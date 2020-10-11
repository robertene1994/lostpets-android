package com.robert.android.lostpets.domain.interactors

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.google.gson.Gson
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.impl.UserAdsInteractorImpl
import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.domain.model.LatLng
import com.robert.android.lostpets.domain.model.Pet
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.AdStatus
import com.robert.android.lostpets.domain.model.types.PetStatus
import com.robert.android.lostpets.domain.model.types.Role
import com.robert.android.lostpets.domain.model.types.Sex
import com.robert.android.lostpets.domain.model.types.UserStatus
import com.robert.android.lostpets.network.MockServiceGenerator
import com.robert.android.lostpets.network.converter.GsonSerializer
import com.robert.android.lostpets.network.service.AdService
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
 * Clase test para la clase UserAdsInteractorImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.impl.UserAdsInteractorImpl
 */
@RunWith(MockitoJUnitRunner::class)
class UserAdsInteractorTest {

    private companion object {
        const val SERVER_PORT = 1000
        const val RESPONSE_TIMEOUT = 3000L
    }

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: UserAdsInteractor.Callback
    @Mock
    private lateinit var mConnectivityManager: ConnectivityManager
    @Mock
    private lateinit var mContext: Context
    @Mock
    private lateinit var mNetworkInfo: NetworkInfo

    private lateinit var mMainThread: MainThread
    private lateinit var mGson: Gson
    private lateinit var mMockWebServer: MockWebServer
    private lateinit var mService: AdService

    private lateinit var mUser: User
    private lateinit var mAds: List<Ad>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()
        mGson = GsonSerializer.instance
        mMockWebServer = MockWebServer()
        mMockWebServer.start(SERVER_PORT)
        mService = MockServiceGenerator.createService(mMockWebServer, AdService::class.java)

        val pet = Pet("Otto", "Perro", "Yorkshire",
                Sex.MALE, "Marrón y negro", "18236919")
        mUser = User(22, "username@email.com",
                "username", Role.USER, UserStatus.ENABLED,
                "669910272", "LastName", "UserName")
        val ad = Ad(31, "CODE31", Date(), AdStatus.ENABLED, PetStatus.LOST,
                100.00, LatLng(43.361914, -5.84938), pet,
                "Muy asustado", "CODE1.jpg", mUser)

        mAds = arrayListOf(ad)
    }

    @Test
    @Throws(Exception::class)
    fun testUserAdsRetrieved() {
        val response = MockResponse()
        response.setBody(mGson.toJson(mAds))
        mMockWebServer.enqueue(response)

        UserAdsInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mUser).run()

        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onUserAdsRetrieved(mAds)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testUserAdsEmpty() {
        val response = MockResponse()
        mAds = arrayListOf()
        response.setBody(mGson.toJson(mAds))
        mMockWebServer.enqueue(response)

        UserAdsInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mUser).run()

        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onUserAdsEmpty()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testUserAdsNull() {
        val response = MockResponse()
        response.setBody(mGson.toJson(null))
        mMockWebServer.enqueue(response)

        UserAdsInteractorImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mUser).run()

        verify(mCallback, timeout(RESPONSE_TIMEOUT)).onUserAdsEmpty()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testNoInternetConnection() {
        val response = MockResponse()
        response.socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST
        mMockWebServer.enqueue(response)

        UserAdsInteractorImpl(mExecutor, mMainThread,
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

        UserAdsInteractorImpl(mExecutor, mMainThread,
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
