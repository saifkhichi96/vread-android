package sfllhkhan95.connect.auth.ui.view

import android.content.Context
import android.widget.TextView
import sfllhkhan95.connect.R
import sfllhkhan95.connect.auth.model.User
import sfllhkhan95.connect.core.ui.view.holder.ViewHolder
import sfllhkhan95.connect.profile.ui.view.AvatarView

class UserView(context: Context) : ViewHolder<User>(context, R.layout.listitem_search_result) {

    var name: TextView? = itemView.findViewById(R.id.name)
    var username: TextView? = itemView.findViewById(R.id.username)
    var avatar: AvatarView? = itemView.findViewById(R.id.avatar)

    override fun updateWith(data: User) {
        name?.text = data.firstName
        username?.text = data.username
        avatar?.showUser(data.uid)
    }

}