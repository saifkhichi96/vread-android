package sfllhkhan95.connect.chat.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_conversations.*
import kotlinx.coroutines.launch
import sfllhkhan95.connect.R
import sfllhkhan95.connect.chat.data.repo.ChatRepository
import sfllhkhan95.connect.chat.model.Conversation
import sfllhkhan95.connect.chat.ui.activity.ConversationActivity
import sfllhkhan95.connect.chat.ui.view.ConversationView
import sfllhkhan95.connect.core.ui.adapter.ListAdapter
import sfllhkhan95.connect.core.util.OnItemClickListener
import javax.inject.Inject

@AndroidEntryPoint
class ConversationsFragment : Fragment() {

    private lateinit var adapter: ListAdapter<Conversation>

    @Inject
    lateinit var repo: ChatRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_conversations, container, false)

        adapter = ListAdapter(v.context, ConversationView::class.java)
        arguments?.getString("user")?.let { uid ->
            lifecycleScope.launch {
                repo.listAll(uid).forEach {
                    adapter.add(it)
                }
            }
        }

        adapter.itemClickListener = object : OnItemClickListener<Conversation> {
            override fun onItemClick(item: Conversation) {
                val intent = Intent(v.context, ConversationActivity::class.java)
                intent.putExtra("conversation", item)

                ContextCompat.startActivity(v.context, intent, null)
            }
        }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pageContent.adapter = adapter
    }

    companion object {
        fun newInstance(uid: String): ConversationsFragment {
            val args = Bundle()
            args.putString("user", uid)

            val fragment = ConversationsFragment()
            fragment.arguments = args
            return fragment
        }
    }

}