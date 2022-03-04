package sfllhkhan95.connect.core.ui.adapter

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import sfllhkhan95.connect.chat.ui.fragment.ConversationsFragment
import sfllhkhan95.connect.feed.FeedFragment
import sfllhkhan95.connect.notifications.ui.fragment.NotificationsFragment

class ContentAdapter(fm: FragmentActivity, private val uid: String) : FragmentStateAdapter(fm) {

    val fragments = arrayListOf<Fragment>()

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        fragments.forEach { it.onActivityResult(requestCode, resultCode, data) }
    }

    fun getPageTitle(position: Int): CharSequence? {
        return try {
            when (position) {
                0 -> "Chats"
                1 -> "Posts"
                2 -> "Notifications"
                else -> throw IllegalArgumentException()
            }
        } catch (ex: Exception) {
            "Chats"
        }
    }

    override fun createFragment(position: Int): Fragment {
        return try {
            when (position) {
                0 -> ConversationsFragment.newInstance(uid)
                1 -> FeedFragment.newInstance(uid)
                2 -> NotificationsFragment.newInstance(uid)
                else -> throw IllegalArgumentException()
            }
        } catch (ex: Exception) {
            ConversationsFragment.newInstance(uid)
        }.also {
            fragments.add(it)
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}