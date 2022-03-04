package sfllhkhan95.connect.chat.ui.view

import android.content.Context
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import sfllhkhan95.connect.R
import sfllhkhan95.connect.auth.model.User
import sfllhkhan95.connect.chat.model.Conversation
import sfllhkhan95.connect.core.ui.view.holder.ViewHolder
import sfllhkhan95.connect.profile.ui.view.AvatarView


/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 2019-05-27 18:37
 */
class ConversationView(context: Context) : ViewHolder<Conversation>(context, R.layout.listitem_conversation) {

    private val avatar: AvatarView? = itemView.findViewById(R.id.avatar)
    private val title: TextView? = itemView.findViewById(R.id.conversationTitle)
    private val snippet: TextView? = itemView.findViewById(R.id.conversationSnippet)
    private val time: TextView? = itemView.findViewById(R.id.lastMessageOn)

    override fun updateWith(data: Conversation) {
        // Show conversation title and photo
        if (data.participants.size <= 2) {
            for (user in data.participants) {
                if (user != Firebase.auth.currentUser?.uid) {
                    FirebaseDatabase.getInstance()
                        .getReference("users/$user/meta/")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.getValue(User::class.java)?.let { u ->
                                    title?.text = "${u.firstName} ${u.lastName}"
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                title?.text = data.name
                            }
                        })

                    // Show conversation photo
                    avatar?.showUser(user)
                    break
                }
            }
        } else {
            title?.text = data.name
            // TODO: Show group conversation photo
        }

        // FIXME: Show a snippet of latest message
        // CoroutineScope(Dispatchers.Main).launch {
            // val lastMessage = repo.getMessages(data.id).lastOrNull()
            // snippet?.text = lastMessage?.body
        //}
    }

}