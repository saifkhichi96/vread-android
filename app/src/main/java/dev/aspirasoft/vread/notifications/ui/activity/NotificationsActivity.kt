package dev.aspirasoft.vread.notifications.ui.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import dev.aspirasoft.vread.core.ui.activity.SecureActivity
import dev.aspirasoft.vread.core.ui.adapter.ListAdapter
import dev.aspirasoft.vread.databinding.ActivityNotificationsBinding
import dev.aspirasoft.vread.notifications.data.source.NotificationDataSource
import dev.aspirasoft.vread.notifications.model.Notification
import dev.aspirasoft.vread.notifications.ui.view.NotificationView
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationsActivity : SecureActivity() {

    private lateinit var binding: ActivityNotificationsBinding

    private lateinit var adapter: ListAdapter<Notification>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ListAdapter(this, NotificationView::class.java)
        lifecycleScope.launch {
            val data = NotificationDataSource.getAll(signedInUser.uid).await()
            data.children.forEach { snapshot ->
                val notification = snapshot.getValue(Notification::class.java)
                notification?.let { adapter.add(it) }
            }
            binding.notificationsList.adapter = adapter
        }
    }

}