package sfllhkhan95.connect.profile.ui.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.launch
import sfllhkhan95.connect.R
import sfllhkhan95.connect.auth.model.User
import sfllhkhan95.connect.core.ui.activity.SecureActivity
import sfllhkhan95.connect.profile.data.source.ProfileDataSource
import sfllhkhan95.connect.profile.ui.fragment.*
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : SecureActivity() {

    @Inject
    lateinit var repo: ProfileDataSource

    private var unfollowButton: MenuItem? = null
    private var isFollowing = false
        set(value) {
            field = value

            unfollowButton?.isVisible = !isOwnProfile && isFollowing
            if (!isOwnProfile) {
                if (isFollowing) {
                    profileContent.visibility = View.VISIBLE
                    profileSections.visibility = View.VISIBLE
                    profileContentDisallowed.visibility = View.GONE
                } else {
                    profileContent.visibility = View.GONE
                    profileSections.visibility = View.GONE
                    profileContentDisallowed.visibility = View.VISIBLE
                }
            }
        }

    private var isOwnProfile = true

    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Set up action bar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        // Which user's profile to display?
        val user = intent.getSerializableExtra("currentUser") as User?
        if (user == null) {
            finish()
            return
        }
        currentUser = user

        // Show cover photo
        cover.showUser(currentUser.uid)

        profileSummary.adapter = SummaryAdapter(supportFragmentManager)
        summaryIndicators.setupWithViewPager(profileSummary)
        profileSummary.currentItem = 0

        profileContent.adapter = ContentAdapter(supportFragmentManager)
        profileSections.setupWithViewPager(profileContent)
        profileContent.currentItem = 0

        isOwnProfile = currentUser.uid == signedInUser.uid
        action_follow.setOnClickListener {
            follow()
        }
    }

    override fun onStart() {
        super.onStart()
        if (!isOwnProfile) {
            lifecycleScope.launch {
                isFollowing = repo.getFollowers(currentUser.uid).firstOrNull { it.uid == signedInUser.uid } != null
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isOwnProfile) {
            profileContent.visibility = View.VISIBLE
            profileSections.visibility = View.VISIBLE
            profileContentDisallowed.visibility = View.GONE
        } else {
            profileContent.visibility = View.GONE
            profileSections.visibility = View.GONE
            profileContentDisallowed.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_profile, menu)

        // Edit button is displayed only on own profile
        menu.findItem(R.id.action_edit).isVisible = isOwnProfile
        unfollowButton = menu.findItem(R.id.action_unfollow).apply { isVisible = !isOwnProfile && isFollowing }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_unfollow -> {
                if (isFollowing) {
                    val prompt = String.format(getString(R.string.confirmation_unfollow), currentUser.username)
                    AlertDialog.Builder(this)
                        .setTitle(HtmlCompat.fromHtml(prompt, HtmlCompat.FROM_HTML_MODE_LEGACY))
                        .setPositiveButton(getString(R.string.action_unfollow)) { _: DialogInterface, _: Int ->
                            unfollow()
                        }
                        .setNegativeButton(getString(android.R.string.cancel)) { dialog: DialogInterface, _: Int ->
                            dialog.dismiss()
                        }
                        .show()
                }
                return true
            }
            R.id.action_edit -> {
                startActivity(Intent(this, EditProfileActivity::class.java))
                return true
            }
            else -> return false
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (profileContent.currentItem > 0) {
            profileContent.currentItem = profileContent.currentItem - 1
        } else {
            super.onBackPressed()
        }
    }

    private fun follow() {
        action_follow.text = "Following ..."
        action_follow.isEnabled = false
        lifecycleScope.launch {
            try {
                repo.addFollower(currentUser.uid, signedInUser.uid)
                action_follow.text = getString(R.string.action_follow)
                action_follow.isEnabled = true
            } catch (ex: Exception) {
                makeToast(ex.message ?: ex::class.java.simpleName)
            }
        }
    }

    private fun unfollow() {
        lifecycleScope.launch {
            try {
                repo.removeFollower(currentUser.uid, signedInUser.uid)
            } catch (ex: Exception) {
                makeToast(ex.message ?: ex::class.java.simpleName)
            }
        }
    }

    private inner class SummaryAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        private val mPageTitles = ArrayList<String>()
        private val mPages = ArrayList<Class<out Fragment>>()

        init {
            mPages.add(ProfileSummaryFragment::class.java)
            mPageTitles.add("")

            mPages.add(ProfileDetailsFragment::class.java)
            mPageTitles.add("")
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return try {
                mPageTitles[position]
            } catch (ex: Exception) {
                ""
            }

        }

        override fun getItem(position: Int): Fragment {
            val fragment = try {
                val c = mPages[position].getConstructor()
                c.newInstance()
            } catch (e: Exception) {
                ProfileDetailsFragment()
            }

            val args = Bundle()
            args.putSerializable("user", currentUser)
            fragment.arguments = args

            return fragment
        }

        override fun getCount(): Int {
            return mPages.size
        }
    }

    private inner class ContentAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        private val mPageTitles = ArrayList<String>()
        private val mPages = ArrayList<Class<out Fragment>>()

        init {
            // TODO: Get actual post count
            mPages.add(TimelineFragment::class.java)
            mPageTitles.add("0\nPosts")

            mPages.add(1, FollowersFragment::class.java)
            mPageTitles.add(1, String.format("0\n%s", getString(R.string.label_followers)))
            lifecycleScope.launch {
                val count = repo.getFollowerCount(currentUser.uid)
                mPageTitles.removeAt(1)
                mPageTitles.add(1, String.format("%d\n%s", count, getString(R.string.label_followers)))
                notifyDataSetChanged()
            }

            mPages.add(2, FollowingFragment::class.java)
            mPageTitles.add(2, String.format("0\n%s", getString(R.string.label_following)))

            lifecycleScope.launch {
                val count = repo.getFollowingCount(currentUser.uid)
                mPageTitles.removeAt(2)
                mPageTitles.add(2, String.format("%d\n%s", count, getString(R.string.label_following)))
                notifyDataSetChanged()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return try {
                mPageTitles[position]
            } catch (ex: Exception) {
                "0\nPosts"
            }
        }

        override fun getItem(position: Int): Fragment {
            val fragment = try {
                val c = mPages[position].getConstructor()
                c.newInstance()
            } catch (e: Exception) {
                TimelineFragment()
            }

            val args = Bundle()
            args.putBoolean("isOwnProfile", isOwnProfile)
            args.putSerializable("user", currentUser)
            fragment.arguments = args

            return fragment

        }

        override fun getCount(): Int {
            return mPages.size
        }
    }

}