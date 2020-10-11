package com.robert.android.lostpets.presentation.presenters.impl

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import com.robert.android.lostpets.R
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.GetLanguageInteractor
import com.robert.android.lostpets.domain.interactors.SaveSettingsInteractor
import com.robert.android.lostpets.domain.interactors.impl.GetLanguageInteractorImpl
import com.robert.android.lostpets.domain.interactors.impl.SaveLanguageInteractorImpl
import com.robert.android.lostpets.domain.repository.SettingsRepository
import com.robert.android.lostpets.presentation.presenters.SettingsPresenter
import com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter
import com.robert.android.lostpets.utils.ContextWrapper
import java.util.Locale

/**
 * Clase que extiende la clase AbstractPresenter e implementa la interfaz SettingsPresenter.
 * Implementa las interfaces de los callbacks de los interactors GetLanguageInteractor y
 * SaveSettingsInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter
 * @see com.robert.android.lostpets.presentation.presenters.SettingsPresenter
 * @see com.robert.android.lostpets.domain.interactors.GetLanguageInteractor.Callback
 * @see com.robert.android.lostpets.domain.interactors.SaveSettingsInteractor.Callback
 */
class SettingsPresenterImpl(
    executor: Executor,
    mainThread: MainThread,
    view: SettingsPresenter.View,
    context: Context,
    settingsRepository: SettingsRepository
) :
    AbstractPresenter(executor, mainThread), SettingsPresenter, GetLanguageInteractor.Callback,
        SaveSettingsInteractor.Callback {

    private var mView: SettingsPresenter.View? = view
    private var mContext: Context? = context
    private var mSettingsRepository: SettingsRepository = settingsRepository

    private lateinit var languages: ArrayAdapter<CharSequence>
    private lateinit var locales: List<String>

    override fun resume() {
        // no aplicable
    }

    override fun pause() {
        // no aplicable
    }

    override fun stop() {
        mView = null
        mContext = null
    }

    override fun destroy() {
        mView = null
        mContext = null
    }

    override fun onSavedSettings() {
        // no aplicable
    }

    override fun onLanguageRetrieved(language: String) {
        mView!!.onLanguageRetrieved(language)
        mView!!.hideProgress()
    }

    override fun getLanguagesAdapter(locale: String): SpinnerAdapter {
        mContext = ContextWrapper.wrap(mContext!!, Locale(locale))
        loadLanguagesAndLocales()
        return languages
    }

    override fun getIndexByLocale(locale: String): Int {
        return locales.indexOf(locale)
    }

    override fun getLanguage() {
        mView!!.showProgress()
        GetLanguageInteractorImpl(mExecutor, mMainThread,
                this, mSettingsRepository).execute()
    }

    override fun saveLanguageByIndex(language: String, index: Int): Boolean {
        if (locales[index] != language) {
            val locale = locales[index]
            saveLanguage(locale)
            return true
        }
        return false
    }

    private fun loadLanguagesAndLocales() {
        locales = mContext!!.resources.getStringArray(R.array.pref_language_values).toMutableList()
        languages = ArrayAdapter(mContext!!, R.layout.language_item, mContext!!.resources
                .getStringArray(R.array.pref_language_titles))
        languages.setDropDownViewResource(R.layout.language_dropdown_item)
    }

    private fun saveLanguage(language: String) {
        SaveLanguageInteractorImpl(mExecutor, mMainThread,
                this, mSettingsRepository, language).execute()
    }
}
