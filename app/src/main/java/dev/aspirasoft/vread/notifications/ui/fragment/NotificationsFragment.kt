package dev.aspirasoft.vread.notifications.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.core.ui.adapter.ListAdapter
import dev.aspirasoft.vread.notifications.data.source.NotificationDataSource
import dev.aspirasoft.vread.notifications.model.Notification
import dev.aspirasoft.vread.notifications.ui.view.NotificationView
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationsFragment : Fragment() {

    private lateinit var adapter: ListAdapter<Notification>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_notifications, container, false)

        adapter = ListAdapter(v.context, NotificationView::class.java)
        arguments?.getString("user")?.let { uid ->
            lifecycleScope.launch {
                val data = NotificationDataSource.getAll(uid).await()
                data.children.forEach { snapshot ->
                    val notification = snapshot.getValue(Notification::class.java)
                    notification?.let { adapter.add(it) }
                }
            }
        }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationsList.adapter = adapter
    }

    companion object {
        fun newInstance(uid: String): NotificationsFragment {
            val args = Bundle()
            args.putString("user", uid)

            val fragment = NotificationsFragment()
            fragment.arguments = args
            return fragment
        }
    }

}