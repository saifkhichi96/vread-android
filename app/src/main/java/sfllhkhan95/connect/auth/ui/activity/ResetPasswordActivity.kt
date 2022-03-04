package sfllhkhan95.connect.auth.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_password_reset.*
import kotlinx.coroutines.launch
import sfllhkhan95.connect.R
import sfllhkhan95.connect.auth.util.AccountManager
import sfllhkhan95.connect.core.ui.activity.ConnectActivity
import javax.inject.Inject

@AndroidEntryPoint
class ResetPasswordActivity : ConnectActivity(), View.OnClickListener {

    @Inject
    lateinit var repo: AccountManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        sendLinkButton.setOnClickListener(this)
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

    private fun resetPassword() {
        val e = email.text.toString().trim { it <= ' ' }
        validateEmail(e)

        if (!emailWrapper.isErrorEnabled) {
            sendLinkButton.isEnabled = false
            lifecycleScope.launch {
                try {
                    repo.sendPasswordResetEmail(e)
                    sendLinkButton.isEnabled = true
                    makeSnackbar(credentials, getString(R.string.status_link_send))
                } catch (ex: Exception) {
                    makeSnackbar(
                        credentials,
                        when (ex) {
                            is FirebaseAuthInvalidUserException -> getString(R.string.error_no_user)
                            is FirebaseNetworkException -> getString(R.string.error_no_internet)
                            else -> ex.message ?: ex.javaClass.simpleName
                        }
                    )
                }
            }
        }
    }

    override fun onClick(view: View) {
        if (!sendLinkButton.isEnabled) return
        if (view.id == R.id.sendLinkButton) {
            resetPassword()
        }
    }

    override fun onBackPressed() {
        if (!sendLinkButton.isEnabled) return
        super.onBackPressed()
    }

}