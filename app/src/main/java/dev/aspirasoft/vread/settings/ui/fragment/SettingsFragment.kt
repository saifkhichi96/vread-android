package dev.aspirasoft.vread.settings.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.auth.data.repo.UserRepository
import dev.aspirasoft.vread.auth.model.User
import dev.aspirasoft.vread.auth.ui.activity.UpdatePasswordActivity
import dev.aspirasoft.vread.auth.util.AccountManager
import dev.aspirasoft.vread.auth.util.SessionManager
import dev.aspirasoft.vread.databinding.FragmentSettingsBinding
import dev.aspirasoft.vread.profile.ui.activity.ProfileActivity
import dev.aspirasoft.vread.settings.data.AppearancePreferences
import dev.aspirasoft.vread.settings.ui.activity.HelpActivity
import kotlinx.android.synthetic.main.dialog_delete_account.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSettingsBinding

    @Inject
    lateinit var appearance: AppearancePreferences

    @Inject
    lateinit var repo: AccountManager

    @Inject
    lateinit var auth: SessionManager

    @Inject
    lateinit var users: UserRepository

    private lateinit var signedInUser: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            val uid = arguments?.getString("user_id") ?: ""
            signedInUser = users.getById(uid).getOrNull() ?: User()
            updateUI()
        }
        return binding.root
    }

    private fun updateUI() {
        binding.avatar.showUser(signedInUser.uid)
        binding.name.text = "${signedInUser.firstName} ${signedInUser.lastName}"
        binding.username.text = signedInUser.username
        binding.profileCard.setOnClickListener {
            val i = Intent(context, ProfileActivity::class.java)
            i.putExtra("currentUser", signedInUser)
            startActivity(i)
        }

        binding.signout.setOnClickListener(this)
        binding.actionChangePassword.setOnClickListener(this)
        binding.actionDeleteAccount.setOnClickListener(this)
        binding.actionGetHelp.setOnClickListener(this)
        binding.defaultTheme.setOnClickListener(this)
        binding.ravenclawTheme.setOnClickListener(this)
        binding.gryffindorTheme.setOnClickListener(this)
        binding.slytherinTheme.setOnClickListener(this)
        binding.hufflepuffTheme.setOnClickListener(this)
        binding.appVersion.setOnClickListener(this)
        binding.nightMode.isChecked = appearance.uiMode == "Night"
        binding.nightMode.setOnCheckedChangeListener { _, isChecked -> setDarkModeEnabled(isChecked) }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.action_change_password -> startActivity(Intent(requireContext(), UpdatePasswordActivity::class.java))
            R.id.action_delete_account -> object : Dialog(requireContext()) {
                override fun onCreate(savedInstanceState: Bundle?) {
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
                                        requireActivity().finish()
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
            R.id.action_get_help -> startActivity(Intent(requireContext(), HelpActivity::class.java))
            R.id.defaultTheme -> changeTheme(AppearancePreferences.THEME_DEFAULT)
            R.id.slytherinTheme -> changeTheme(AppearancePreferences.THEME_SALAZAR)
            R.id.gryffindorTheme -> changeTheme(AppearancePreferences.THEME_GODRIC)
            R.id.hufflepuffTheme -> changeTheme(AppearancePreferences.THEME_HELGA)
            R.id.ravenclawTheme -> changeTheme(AppearancePreferences.THEME_ROWENA)
            R.id.signout -> {
                auth.finish()
                requireActivity().finish()
            }
        }
    }

    private fun setDarkModeEnabled(enabled: Boolean) {
        appearance.uiMode = if (enabled) AppearancePreferences.MODE_DARK else AppearancePreferences.MODE_LIGHT
        requireActivity().recreate()
    }

    private fun changeTheme(theme: String?) {
        appearance.theme = theme!!
        requireActivity().recreate()
    }

    companion object {
        fun newInstance(uid: String): SettingsFragment {
            val fragment = SettingsFragment()
            val args = Bundle()
            args.putString("user_id", uid)
            fragment.arguments = args
            return fragment
        }
    }

}