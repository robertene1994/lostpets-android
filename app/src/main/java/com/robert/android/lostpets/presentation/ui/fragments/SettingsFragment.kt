package com.robert.android.lostpets.presentation.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.robert.android.lostpets.R
import com.robert.android.lostpets.domain.executor.impl.ThreadExecutor
import com.robert.android.lostpets.presentation.presenters.SettingsPresenter
import com.robert.android.lostpets.presentation.presenters.impl.SettingsPresenterImpl
import com.robert.android.lostpets.presentation.ui.fragments.base.AbstractFragment
import com.robert.android.lostpets.presentation.ui.utils.SnackbarUtil
import com.robert.android.lostpets.storage.SettingsRepositoryImpl
import com.robert.android.lostpets.threading.MainThreadImpl
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.progress_bar.*

/**
 * Fragment que extiende la clase AbstractFragment e implementa la interfaz del callback del
 * presenter SettingsPresenter. Es el controlador que se encarga de manejar la vista correspondiente
 * a los ajustes de la aplicación.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.ui.fragments.base.AbstractFragment
 * @see com.robert.android.lostpets.presentation.presenters.SettingsPresenter.View
 */
class SettingsFragment : AbstractFragment(), SettingsPresenter.View,
        AdapterView.OnItemSelectedListener {

    companion object {

        /**
         * Método que instancia el fragment para la vista asociada a los ajustes de la aplicación.
         *
         * @return el fragment creado e instanciado para la vista correspondiente.
         */
        fun newInstance(): Fragment {
            return SettingsFragment()
        }
    }

    private lateinit var mSettingsPresenter: SettingsPresenter
    private lateinit var locale: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onResume() {
        super.onResume()
        mSettingsPresenter.resume()
    }

    override fun onPause() {
        super.onPause()
        mSettingsPresenter.pause()
    }

    override fun onStop() {
        super.onStop()
        mSettingsPresenter.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mSettingsPresenter.destroy()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // no aplicable
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (mSettingsPresenter.saveLanguageByIndex(locale, position)) {
            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    override fun onLanguageRetrieved(language: String) {
        locale = language
        spLanguage.adapter = mSettingsPresenter.getLanguagesAdapter(locale)
        spLanguage.setSelection(mSettingsPresenter.getIndexByLocale(language))
    }

    override fun noInternetConnection() {
        SnackbarUtil.makeShort(fragmentSettingsLayout, R.string.msg_no_internet_connection)
    }

    override fun serviceNotAvailable() {
        SnackbarUtil.makeLong(fragmentSettingsLayout, R.string.msg_service_not_avabile)
    }

    private fun init() {
        (activity as AppCompatActivity).supportActionBar!!
                .setTitle(R.string.settings_fragment_toolbar_title)

        spLanguage.onItemSelectedListener = this

        setProgressBarLayout(progressBarConstraintLayout)
        addViewsToHide(languageLayout, updateAdFirstDivider)

        mSettingsPresenter = SettingsPresenterImpl(ThreadExecutor.instance,
                MainThreadImpl.instance, this, context, SettingsRepositoryImpl(context))
        mSettingsPresenter.getLanguage()
    }
}
