package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.impl.GetUserInteractorImpl
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.Role
import com.robert.android.lostpets.domain.model.types.UserStatus
import com.robert.android.lostpets.domain.repository.SessionRepository
import com.robert.android.lostpets.utilTest.threading.TestMainThread
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner

/**
 * Clase test para la clase GetUserInteractorImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.impl.GetUserInteractorImpl
 */
@RunWith(MockitoJUnitRunner::class)
class GetUserInteractorTest {

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: GetUserInteractor.Callback
    @Mock
    private lateinit var mSessionRepository: SessionRepository

    private lateinit var mMainThread: MainThread

    private var mUser: User? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun testGetUser() {
        mUser = User(99, "username@email.com",
                "username", Role.USER, UserStatus.ENABLED,
                "669910272", "LastName", "UserName")

        `when`(mSessionRepository.getUser()).thenReturn(mUser)

        GetUserInteractorImpl(mExecutor, mMainThread, mCallback, mSessionRepository).run()

        verify(mSessionRepository).getUser()
        verify(mCallback).onUserRetrieved(mUser!!)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun testGetUserNull() {
        mUser = null

        `when`(mSessionRepository.getUser()).thenReturn(mUser)

        GetUserInteractorImpl(mExecutor, mMainThread, mCallback, mSessionRepository).run()

        verify(mSessionRepository).getUser()
        verifyNoMoreInteractions(mCallback)
    }
}
