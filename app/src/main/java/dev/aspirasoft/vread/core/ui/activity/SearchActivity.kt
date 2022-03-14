package dev.aspirasoft.vread.core.ui.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.auth.data.repo.UserRepository
import dev.aspirasoft.vread.auth.model.User
import dev.aspirasoft.vread.auth.ui.view.UserView
import dev.aspirasoft.vread.core.ui.adapter.ListAdapter
import dev.aspirasoft.vread.core.util.OnItemClickListener
import dev.aspirasoft.vread.profile.ui.activity.ProfileActivity
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SearchActivity : SecureActivity() {

    @Inject
    lateinit var repo: UserRepository

    private var adapter: ListAdapter<User>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        adapter = ListAdapter(this, UserView::class.java)
        adapter?.itemClickListener = object : OnItemClickListener<User> {
            override fun onItemClick(item: User) {
                val i = Intent(applicationContext, ProfileActivity::class.java)
                i.putExtra("currentUser", item)
                startActivity(i)
            }
        }

        searchResults.adapter = adapter
        adapter?.notifyDataSetChanged()

        // Set up search
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                val query = try {
                    text!!.trim { it <= ' ' }
                        .replace("\n", "")
                        .replace("\t", "")
                        .replace("[ ]+".toRegex(), " ")
                } catch (ex: Exception) {
                    ""
                }

                if (query.isNotBlank()) {
                    search(query)
                    return true
                }

                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {
                if (text != null && text.isBlank()) {
                    adapter?.clear()
                    return true
                }

                return false
            }
        })
        searchView.setSearchableInfo(
            (getSystemService(Context.SEARCH_SERVICE) as SearchManager).getSearchableInfo(
                componentName
            )
        )

        // Verify the action and get the query
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                searchView.setQuery(query, true)
            }
        }
    }

    private fun search(query: String) {
        if (query.isNotBlank()) {
            searchView.isEnabled = true
            adapter?.clear()

            FirebaseDatabase.getInstance()
                .getReference("users/")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.getValue(object :
                            GenericTypeIndicator<HashMap<String, HashMap<String, Any?>>>() {})?.values?.let {
                            for (m in it.iterator()) {
                                val uid = (m["meta"] as HashMap<*, *>?)?.get("uid")?.toString()
                                val email = (m["meta"] as HashMap<*, *>?)?.get("email")?.toString()
                                val firstName = (m["meta"] as HashMap<*, *>?)?.get("firstName")?.toString()
                                val username = (m["meta"] as HashMap<*, *>?)?.get("username")?.toString()

                                if (uid != null
                                    && ((email != null && email.contains(query, true))
                                            || (firstName != null && firstName.contains(query, true))
                                            || (username != null && username.contains(query, true)))
                                ) {
                                    lifecycleScope.launch {
                                        try {
                                            val follower = repo.getById(uid).getOrThrow()
                                            searchView.isEnabled = false
                                            adapter?.add(follower)
                                        } catch (ex: Exception) {
                                            searchView.isEnabled = false
                                            makeToast(ex.message ?: ex::class.java.simpleName)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        searchView.isEnabled = false
                        makeToast(error.message)
                    }
                })
        }
    }

}