package dev.aspirasoft.vread.settings.ui.activity

import android.os.Bundle
import android.webkit.WebView
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.core.ui.activity.TitledActivity

class HelpActivity : TitledActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        setTitles("Help Center", "Contact Us")
        val webView = findViewById<WebView>(R.id.webView)
        webView.loadUrl("file:///android_asset/team.html")
    }
}