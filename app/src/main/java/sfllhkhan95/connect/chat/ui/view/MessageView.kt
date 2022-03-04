package sfllhkhan95.connect.chat.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sfllhkhan95.connect.R
import sfllhkhan95.connect.auth.data.repo.UserRepository
import sfllhkhan95.connect.chat.model.Message
import sfllhkhan95.connect.core.ui.view.holder.ViewHolder
import sfllhkhan95.connect.profile.ui.view.AvatarView
import javax.inject.Inject

class MessageView(context: Context, private val type: MessageType) : ViewHolder<Message>(context, null) {

    @Inject
    lateinit var repo: UserRepository

    private var mBodyView: AppCompatTextView? = null
    private var mSenderView: AppCompatTextView? = null
    private var mTimestampView: AppCompatTextView? = null
    private var mAvatarView: AvatarView? = null

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        itemView = inflater.inflate(
            when (type) {
                MessageType.INCOMING -> R.layout.view_message_incoming
                MessageType.OUTGOING -> R.layout.view_message_outgoing
            }, null, false
        )

        mBodyView = itemView.findViewById(R.id.sendMessageButton)
        mSenderView = itemView.findViewById(R.id.sender)
        mTimestampView = itemView.findViewById(R.id.timestamp)
        mAvatarView = itemView.findViewById(R.id.avatar)

        // Hide timestamp by default
        mTimestampView?.visibility = View.GONE
        mSenderView?.visibility = View.GONE

        // Set up timestamp visibility toggle
        mBodyView?.setOnClickListener {
            if (mTimestampView?.visibility == View.GONE) {
                mTimestampView?.visibility = View.VISIBLE
                if (type == MessageType.INCOMING) mSenderView?.visibility = View.VISIBLE
            } else {
                mTimestampView?.visibility = View.GONE
                mSenderView?.visibility = View.GONE
            }
        }

        if (type == MessageType.OUTGOING) hideSender()
    }

    override fun updateWith(data: Message) {
        mBodyView?.text = data.body
        mTimestampView?.text = data.timeSentOn()
        mAvatarView?.showUser(data.senderId)

        mSenderView?.text = ""
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val sender = repo.getById(data.senderId).getOrNull()!!
                mSenderView?.text = sender.firstName
            } catch (ex: Exception) {
                mSenderView?.visibility = View.GONE
            }
        }
    }

    fun hideSender() {
        mSenderView?.visibility = View.GONE
        mAvatarView?.visibility = if (type == MessageType.INCOMING) View.INVISIBLE else View.GONE
    }

    fun removeSpacing() {
        itemView.findViewById<View>(R.id.spacing)?.visibility = View.GONE
    }

    enum class MessageType {
        INCOMING,
        OUTGOING
    }

}