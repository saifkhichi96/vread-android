package sfllhkhan95.connect.core.ui.activity

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.launch
import sfllhkhan95.connect.R
import sfllhkhan95.connect.chat.ui.activity.StartConversationActivity
import sfllhkhan95.connect.core.ui.adapter.ContentAdapter
import sfllhkhan95.connect.notifications.util.TokenReceiver
import sfllhkhan95.connect.settings.ui.activity.SettingsActivity

@AndroidEntryPoint
class HomeActivity : SecureActivity() {

    private lateinit var mTokenReceiver: TokenReceiver

    private lateinit var contentAdapter: ContentAdapter
    private var savedContentPosition = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Set up action bar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        // Set up search
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                onSearchRequested()
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
                return false
            }
        })

        findViewById<View>(R.id.chat_button).visibility = View.VISIBLE
        chat_button.setOnClickListener {
            startActivity(Intent(this, StartConversationActivity::class.java))
        }
    }

    override fun onSearchRequested(): Boolean {
        val query = try {
            searchView.query!!.toString().trim { it <= ' ' }
                .replace("\n", "")
                .replace("\t", "")
                .replace("[ ]+".toRegex(), " ")
        } catch (ex: Exception) {
            ""
        }

        if (query.isNotBlank()) {
            searchView.setQuery("", false)
            searchView.clearFocus()

            val i = Intent(this, SearchActivity::class.java)
            i.action = Intent.ACTION_SEARCH
            i.putExtra(SearchManager.QUERY, query)
            startActivity(i)
            return true
        }

        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        contentAdapter.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        signedInUser.let { user ->
            val uid = user.uid
            if (!::mTokenReceiver.isInitialized) {
                mTokenReceiver = TokenReceiver(uid)
                lifecycleScope.launch { mTokenReceiver.requestNewToken() }
            }
            avatar.showUser(uid)

            // Set up pages and navigation
            if (!::contentAdapter.isInitialized) createContent(uid)
            profileContent.currentItem = savedContentPosition
        }

        searchView.clearFocus()

        if (::mTokenReceiver.isInitialized) mTokenReceiver.register(this)
    }

    override fun onPause() {
        if (::mTokenReceiver.isInitialized) mTokenReceiver.unregister(this)
        savedContentPosition = profileContent.currentItem
        super.onPause()
    }

    override fun onStop() {
        if (::mTokenReceiver.isInitialized) mTokenReceiver.unregister(this)
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }

        return false
    }

    private fun createContent(uid: String) {
        contentAdapter = ContentAdapter(this, uid)
        profileContent.adapter = contentAdapter
        profileContent.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> findViewById<View>(R.id.chat_button).visibility = View.VISIBLE
                    else -> findViewById<View>(R.id.chat_button).visibility = View.GONE
                }
            }
        })
        TabLayoutMediator(navigation, profileContent) { tab, position ->
            tab.text = contentAdapter.getPageTitle(position)
        }.attach()
    }

}