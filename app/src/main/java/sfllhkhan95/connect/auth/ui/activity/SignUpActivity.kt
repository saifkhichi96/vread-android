package sfllhkhan95.connect.auth.ui.activity

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.launch
import sfllhkhan95.connect.R
import sfllhkhan95.connect.auth.model.User
import sfllhkhan95.connect.auth.util.AccountManager
import sfllhkhan95.connect.auth.util.SessionManager
import sfllhkhan95.connect.core.ui.activity.ConnectActivity
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class SignUpActivity : ConnectActivity(), View.OnClickListener {

    @Inject
    lateinit var auth: SessionManager

    @Inject
    lateinit var repo: AccountManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        username.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // no-op
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // no-op
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                validateUsername(s.toString())
            }
        })
        email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateEmail(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
        passwordField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                validatePassword(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // no-op
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // no-op
            }
        })

        signUpButton.setOnClickListener(this)
        firstName.requestFocus()

        disclaimer.text = HtmlCompat.fromHtml(getString(R.string.disclaimer_signup), HtmlCompat.FROM_HTML_MODE_LEGACY)
        disclaimer.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun validateUsername(s: String) {
        val username = s.trim { it <= ' ' }
        val usernamePattern = "[a-zA-Z0-9]+"

        if (username.matches(usernamePattern.toRegex()) && s.isNotEmpty()) {
            usernameWrapper.isErrorEnabled = false
        } else {
            usernameWrapper.isErrorEnabled = true
            usernameWrapper.error = getString(R.string.error_username_format)
        }
    }

    private fun validateEmail(s: String) {
        val email = s.trim { it <= ' ' }
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        if (email.matches(emailPattern.toRegex()) && s.isNotEmpty()) {
            emailWrapper.isErrorEnabled = false
        } else {
            emailWrapper.isErrorEnabled = true
            emailWrapper.error = getString(R.string.error_email_format)
        }
    }

    private fun validatePassword(s: String) {
        val password = s.trim { it <= ' ' }
        if (password.isNotEmpty()) {
            passwordWrapper.isErrorEnabled = false
        } else {
            passwordWrapper.isErrorEnabled = true
            passwordWrapper.error = getString(R.string.error_required)
        }
    }

    private fun signUp() {
        val email = email.text.toString().trim { it <= ' ' }
        validateEmail(email)

        val password = passwordField.text.toString().trim { it <= ' ' }
        validatePassword(password)

        val username = username.text.toString().trim { it <= ' ' }
        validateUsername(username)
        if (username.isEmpty()) {
            usernameWrapper.isErrorEnabled = true
            usernameWrapper.error = getString(R.string.error_required)
        }

        val firstName = firstName.text.toString().trim { it <= ' ' }
        firstNameWrapper.isErrorEnabled = false
        if (firstName.isEmpty()) {
            firstNameWrapper.isErrorEnabled = true
            firstNameWrapper.error = getString(R.string.error_required)
        }

        val lastName = this.lastName.text.toString().trim { it <= ' ' }
        if (!emailWrapper.isErrorEnabled && !passwordWrapper.isErrorEnabled &&
            !usernameWrapper.isErrorEnabled && !firstNameWrapper.isErrorEnabled
        ) {
            signUpButton.isEnabled = false

            val user = User()
            user.email = email
            user.firstName = firstName
            user.lastName = lastName
            user.username = username

            // Set joining date to today
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            user.joinedOn = formatter.format(Date(System.currentTimeMillis()))

            // Get user country from device locale
            val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                resources.configuration.locales.get(0)
            } else {
                resources.configuration.locale
            }

            user.country = locale.displayCountry

            lifecycleScope.launch {
                try {
                    val newUser = repo.createUserAccount(user, password)
                    auth.saveSessionData(newUser)
                    finish()
                } catch (ex: Exception) {
                    signUpButton.isEnabled = true
                    when (ex) {
                        is FirebaseAuthUserCollisionException -> {
                            emailWrapper.isErrorEnabled = true
                            emailWrapper.error = getString(R.string.error_email_not_unique)
                        }
                        is IllegalStateException -> {
                            usernameWrapper.isErrorEnabled = true
                            usernameWrapper.error = getString(R.string.error_username_not_unique)
                        }
                        is FirebaseNetworkException -> makeSnackbar(credentials, getString(R.string.error_no_internet))
                        else -> makeSnackbar(credentials, ex.message ?: ex.javaClass.simpleName)
                    }
                }
            }
        }
    }

    override fun onClick(view: View) {
        if (!signUpButton.isEnabled) return
        if (view.id == R.id.signUpButton) {
            signUp()
        }
    }

    override fun onBackPressed() {
        if (!signUpButton.isEnabled) return
        super.onBackPressed()
    }

}