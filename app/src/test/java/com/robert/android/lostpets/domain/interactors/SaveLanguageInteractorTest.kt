package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.impl.SaveLanguageInteractorImpl
import com.robert.android.lostpets.domain.repository.SettingsRepository
import com.robert.android.lostpets.utilTest.threading.TestMainThread
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

/**
 * Clase test para la clase SaveLanguageInteractorImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.impl.SaveLanguageInteractorImpl
 */
@RunWith(MockitoJUnitRunner::class)
class SaveLanguageInteractorTest {

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: SaveSettingsInteractor.Callback
    @Mock
    private lateinit var mSettingsRepository: SettingsRepository

    private lateinit var mMainThread: MainThread

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()
    }

    @Test
    @Throws(Exception::class)
    fun testSaveLanguage() {
        val language = "en"

        `when`(mSettingsRepository.getLanguage()).thenReturn(language)

        SaveLanguageInteractorImpl(mExecutor, mMainThread,
                mCallback, mSettingsRepository, language).run()

        verify(mSettingsRepository).saveLanguage(language)
        verify(mCallback).onSavedSettings()
        verifyNoMoreInteractions(mSettingsRepository, mCallback)
        assertEquals(language, mSettingsRepository.getLanguage())
    }
}
