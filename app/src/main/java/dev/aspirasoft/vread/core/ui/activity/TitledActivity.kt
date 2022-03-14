package dev.aspirasoft.vread.core.ui.activity

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import dev.aspirasoft.vread.R
import kotlinx.android.synthetic.main.base_activity.*

abstract class TitledActivity : SecureActivity() {

    override fun setContentView(@LayoutRes layoutResID: Int) {
        setContentViewWith(layoutResID)
    }

    fun setContentViewWith(@LayoutRes layoutResID: Int?) {
        super.setContentView(R.layout.base_activity)
        if (layoutResID != null) layoutInflater.inflate(layoutResID, container as ViewGroup, true)

        // Set up action bar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        activityTitle.visibility = View.VISIBLE
        activitySubtitle.visibility = View.GONE
    }

    fun setTitle(title: String) {
        activityTitle.text = title
    }

    fun setTitles(title: String, subtitle: String) {
        setTitle(title)

        activitySubtitle.text = subtitle
        activitySubtitle.visibility = View.VISIBLE
    }

}