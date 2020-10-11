package com.robert.android.lostpets.presentation.ui.activities

import android.os.Bundle
import android.util.Patterns
import com.robert.android.lostpets.R
import com.robert.android.lostpets.domain.executor.impl.ThreadExecutor
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.network.ServiceGenerator
import com.robert.android.lostpets.network.service.UserService
import com.robert.android.lostpets.presentation.presenters.SignUpPresenter
import com.robert.android.lostpets.presentation.presenters.impl.SignUpPresenterImpl
import com.robert.android.lostpets.presentation.ui.activities.base.AbstractActivity
import com.robert.android.lostpets.presentation.ui.utils.KeyboardUtil
import com.robert.android.lostpets.presentation.ui.utils.SnackbarUtil
import com.robert.android.lostpets.threading.MainThreadImpl
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.progress_bar.*
import org.jetbrains.anko.startActivity

/**
 * Activity que extiende la clase AbstractActivity e implementa la interfaz del callback del
 * presenter SignUpPresenter. Es el controlador que se encarga de manejar la vista correspondiente
 * al registro (signup) de los usuarios en el sistema.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.ui.activities.base.AbstractActivity
 * @see com.robert.android.lostpets.presentation.presenters.SignUpPresenter.View
 */
class SignUpActivity : AbstractActivity(), SignUpPresenter.View {

    private lateinit var mSignUpPresenter: SignUpPresenter
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        init()
    }

    override fun onResume() {
        super.onResume()
        mSignUpPresenter.resume()
    }

    override fun onPause() {
        super.onPause()
        mSignUpPresenter.pause()
    }

    override fun onStop() {
        super.onStop()
        mSignUpPresenter.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mSignUpPresenter.destroy()
    }

    override fun onCheckUniqueEmail(isUnique: Boolean) {
        if (isUnique)
            mSignUpPresenter.signUp(mUser)
        else
            emailTextInputLayout.error = getString(R.string.msg_email_not_unique)
    }

    override fun onSignUp(user: User) {
        startActivity<LogInActivity>(
                LogInActivity.SIGNED_UP_USER to LogInActivity.SIGNED_UP_USER)
        finish()
    }

    override fun noInternetConnection() {
        SnackbarUtil.makeShort(activitySignupLayout, R.string.msg_no_internet_connection)
    }

    override fun serviceNotAvailable() {
        SnackbarUtil.makeLong(activitySignupLayout, R.string.msg_service_not_avabile)
    }

    private fun init() {
        toolbar.setTitle(R.string.signup_activity_toolbar_title)
        setSupportActionBar(toolbar)

        setProgressBarLayout(progressBarConstraintLayout)
        addViewsToHide(logoImageView, emailTextInputLayout, phoneTextInputLayout,
                firstNameTextInputLayout, lastNameTextInputLayout, passwordTextInputLayout,
                repeatedPasswordTextInputLayout, loginButton, signupButton)

        mSignUpPresenter = SignUpPresenterImpl(ThreadExecutor.instance, MainThreadImpl.instance,
                this, getContext(), ServiceGenerator.createService(getContext(),
                UserService::class.java))

        signupButton.setOnClickListener { onClickSignUp() }
        loginButton.setOnClickListener { onClickLogIn() }
    }

    private fun onClickSignUp() {
        KeyboardUtil.hideKeyboard(getContext(), activitySignupLayout)

        val email = emailEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val repeatedPassword = repeatedPasswordEditText.text.toString().trim()

        val validEmail = validateEmail(email)
        val validPhone = validatePhone(phone)
        val validFirstName = validateFirstName(firstName)
        val validLastName = validateLastName(lastName)
        val validPassword = validatePassword(password)
        val validRepeatedPassword = validateRepeatedPassword(password, repeatedPassword)

        if (validEmail && validPhone && validFirstName &&
                validLastName && validPassword && validRepeatedPassword) {
            mSignUpPresenter.checkUniqueEmail(email)
            mUser = User(email, password, phone, firstName, lastName)
        }
    }

    private fun onClickLogIn() {
        startActivity<LogInActivity>()
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

    private fun validatePhone(phone: String): Boolean {
        return when {
            phone.isEmpty() -> {
                phoneTextInputLayout.error = getString(R.string.msg_phone_required)
                false
            }
            !Patterns.PHONE.matcher(phone).matches() -> {
                phoneTextInputLayout.error = getString(R.string.msg_phone_invalid)
                false
            }
            else -> {
                phoneTextInputLayout.error = null
                true
            }
        }
    }

    private fun validateFirstName(firstName: String): Boolean {
        return when {
            firstName.isEmpty() -> {
                firstNameTextInputLayout.error = getString(R.string.msg_first_name_required)
                false
            }
            else -> {
                firstNameTextInputLayout.error = null
                true
            }
        }
    }

    private fun validateLastName(lastName: String): Boolean {
        return when {
            lastName.isEmpty() -> {
                lastNameTextInputLayout.error = getString(R.string.msg_last_name_required)
                false
            }
            else -> {
                lastNameTextInputLayout.error = null
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

    private fun validateRepeatedPassword(password: String, repeatedPassword: String): Boolean {
        return when {
            repeatedPassword.isEmpty() -> {
                repeatedPasswordTextInputLayout.error =
                        getString(R.string.msg_repeated_password_required)
                false
            }
            password != repeatedPassword -> {
                repeatedPasswordTextInputLayout.error =
                        getString(R.string.msg_repeated_password_invalid)
                false
            }
            else -> {
                repeatedPasswordTextInputLayout.error = null
                true
            }
        }
    }
}
