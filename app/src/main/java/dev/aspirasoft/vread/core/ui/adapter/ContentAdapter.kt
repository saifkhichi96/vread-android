package dev.aspirasoft.vread.core.ui.adapter

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import dev.aspirasoft.vread.books.ui.fragment.LibraryFragment
import dev.aspirasoft.vread.chat.ui.fragment.ConversationsFragment
import dev.aspirasoft.vread.feed.FeedFragment
import dev.aspirasoft.vread.settings.ui.fragment.SettingsFragment

class ContentAdapter(fm: FragmentManager, private val uid: String) : FragmentPagerAdapter(fm) {

    private val fragments = arrayListOf<Fragment>()

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        fragments.forEach { it.onActivityResult(requestCode, resultCode, data) }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return try {
            when (position) {
                0 -> "Home"
                1 -> "Friends"
                2 -> "Forums"
                3 -> "Settings"
                else -> throw IllegalArgumentException()
            }
        } catch (ex: Exception) {
            "Home"
        }
    }

    override fun getItem(position: Int): Fragment {
        return try {
            when (position) {
                0 -> LibraryFragment.newInstance(uid, true) // fixme: let users edit their own library only
                1 -> ConversationsFragment.newInstance(uid)
                2 -> FeedFragment.newInstance(uid)
                3 -> SettingsFragment.newInstance(uid)
                else -> throw IllegalArgumentException()
            }
        } catch (ex: Exception) {
            ConversationsFragment.newInstance(uid)
        }.also {
            fragments.add(it)
        }
    }

    override fun getCount(): Int {
        return 4
    }
}