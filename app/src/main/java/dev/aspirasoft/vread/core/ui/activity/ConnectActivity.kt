package dev.aspirasoft.vread.core.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.aspirasoft.vread.R

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 2019-05-12 11:15
 */
@AndroidEntryPoint
abstract class ConnectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Set app-wide theme settings here
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)

        // Disable screenshots
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    protected fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    protected fun makeSnackbar(view: View, message: String) {
        val s = Snackbar.make(view, message, Snackbar.LENGTH_LONG)

        // Set theme colors
        val a = obtainStyledAttributes(TypedValue().data, intArrayOf(R.attr.colorPrimaryDark))
        s.view.setBackgroundColor(a.getColor(0, 0))
        s.view.findViewById<TextView>(R.id.snackbar_text).setTextColor(Color.WHITE)
        a.recycle()

        // Display message
        s.show()
    }

}