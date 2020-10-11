package com.robert.android.lostpets

import android.app.Application
import android.content.Context
import com.robert.android.lostpets.network.message.MessagingService
import com.robert.android.lostpets.presentation.presenters.impl.LanguagePresenterImpl
import com.robert.android.lostpets.presentation.ui.notifications.NotificationsManager
import com.robert.android.lostpets.storage.SettingsRepositoryImpl
import com.robert.android.lostpets.utils.ContextWrapper
import java.util.*

/**
 * Punto de entrada para la aplicación LostPets.
 *
 * @author Robert Ene
 */
@Suppress("unused")
class LostPetsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        wrapContextLanguage(applicationContext)
        NotificationsManager.createNotificationChannel(applicationContext)
        MessagingService.startService(applicationContext)
    }

    private fun wrapContextLanguage(context: Context) {
        val languagePresenter = LanguagePresenterImpl(SettingsRepositoryImpl(context))
        val language = languagePresenter.getLanguage()
        ContextWrapper.wrap(context, Locale(language))
    }
}
