package sfllhkhan95.connect.chat.ui.activity

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import io.github.saifkhichi96.android.db.model.DatabaseEvent
import kotlinx.android.synthetic.main.base_activity.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import sfllhkhan95.connect.R
import sfllhkhan95.connect.chat.data.repo.ChatRepository
import sfllhkhan95.connect.chat.model.Conversation
import sfllhkhan95.connect.chat.model.Message
import sfllhkhan95.connect.chat.ui.adapter.MessageAdapter
import sfllhkhan95.connect.core.ui.activity.TitledActivity
import sfllhkhan95.connect.databinding.ActivityConversationBinding
import sfllhkhan95.connect.notifications.util.NotificationSender
import javax.inject.Inject

@AndroidEntryPoint
class ConversationActivity : TitledActivity(), View.OnClickListener {

    private lateinit var binding: ActivityConversationBinding

    private lateinit var conversation: Conversation

    @Inject
    lateinit var repo: ChatRepository

    private lateinit var messageAdapter: MessageAdapter
    private val messages = ArrayList<Message>()

    private val dateList: ArrayList<String> = ArrayList()
    private val dateFinalList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(null)
        binding = ActivityConversationBinding.inflate(layoutInflater, container, true)
        setTitle("Chat")
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        // Set up event handlers
        binding.sendMessage.setOnClickListener(this)
        binding.suggestion0.setOnClickListener(this)
        binding.suggestion1.setOnClickListener(this)
        binding.suggestion2.setOnClickListener(this)

        conversation = intent.getSerializableExtra("conversation") as Conversation? ?: return finish()
        updateUI(conversation)

        binding.sendMessage.setOnClickListener { onSendMessageClicked() }
    }

    override fun onResume() {
        super.onResume()

        messageAdapter = MessageAdapter(this, messages, dateFinalList, binding.messageInput, signedInUser)
        binding.messageList.adapter = messageAdapter
        val manager = LinearLayoutManager(this)
        binding.messageList.layoutManager = manager
        manager.stackFromEnd = true
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.suggestion0,
            R.id.suggestion1,
            R.id.suggestion2,
            -> {
                sendMessage((v as MaterialButton).text.toString())
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }

    private fun updateUI(conversation: Conversation) {
        this.conversation = conversation
        lifecycleScope.launch {
            repo.observe(conversation.id).collect { event ->
                when (event) {
                    is DatabaseEvent.Changed -> {
                        // TODO: Update existing message in adapter
                    }
                    is DatabaseEvent.Created -> onMessageReceived(event.item)
                    is DatabaseEvent.Deleted -> {
                        // TODO: Delete message from adapter
                    }
                    is DatabaseEvent.Cancelled -> {
                        /* no-op */
                    }
                }
            }
        }

        // Show conversation title
        conversation.name?.let { setTitles(it, "Chat") }
    }

    private fun onMessageReceived(message: Message) {
        if (messages.contains(message)) return

        dateList.add(message.dateSentOn())
        dateFinalList.clear()
        for (x in dateList.indices) {
            if (dateFinalList.contains(dateList[x])) {
                dateFinalList.add("")
            } else {
                dateFinalList.add(dateList[x])
            }
        }

        // Mark as read (if not always read)
        if (!message.readReports.containsKey(signedInUser.uid)) {
            lifecycleScope.launch {
                repo.readMessage(conversation.id, message.id, signedInUser.uid)
            }
            message.readReports[signedInUser.uid] = System.currentTimeMillis()
        }

        messages.add(message)
        messageAdapter.notifyDataSetChanged()

        binding.messageList.layoutManager?.scrollToPosition(messageAdapter.itemCount - 1)
        binding.messageList.smoothScrollToPosition(messageAdapter.itemCount - 1)
    }

    private fun onSendMessageClicked() {
        val messageText = binding.messageInput.text.toString()
        if (messageText.isNotBlank()) {
            sendMessage(messageText)
            binding.messageInput.text?.clear()
        }
    }

    private fun sendMessage(text: String) {
        if (!::conversation.isInitialized) return
        if (text.isNotBlank()) {
            lifecycleScope.launch {
                try {
                    val message = Message(
                        body = text.trim(),
                        senderId = signedInUser.uid,
                    )

                    repo.addMessage(conversation.id, message)
                    NotificationSender.sendAll(
                        applicationContext,
                        "You have a new message.",
                        text,
                        conversation.participants.filterNot { it == signedInUser.uid }
                    )
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            hideSuggestions()
        }
    }

    private fun hideSuggestions() {
        binding.suggestions.visibility = View.GONE
        binding.suggestion0.visibility = View.GONE
        binding.suggestion1.visibility = View.GONE
        binding.suggestion2.visibility = View.GONE
    }

    companion object {
        const val EXTRA_CHAT_RECIPIENT = "chat_recipient"
    }

}