package dev.aspirasoft.vread.auth.ui.activity

import android.os.Bundle
import android.view.View
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.core.ui.activity.TitledActivity
import kotlinx.android.synthetic.main.activity_password_update.*


class UpdatePasswordActivity : TitledActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_update)
        setTitle("Change Password")
    }

    fun updatePassword(view: View) {
        oldPasswordWrapper.isErrorEnabled = false
        confirmPasswordWrapper.isErrorEnabled = false

        val existingPassword = oldPassword.text.toString().trim()
        val password = passwordField.text.toString().trim()
        val confirmPassword = confirmPassword.text.toString().trim()

        if (existingPassword.isNotBlank() && password.isNotBlank()) {
            if (password != confirmPassword) {
                confirmPasswordWrapper.isErrorEnabled = true
                confirmPasswordWrapper.error = getString(R.string.error_password_mismatch)
            } else {
                view.isEnabled = false

                val credential = EmailAuthProvider.getCredential(signedInUser!!.email!!, existingPassword)
                val user = FirebaseAuth.getInstance().currentUser
                user?.reauthenticate(credential)?.addOnCompleteListener {
                    view.isEnabled = true
                    if (it.isSuccessful) {
                        user.updatePassword(password).addOnCompleteListener { task ->
                            makeSnackbar(credentials, if (task.isSuccessful) {
                                getString(R.string.status_password_updated)
                            } else {
                                if (task.exception == null) {
                                    getString(R.string.error_unknown)
                                } else {
                                    when (task.exception!!.javaClass.simpleName) {
                                        FirebaseNetworkException::class.java.simpleName -> getString(R.string.error_no_internet)
                                        else -> task.exception!!.message
                                                ?: task.exception!!.javaClass.simpleName
                                    }
                                }
                            })
                        }
                    } else if (it.exception != null) {
                        makeSnackbar(credentials, when (it.exception!!.javaClass.simpleName) {
                            FirebaseAuthInvalidCredentialsException::class.java.simpleName -> getString(R.string.error_wrong_password)
                            FirebaseNetworkException::class.java.simpleName -> getString(R.string.error_no_internet)
                            else -> it.exception!!.message ?: it.exception!!.javaClass.simpleName
                        })
                    }
                }
            }
        }
    }

}