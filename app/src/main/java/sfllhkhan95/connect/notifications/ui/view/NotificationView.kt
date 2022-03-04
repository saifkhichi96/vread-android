package sfllhkhan95.connect.notifications.ui.view

import android.content.Context
import android.widget.TextView
import sfllhkhan95.connect.core.ui.view.holder.ViewHolder
import sfllhkhan95.connect.notifications.model.Notification
import java.text.DateFormat

class NotificationView(context: Context) : ViewHolder<Notification>(context, android.R.layout.simple_list_item_2) {

    override fun updateWith(data: Notification) {
        view.findViewById<TextView?>(android.R.id.text1)?.text = data.title

        val timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(data.timestamp)
        view.findViewById<TextView?>(android.R.id.text2)?.text = "${data.message}\n${timestamp}"
    }

}