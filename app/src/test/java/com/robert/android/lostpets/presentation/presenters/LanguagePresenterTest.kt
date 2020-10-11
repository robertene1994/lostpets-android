package com.robert.android.lostpets.presentation.presenters

import com.robert.android.lostpets.domain.repository.SettingsRepository
import com.robert.android.lostpets.presentation.presenters.impl.LanguagePresenterImpl
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

/**
 * Clase test para la clase LanguagePresenterImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.impl.LanguagePresenterImpl
 */
@RunWith(MockitoJUnitRunner::class)
class LanguagePresenterTest {

    @Mock
    private lateinit var mSettingsRepository: SettingsRepository

    @Test
    @Throws(Exception::class)
    fun testCheckUserSession() {
        val presenter = LanguagePresenterImpl(mSettingsRepository)
        presenter.getLanguage()
        verify(mSettingsRepository).getLanguage()
    }
}
