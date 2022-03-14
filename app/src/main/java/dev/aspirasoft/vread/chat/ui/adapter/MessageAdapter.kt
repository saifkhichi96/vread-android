package dev.aspirasoft.vread.chat.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.auth.model.User
import dev.aspirasoft.vread.chat.model.Message

class MessageAdapter(
    private val context: Context,
    private val messageList: List<Message>,
    private val dateList: List<String>,
    private val messageArea: TextView,
    private val currentUser: User,
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.view_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]

        holder.messageTime.textSize = 10f
        holder.messageTime.text = message.timeSentOn()
        holder.messageBody.text = HtmlCompat.fromHtml(message.body, HtmlCompat.FROM_HTML_MODE_LEGACY)
        holder.messageBody.textSize = 16f

        if (message.senderId == currentUser.uid) {
            holder.messageBody.setTextColor(Color.WHITE)

            holder.messageLayout.setBackgroundResource(R.drawable.bg_message_outgoing)
            holder.messageDirection.gravity = Gravity.END
        } else {
            holder.messageSender.visibility = View.VISIBLE
            holder.messageSender.text = message.senderId
            holder.messageBody.setTextColor(Color.BLACK)

            holder.messageLayout.setBackgroundResource(R.drawable.bg_message_incoming)
            holder.messageDirection.gravity = Gravity.START
        }

        if (dateList[position].isEmpty()) {
            holder.messageDate.visibility = View.GONE
        } else {
            holder.messageDate.text = dateList[position]
        }

        messageArea.text = ""
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageBody: TextView = itemView.findViewById(R.id.message)
        var messageSender: TextView = itemView.findViewById(R.id.sender)
        var messageDate: TextView = itemView.findViewById(R.id.date)
        var messageTime: TextView = itemView.findViewById(R.id.timestamp)

        var messageLayout: LinearLayout = itemView.findViewById(R.id.fr_chat)
        var messageDirection: LinearLayout = itemView.findViewById(R.id.ll_chat)
    }
}