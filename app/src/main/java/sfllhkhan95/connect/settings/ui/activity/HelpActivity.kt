package sfllhkhan95.connect.settings.ui.activity

import android.os.Bundle
import android.webkit.WebView
import sfllhkhan95.connect.R
import sfllhkhan95.connect.core.ui.activity.TitledActivity

class HelpActivity : TitledActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        setTitles("Help Center", "Contact Us")
        val webView = findViewById<WebView>(R.id.webView)
        webView.loadUrl("file:///android_asset/team.html")
    }
}