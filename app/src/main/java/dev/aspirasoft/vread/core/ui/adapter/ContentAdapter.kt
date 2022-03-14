package dev.aspirasoft.vread.core.ui.adapter

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.aspirasoft.vread.books.ui.fragment.LibraryFragment
import dev.aspirasoft.vread.chat.ui.fragment.ConversationsFragment
import dev.aspirasoft.vread.feed.FeedFragment
import dev.aspirasoft.vread.notifications.ui.fragment.NotificationsFragment

class ContentAdapter(fm: FragmentActivity, private val uid: String) : FragmentStateAdapter(fm) {

    private val fragments = arrayListOf<Fragment>()

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        fragments.forEach { it.onActivityResult(requestCode, resultCode, data) }
    }

    fun getPageTitle(position: Int): CharSequence? {
        return try {
            when (position) {
                0 -> "Home"
                1 -> "Friends"
                2 -> "Forums"
                3 -> "Updates"
                else -> throw IllegalArgumentException()
            }
        } catch (ex: Exception) {
            "Home"
        }
    }

    override fun createFragment(position: Int): Fragment {
        return try {
            when (position) {
                0 -> LibraryFragment.newInstance(uid, true) // fixme: let users edit their own library only
                1 -> ConversationsFragment.newInstance(uid)
                2 -> FeedFragment.newInstance(uid)
                3 -> NotificationsFragment.newInstance(uid)
                else -> throw IllegalArgumentException()
            }
        } catch (ex: Exception) {
            ConversationsFragment.newInstance(uid)
        }.also {
            fragments.add(it)
        }
    }

    override fun getItemCount(): Int {
        return 4
    }
}