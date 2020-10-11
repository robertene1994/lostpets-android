package com.robert.android.lostpets.presentation.presenters

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.model.Chat
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.network.service.ChatService
import com.robert.android.lostpets.presentation.presenters.impl.UserChatsPresenterImpl
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
 * Clase test para la clase UserChatsPresenterImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.impl.UserChatsPresenterImpl
 */
@RunWith(MockitoJUnitRunner::class)
class UserChatsPresenterTest {

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: UserChatsPresenter.View
    @Mock
    private lateinit var mContext: Context
    @Mock
    private lateinit var mService: ChatService
    @Mock
    private lateinit var mChats: List<Chat>
    @Mock
    private lateinit var mUser: User

    private lateinit var mMainThread: MainThread

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()
    }

    @Test
    @Throws(Exception::class)
    fun testUserChatsRetrieved() {
        val presenter = UserChatsPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mUser)

        spy(presenter).pause()
        spy(presenter).resume()
        spy(presenter).onUserChatsRetrieved(mChats)
        spy(presenter).stop()
        spy(presenter).destroy()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).showUserChats(mChats)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testUserChatsEmpty() {
        val presenter = UserChatsPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mUser)
        presenter.getUserChats()

        spy(presenter).onUserChatsEmpty()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).onUserChatsEmpty()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testNoInternetConnection() {
        val presenter = UserChatsPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mUser)
        presenter.getUserChats()

        spy(presenter).onNoInternetConnection()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).noInternetConnection()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testServiceNotAvailable() {
        val presenter = UserChatsPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mUser)
        presenter.getUserChats()

        spy(presenter).onServiceNotAvailable()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).serviceNotAvailable()
        verifyNoMoreInteractions(mCallback)
    }
}
