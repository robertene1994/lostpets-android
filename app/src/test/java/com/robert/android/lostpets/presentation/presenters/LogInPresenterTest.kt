package com.robert.android.lostpets.presentation.presenters

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.Role
import com.robert.android.lostpets.domain.model.types.UserStatus
import com.robert.android.lostpets.domain.repository.SessionRepository
import com.robert.android.lostpets.network.service.UserService
import com.robert.android.lostpets.presentation.presenters.impl.LogInPresenterImpl
import com.robert.android.lostpets.utilTest.threading.TestMainThread
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner

/**
 * Clase test para la clase LogInPresenterImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.impl.LogInPresenterImpl
 */
@RunWith(MockitoJUnitRunner::class)
class LogInPresenterTest {

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: LogInPresenter.View
    @Mock
    private lateinit var mSessionRepository: SessionRepository
    @Mock
    private lateinit var mContext: Context
    @Mock
    private lateinit var mService: UserService

    private lateinit var mMainThread: MainThread

    private lateinit var mEmail: String
    private lateinit var mPassword: String
    private lateinit var mUser: User

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()

        mEmail = "username@email.com"
        mPassword = "username"
        mUser = User(99, mEmail,
                null, Role.USER, UserStatus.ENABLED,
                "669910272", "LastName", "UserName")
    }

    @Test
    @Throws(Exception::class)
    fun testCheckUserSession() {
        val presenter = LogInPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mSessionRepository)
        presenter.checkUserSession()

        spy(presenter).pause()
        spy(presenter).resume()
        spy(presenter).onUserLoggedIn()
        spy(presenter).stop()
        spy(presenter).destroy()

        verify(mCallback).showProgress()
        verify(mCallback).onUserLoggedIn()
        verifyNoMoreInteractions(mSessionRepository, mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testSuccessLogIn() {
        val presenter = LogInPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mSessionRepository)
        presenter.logIn(mEmail, mPassword)

        spy(presenter).onSuccessLogIn(mUser)

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).onSuccessLogIn(mUser)
        verifyNoMoreInteractions(mSessionRepository, mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testFailureLogIn() {
        val presenter = LogInPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mSessionRepository)
        presenter.logIn(mEmail, mPassword)

        spy(presenter).onFailureLogIn()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).onFailureLogIn()
        verifyNoMoreInteractions(mSessionRepository, mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testRoleNotAllowedLogIn() {
        mUser.role = Role.ADMIN

        val presenter = LogInPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mSessionRepository)
        presenter.logIn(mEmail, mPassword)

        spy(presenter).onRoleNotAllowedLogIn()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).onRoleNotAllowedLogIn()
        verifyNoMoreInteractions(mSessionRepository, mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testUserSessionExpired() {
        mUser.role = Role.ADMIN

        val presenter = LogInPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mSessionRepository)
        presenter.logIn(mEmail, mPassword)

        spy(presenter).onUserSessionExpired()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).onUserSessionExpired()
        verifyNoMoreInteractions(mSessionRepository, mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testUserNotLoggedIn() {
        val presenter = LogInPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mSessionRepository)
        presenter.logIn(mEmail, mPassword)

        spy(presenter).onUserNotLoggedIn()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verifyNoMoreInteractions(mSessionRepository, mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testNoInternetConnection() {
        val presenter = LogInPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mSessionRepository)
        presenter.logIn(mEmail, mPassword)

        spy(presenter).onNoInternetConnection()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).noInternetConnection()
        verifyNoMoreInteractions(mSessionRepository, mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testServiceNotAvailable() {
        val presenter = LogInPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mSessionRepository)
        presenter.logIn(mEmail, mPassword)

        spy(presenter).onServiceNotAvailable()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).serviceNotAvailable()
        verifyNoMoreInteractions(mSessionRepository, mCallback)
    }
}
