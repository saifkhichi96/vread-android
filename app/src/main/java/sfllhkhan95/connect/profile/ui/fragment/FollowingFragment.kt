package sfllhkhan95.connect.profile.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_conversations.*
import kotlinx.coroutines.launch
import sfllhkhan95.connect.R
import sfllhkhan95.connect.auth.model.User
import sfllhkhan95.connect.auth.ui.view.UserView
import sfllhkhan95.connect.core.ui.adapter.ListAdapter
import sfllhkhan95.connect.core.util.OnItemClickListener
import sfllhkhan95.connect.profile.data.source.ProfileDataSource
import sfllhkhan95.connect.profile.ui.activity.ProfileActivity
import javax.inject.Inject

@AndroidEntryPoint
class FollowingFragment : Fragment() {

    @Inject
    lateinit var repo: ProfileDataSource

    private var adapter: ListAdapter<User>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_conversations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { args ->
            (args.getSerializable("user") as User?)?.let { user ->
                lifecycleScope.launch {
                    try {
                        repo.getFollowing(user.uid)
                            .forEach { following -> adapter?.add(following) }
                    } catch (ex: Exception) {
                        Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        adapter = ListAdapter(view.context, UserView::class.java)
        adapter?.itemClickListener = object : OnItemClickListener<User> {
            override fun onItemClick(item: User) {
                val i = Intent(view.context, ProfileActivity::class.java)
                i.putExtra("currentUser", item)
                startActivity(i)
            }
        }

        pageContent.adapter = adapter
        adapter?.notifyDataSetChanged()
    }

}