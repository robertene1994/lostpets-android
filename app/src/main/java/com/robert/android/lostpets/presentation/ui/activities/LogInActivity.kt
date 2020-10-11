package com.robert.android.lostpets.presentation.ui.activities

import android.os.Bundle
import android.util.Patterns
import com.robert.android.lostpets.R
import com.robert.android.lostpets.domain.executor.impl.ThreadExecutor
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.network.ServiceGenerator
import com.robert.android.lostpets.network.service.UserService
import com.robert.android.lostpets.presentation.presenters.LogInPresenter
import com.robert.android.lostpets.presentation.presenters.impl.LogInPresenterImpl
import com.robert.android.lostpets.presentation.ui.activities.base.AbstractActivity
import com.robert.android.lostpets.presentation.ui.utils.KeyboardUtil
import com.robert.android.lostpets.presentation.ui.utils.SnackbarUtil
import com.robert.android.lostpets.storage.SessionRepositoryImpl
import com.robert.android.lostpets.threading.MainThreadImpl
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.progress_bar.*
import org.jetbrains.anko.startActivity

/**
 * Activity que extiende la clase AbstractActivity e implementa la interfaz del callback del
 * presenter LogInPresenter. Es el controlador que se encarga de manejar la vista correspondiente
 * al acceso (login) de los usuarios en el sistema.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.ui.activities.base.AbstractActivity
 * @see com.robert.android.lostpets.presentation.presenters.LogInPresenter.View
 */
class LogInActivity : AbstractActivity(), LogInPresenter.View {

    companion object {
        const val SIGNED_UP_USER = "LogInActivity::SignedUpUser"
    }

    private lateinit var mLogInPresenter: LogInPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loadExtras()
        init()
    }

    override fun onResume() {
        super.onResume()
        mLogInPresenter.resume()
    }

    override fun onPause() {
        super.onPause()
        mLogInPresenter.pause()
    }

    override fun onStop() {
        super.onStop()
        mLogInPresenter.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mLogInPresenter.destroy()
    }

    override fun onFailureLogIn() {
        SnackbarUtil.makeShort(activityLoginLayout, R.string.msg_invalid_email_or_password)
    }

    override fun onRoleNotAllowedLogIn() {
        SnackbarUtil.makeShort(activityLoginLayout, R.string.msg_only_users_allowed)
    }

    override fun onSuccessLogIn(user: User) {
        startActivity<MainActivity>()
        finish()
    }

    override fun onUserLoggedIn() {
        startActivity<MainActivity>()
        finish()
    }

    override fun onUserSessionExpired() {
        SnackbarUtil.makeShort(activityLoginLayout, R.string.msg_expired_session)
    }

    override fun onUserDisabled() {
        SnackbarUtil.makeShort(activityLoginLayout, R.string.msg_user_disabled)
    }

    override fun noInternetConnection() {
        SnackbarUtil.makeShort(activityLoginLayout, R.string.msg_no_internet_connection)
    }

    override fun serviceNotAvailable() {
        SnackbarUtil.makeLong(activityLoginLayout, R.string.msg_service_not_avabile)
    }

    private fun loadExtras() {
        with(intent) {
            if (getStringExtra(SIGNED_UP_USER) != null)
                SnackbarUtil.makeLong(activityLoginLayout, R.string.msg_signed_up_user)
        }
    }

    private fun init() {
        toolbar.setTitle(R.string.login_activity_toolbar_title)
        setSupportActionBar(toolbar)

        setProgressBarLayout(progressBarConstraintLayout)
        addViewsToHide(logoImageView, emailTextInputLayout, passwordTextInputLayout,
                loginButton, signupButton)

        mLogInPresenter = LogInPresenterImpl(ThreadExecutor.instance, MainThreadImpl.instance,
                this, getContext(), ServiceGenerator.createService(getContext(),
                UserService::class.java), SessionRepositoryImpl(getContext()))

        mLogInPresenter.checkUserSession()

        loginButton.setOnClickListener { onClickLogIn() }
        signupButton.setOnClickListener { onClickSignUp() }
    }

    private fun onClickLogIn() {
        KeyboardUtil.hideKeyboard(getContext(), activityLoginLayout)

        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        val validEmail = validateEmail(email)
        val validPassword = validatePassword(password)

        if (validEmail && validPassword)
            mLogInPresenter.logIn(email, password)
    }

    private fun onClickSignUp() {
        startActivity<SignUpActivity>()
        finish()
    }

    private fun validateEmail(email: String): Boolean {
        return when {
            email.isEmpty() -> {
                emailTextInputLayout.error = getString(R.string.msg_email_required)
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailTextInputLayout.error = getString(R.string.msg_email_invalid)
                false
            }
            else -> {
                emailTextInputLayout.error = null
                true
            }
        }
    }

    private fun validatePassword(password: String): Boolean {
        return when {
            password.isEmpty() -> {
                passwordTextInputLayout.error = getString(R.string.msg_password_required)
                false
            }
            else -> {
                passwordTextInputLayout.error = null
                true
            }
        }
    }
}
