package com.robert.android.lostpets.presentation.presenters

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.network.service.UserService
import com.robert.android.lostpets.presentation.presenters.impl.SignUpPresenterImpl
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
 * Clase test para la clase SignUpPresenterImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.impl.SignUpPresenterImpl
 */
@RunWith(MockitoJUnitRunner::class)
class SignUpPresenterTest {

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: SignUpPresenter.View
    @Mock
    private lateinit var mContext: Context
    @Mock
    private lateinit var mService: UserService
    @Mock
    private lateinit var mUser: User

    private lateinit var mMainThread: MainThread
    private lateinit var mUserEmail: String

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()
        mUserEmail = "user@email.com"
    }

    @Test
    @Throws(Exception::class)
    fun testCheckUniqueEmail() {
        val presenter = SignUpPresenterImpl(mExecutor, mMainThread, mCallback, mContext, mService)
        presenter.checkUniqueEmail(mUserEmail)

        spy(presenter).pause()
        spy(presenter).resume()
        spy(presenter).onCheckUniqueEmail(true)
        spy(presenter).stop()
        spy(presenter).destroy()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).onCheckUniqueEmail(true)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testSignUp() {
        val presenter = SignUpPresenterImpl(mExecutor, mMainThread, mCallback, mContext, mService)
        presenter.signUp(mUser)

        spy(presenter).onSignUp(mUser)

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).onSignUp(mUser)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testNoInternetConnection() {
        val presenter = SignUpPresenterImpl(mExecutor, mMainThread, mCallback, mContext, mService)
        presenter.signUp(mUser)

        spy(presenter).onNoInternetConnection()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).noInternetConnection()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testServiceNotAvailable() {
        val presenter = SignUpPresenterImpl(mExecutor, mMainThread, mCallback, mContext, mService)
        presenter.signUp(mUser)

        spy(presenter).onServiceNotAvailable()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).serviceNotAvailable()
        verifyNoMoreInteractions(mCallback)
    }
}
