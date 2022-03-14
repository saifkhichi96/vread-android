package dev.aspirasoft.vread.auth.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.android.material.animation.AnimationUtils
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.AndroidEntryPoint
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.auth.model.Credential
import dev.aspirasoft.vread.auth.util.SessionManager
import dev.aspirasoft.vread.core.ui.activity.ConnectActivity
import dev.aspirasoft.vread.core.ui.activity.HomeActivity
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignInActivity : ConnectActivity(), View.OnClickListener {

    @Inject
    lateinit var auth: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        signInButton.setOnClickListener(this)
        signUpButton.setOnClickListener(this)
        forgotPasswordButton.setOnClickListener(this)

        if (auth.currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            signInButton.setOnClickListener(this)
            signUpButton.setOnClickListener(this)
            forgotPasswordButton.setOnClickListener(this)

            // Fade in sign in fields
            credentials.visibility = View.VISIBLE
            credentials
                .animate()
                .alphaBy(1.0f)
                .setDuration(2000L)
                .setInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR)
                .start()
        }

        email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                validateEmail(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // no-op
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // no-op
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
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
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
            passwordWrapper.error = "This field is required."
        }
    }

    private fun signIn() {
        val email = email.text.toString().trim { it <= ' ' }
        validateEmail(email)

        val password = passwordField.text.toString().trim { it <= ' ' }
        validatePassword(password)

        if (!passwordWrapper.isErrorEnabled && !emailWrapper.isErrorEnabled) {
            signInButton.isEnabled = false

            lifecycleScope.launch {
                try {
                    auth.start(Credential(email, password))
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    finish()
                } catch (ex: Exception) {
                    signInButton.isEnabled = true
                    makeSnackbar(
                        credentials, when (ex.javaClass.simpleName) {
                            FirebaseAuthInvalidCredentialsException::class.java.simpleName,
                            FirebaseAuthInvalidUserException::class.java.simpleName,
                            -> getString(R.string.error_wrong_credentials)
                            FirebaseNetworkException::class.java.simpleName -> getString(R.string.error_no_internet)
                            else -> ex.message ?: ex.javaClass.simpleName
                        }
                    )
                }
            }
        }
    }

    override fun onClick(view: View) {
        if (!signInButton.isEnabled) return
        when (view.id) {
            R.id.signInButton -> signIn()
            R.id.signUpButton -> startActivity(Intent(this, SignUpActivity::class.java))
            R.id.forgotPasswordButton -> startActivity(Intent(this, ResetPasswordActivity::class.java))
        }
    }

    override fun onBackPressed() {
        if (!signInButton.isEnabled) return
        super.onBackPressed()
    }

}