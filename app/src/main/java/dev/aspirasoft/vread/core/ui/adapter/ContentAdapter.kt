package dev.aspirasoft.vread.core.ui.adapter

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import dev.aspirasoft.vread.books.ui.fragment.LibraryFragment
import dev.aspirasoft.vread.feed.ui.fragment.FeedFragment
import dev.aspirasoft.vread.settings.ui.fragment.SettingsFragment

class ContentAdapter(fm: FragmentManager, private val uid: String) : FragmentPagerAdapter(fm) {

    private val fragments = arrayListOf<Fragment>()

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        fragments.forEach { it.onActivityResult(requestCode, resultCode, data) }
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FeedFragment.newInstance(uid)
            1 -> LibraryFragment.newInstance(uid, true) // fixme: let users edit their own library only
            2 -> LibraryFragment.newInstance(uid, true)
            3 -> SettingsFragment.newInstance(uid)
            else -> FeedFragment.newInstance(uid)
        }.also {
            fragments.add(it)
        }
    }

    override fun getCount(): Int {
        return 4
    }
}