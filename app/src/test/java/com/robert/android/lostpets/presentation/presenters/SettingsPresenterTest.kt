package com.robert.android.lostpets.presentation.presenters

import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.repository.SettingsRepository
import com.robert.android.lostpets.presentation.presenters.impl.SettingsPresenterImpl
import com.robert.android.lostpets.utilTest.threading.TestMainThread
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Clase test para la clase SettingsPresenterImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.impl.SettingsPresenterImpl
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class SettingsPresenterTest {

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: SettingsPresenter.View
    @Mock
    private lateinit var mSettingsRepository: SettingsRepository

    private lateinit var mMainThread: MainThread

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mMainThread = TestMainThread()
    }

    @Test
    @Throws(Exception::class)
    fun testGetLanguage() {
        val presenter = SettingsPresenterImpl(mExecutor, mMainThread, mCallback,
                RuntimeEnvironment.application, mSettingsRepository)
        presenter.getLanguage()

        spy(presenter).pause()
        spy(presenter).resume()
        spy(presenter).onLanguageRetrieved("es")
        spy(presenter).onSavedSettings()
        spy(presenter).stop()
        spy(presenter).destroy()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).onLanguageRetrieved("es")
        verifyNoMoreInteractions(mSettingsRepository, mCallback)
    }
}
