package sfllhkhan95.connect.core.ui.activity

import android.content.Intent
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import sfllhkhan95.connect.auth.model.User
import sfllhkhan95.connect.auth.ui.activity.SignInActivity
import sfllhkhan95.connect.auth.util.SessionManager
import javax.inject.Inject

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 2019-05-12 11:15
 */
@AndroidEntryPoint
abstract class SecureActivity : ConnectActivity() {

    @Inject
    lateinit var auth: SessionManager

    protected lateinit var signedInUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signedInUser = auth.currentUser ?: return onSignedOut()
    }

    private fun onSignedOut() {
        makeToast("You have been signed out.")
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

}