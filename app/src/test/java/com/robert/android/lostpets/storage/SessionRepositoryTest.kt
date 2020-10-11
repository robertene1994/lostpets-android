package com.robert.android.lostpets.storage

import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.Role
import com.robert.android.lostpets.domain.model.types.UserStatus
import com.robert.android.lostpets.domain.repository.SessionRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Clase test para la clase SessionRepositoryImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.storage.SessionRepositoryImpl
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class SessionRepositoryTest {

    private lateinit var mSessionRepository: SessionRepository
    private lateinit var mUser: User
    private lateinit var mToken: String

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mSessionRepository = SessionRepositoryImpl(RuntimeEnvironment.application)
        mUser = User(99, "username@email.com",
                null, Role.USER, UserStatus.ENABLED,
                "669910272", "LastName", "UserName")
        mToken = "Bearer token"
    }

    @Test
    @Throws(Exception::class)
    fun testGetUser() {
        assertNull(mSessionRepository.getUser())
        mSessionRepository.saveUser(mUser)
        assertEquals(mUser, mSessionRepository.getUser())
    }

    @Test
    @Throws(Exception::class)
    fun testSaveUser() {
        mSessionRepository.deleteUser()
        assertNull(mSessionRepository.getUser())
        mSessionRepository.saveUser(mUser)
        assertEquals(mUser, mSessionRepository.getUser())
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteUser() {
        mSessionRepository.deleteUser()
        assertNull(mSessionRepository.getUser())
        mSessionRepository.saveUser(mUser)
        assertEquals(mUser, mSessionRepository.getUser())
        mSessionRepository.deleteUser()
        assertNull(mSessionRepository.getUser())
    }

    @Test
    @Throws(Exception::class)
    fun testGetToken() {
        assertNull(mSessionRepository.getToken())
        mSessionRepository.saveToken(mToken)
        assertEquals(mToken, mSessionRepository.getToken())
    }

    @Test
    @Throws(Exception::class)
    fun testSaveToken() {
        mSessionRepository.deleteToken()
        assertNull(mSessionRepository.getToken())
        mSessionRepository.saveToken(mToken)
        assertEquals(mToken, mSessionRepository.getToken())
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteUToken() {
        mSessionRepository.deleteToken()
        assertNull(mSessionRepository.getToken())
        mSessionRepository.saveToken(mToken)
        assertEquals(mToken, mSessionRepository.getToken())
        mSessionRepository.deleteToken()
        assertNull(mSessionRepository.getToken())
    }
}
