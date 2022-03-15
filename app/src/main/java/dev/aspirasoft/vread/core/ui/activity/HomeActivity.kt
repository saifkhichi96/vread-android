package dev.aspirasoft.vread.core.ui.activity

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.core.ui.adapter.ContentAdapter
import dev.aspirasoft.vread.databinding.ActivityHomeBinding
import dev.aspirasoft.vread.notifications.ui.activity.NotificationsActivity
import dev.aspirasoft.vread.notifications.util.TokenReceiver
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : SecureActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var searchView: SearchView

    private lateinit var mTokenReceiver: TokenReceiver

    private lateinit var contentAdapter: ContentAdapter
    private var savedContentPosition = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up action bar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        // Set up search
        searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                onSearchRequested()
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
                return false
            }
        })
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

            // Set up pages and navigation
            if (!::contentAdapter.isInitialized) createContent(uid)
            binding.profileContent.currentItem = savedContentPosition
        }

        searchView.clearFocus()

        if (::mTokenReceiver.isInitialized) mTokenReceiver.register(this)
    }

    override fun onPause() {
        if (::mTokenReceiver.isInitialized) mTokenReceiver.unregister(this)
        savedContentPosition = binding.profileContent.currentItem
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
        return when (item.itemId) {
            R.id.action_notifications -> {
                startActivity(Intent(this, NotificationsActivity::class.java))
                true
            }
            else -> false
        }
    }

    override fun onBackPressed() {
        super.onSupportNavigateUp()
    }

    private fun createContent(uid: String) {
        contentAdapter = ContentAdapter(supportFragmentManager, uid)
        binding.profileContent.adapter = contentAdapter
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    binding.profileContent.currentItem = 0
                    true
                }
                R.id.menu_reading -> {
                    binding.profileContent.currentItem = 1
                    true
                }
                R.id.menu_favorites -> {
                    binding.profileContent.currentItem = 2
                    true
                }
                R.id.menu_profile -> {
                    binding.profileContent.currentItem = 3
                    true
                }
                else -> false
            }
        }
    }

}