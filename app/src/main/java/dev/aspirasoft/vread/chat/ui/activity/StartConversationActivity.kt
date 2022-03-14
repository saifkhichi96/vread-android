package dev.aspirasoft.vread.chat.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.auth.model.User
import dev.aspirasoft.vread.auth.ui.view.UserView
import dev.aspirasoft.vread.chat.data.repo.ChatRepository
import dev.aspirasoft.vread.core.ui.activity.TitledActivity
import dev.aspirasoft.vread.core.ui.adapter.ListAdapter
import dev.aspirasoft.vread.core.util.OnItemClickListener
import dev.aspirasoft.vread.profile.data.source.ProfileDataSource
import kotlinx.android.synthetic.main.activity_conversation_start.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StartConversationActivity : TitledActivity() {

    private lateinit var adapter: ListAdapter<User>

    @Inject
    lateinit var repo: ChatRepository

    @Inject
    lateinit var profileRepo: ProfileDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation_start)
        setTitle("Select Friend")

        adapter = ListAdapter(this, UserView::class.java)
        adapter.itemClickListener = object : OnItemClickListener<User> {
            override fun onItemClick(item: User) {
                lifecycleScope.launch {
                    val conversation = repo.listAll(signedInUser.uid)
                        .firstOrNull {
                            it.name == null && it.participants.size == 2            // is not a group chat
                                    && it.participants.contains(signedInUser.uid)   // and contains current user
                                    && it.participants.contains(item.uid)           // and the selected friend
                        } ?: repo.create(arrayListOf(signedInUser.uid, item.uid))

                    val intent = Intent(applicationContext, ConversationActivity::class.java)
                    intent.putExtra("conversation", conversation)
                    startActivity(intent)
                }
            }
        }

        conversations.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            try {
                profileRepo.getFriends(signedInUser.uid)
                    .forEach { adapter.add(it) }
            } catch (ex: Exception) {

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}