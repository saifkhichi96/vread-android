package sfllhkhan95.connect.core.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import sfllhkhan95.connect.R
import sfllhkhan95.connect.auth.ui.activity.SignInActivity
import sfllhkhan95.connect.auth.util.SessionManager
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : ConnectActivity() {

    @Inject
    lateinit var auth: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        findViewById<View>(R.id.logo).animate()
            .alphaBy(1.0f)
            .setDuration(1500L)
            .withEndAction {
                if (auth.currentUser != null) {
                    startApp()
                } else {
                    findViewById<View>(R.id.logo).animate()
                        .translationYBy(-500.0f)
                        .setDuration(1000L)
                        .setStartDelay(500L)
                        .withEndAction {
                            startApp()
                        }
                        .start()
                }
            }
            .start()
    }

    private fun startApp() {
        startActivity(Intent(this, SignInActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

}