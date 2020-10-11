package com.robert.android.lostpets.storage

import com.robert.android.lostpets.domain.repository.SettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Clase test para la clase SettingsRepositoryImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.storage.SettingsRepositoryImpl
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class SettingsRepositoryTest {

    private lateinit var mSettingsRepository: SettingsRepository
    private lateinit var mLanguage: String

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mSettingsRepository = SettingsRepositoryImpl(RuntimeEnvironment.application)
        mLanguage = "es"
    }

    @Test
    @Throws(Exception::class)
    fun testGetLanguage() {
        assertNull(mSettingsRepository.getLanguage())
        mSettingsRepository.saveLanguage(mLanguage)
        assertEquals(mLanguage, mSettingsRepository.getLanguage())
    }

    @Test
    @Throws(Exception::class)
    fun testSaveLanguage() {
        mLanguage = "en"
        assertNull(mSettingsRepository.getLanguage())
        mSettingsRepository.saveLanguage(mLanguage)
        assertEquals(mLanguage, mSettingsRepository.getLanguage())
    }
}
