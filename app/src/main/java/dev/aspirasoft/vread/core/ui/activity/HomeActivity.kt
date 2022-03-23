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
    }

    private fun onSearchRequested(query: String): Boolean {
        val q = query.trim { it <= ' ' }
                .replace("\n", "")
                .replace("\t", "")
                .replace("[ ]+".toRegex(), " ")

        if (q.isNotBlank()) {
            val i = Intent(this, SearchActivity::class.java)
            i.action = Intent.ACTION_SEARCH
            i.putExtra(SearchManager.QUERY, q)
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
        menuInflater.inflate(R.menu.menu_main, menu)

        // Associate searchable configuration with the SearchView
        (menu.findItem(R.id.action_search).actionView as SearchView).apply {
            this.queryHint = getString(R.string.hint_search)
            this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return onSearchRequested(query.trim())
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return if (newText.isEmpty()) onSearchRequested("") else false
                }
            })
        }
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