package sfllhkhan95.connect.settings.ui.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Switch
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_delete_account.*
import kotlinx.coroutines.launch
import sfllhkhan95.connect.R
import sfllhkhan95.connect.auth.ui.activity.UpdatePasswordActivity
import sfllhkhan95.connect.auth.util.AccountManager
import sfllhkhan95.connect.core.ui.activity.TitledActivity
import sfllhkhan95.connect.settings.data.AppearancePreferences
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : TitledActivity(), View.OnClickListener {

    @Inject
    lateinit var appearance: AppearancePreferences

    @Inject
    lateinit var repo: AccountManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setTitle("Settings")

        // SignUpActivity click handlers for settings buttons
        findViewById<View>(R.id.signout).setOnClickListener(this)
        findViewById<View>(R.id.action_change_password).setOnClickListener(this)
        findViewById<View>(R.id.action_delete_account).setOnClickListener(this)
        findViewById<View>(R.id.action_get_help).setOnClickListener(this)
        findViewById<View>(R.id.defaultTheme).setOnClickListener(this)
        findViewById<View>(R.id.ravenclawTheme).setOnClickListener(this)
        findViewById<View>(R.id.gryffindorTheme).setOnClickListener(this)
        findViewById<View>(R.id.slytherinTheme).setOnClickListener(this)
        findViewById<View>(R.id.hufflepuffTheme).setOnClickListener(this)
        findViewById<View>(R.id.appVersion).setOnClickListener(this)
        findViewById<Switch>(R.id.nightMode).isChecked = appearance.uiMode == "Night"
        findViewById<Switch>(R.id.nightMode).setOnCheckedChangeListener { _, isChecked -> setDarkModeEnabled(isChecked) }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.action_change_password -> startActivity(Intent(this, UpdatePasswordActivity::class.java))
            R.id.action_delete_account -> object : Dialog(this) {
                override fun onCreate(savedInstanceState: Bundle) {
                    super.onCreate(savedInstanceState)
                    setContentView(R.layout.dialog_delete_account)
                    findViewById<View>(R.id.cancel_button).setOnClickListener { dismiss() }
                    findViewById<View>(R.id.submit_button).setOnClickListener {
                        val password = passwordField.text.toString().trim { it <= ' ' }
                        if (password.isNotEmpty()) {
                            passwordWrapper.isErrorEnabled = false
                            findViewById<View>(R.id.main_content).visibility = View.GONE
                            findViewById<View>(R.id.progress_bar).visibility = View.VISIBLE
                            setCancelable(false)
                            lifecycleScope.launch {
                                try {
                                    repo.deleteUserAccount(signedInUser.email, password)
                                    findViewById<View>(R.id.progress_bar).visibility = View.GONE
                                    findViewById<View>(R.id.confirmation).visibility = View.VISIBLE
                                    Handler().postDelayed({
                                        dismiss()
                                        auth.finish()
                                        finish()
                                    }, 1000L)
                                } catch (ex: Exception) {
                                    findViewById<View>(R.id.main_content).visibility = View.VISIBLE
                                    findViewById<View>(R.id.progress_bar).visibility = View.GONE
                                    setCancelable(true)
                                    passwordWrapper.isErrorEnabled = true
                                    passwordWrapper.error = ex.message
                                }
                            }
                        } else {
                            passwordWrapper.isErrorEnabled = true
                            passwordWrapper.error = "Password is required."
                        }
                    }
                }

                override fun setCancelable(flag: Boolean) {
                    super.setCancelable(flag)
                    findViewById<View>(R.id.cancel_button).isEnabled = false
                }
            }.show()
            R.id.action_get_help -> startActivity(Intent(this, HelpActivity::class.java))
            R.id.defaultTheme -> changeTheme(AppearancePreferences.THEME_DEFAULT)
            R.id.slytherinTheme -> changeTheme(AppearancePreferences.THEME_SALAZAR)
            R.id.gryffindorTheme -> changeTheme(AppearancePreferences.THEME_GODRIC)
            R.id.hufflepuffTheme -> changeTheme(AppearancePreferences.THEME_HELGA)
            R.id.ravenclawTheme -> changeTheme(AppearancePreferences.THEME_ROWENA)
            R.id.signout -> {
                auth.finish()
                finish()
            }
        }
    }

    private fun setDarkModeEnabled(enabled: Boolean) {
        appearance.uiMode = if (enabled) AppearancePreferences.MODE_DARK else AppearancePreferences.MODE_LIGHT
        recreate()
    }

    private fun changeTheme(theme: String?) {
        appearance.theme = theme!!
        recreate()
    }

    override fun onBackPressed() {
        super.onSupportNavigateUp()
    }
}