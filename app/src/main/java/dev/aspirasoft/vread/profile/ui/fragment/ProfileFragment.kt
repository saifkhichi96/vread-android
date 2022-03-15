package dev.aspirasoft.vread.profile.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.auth.data.repo.UserRepository
import dev.aspirasoft.vread.auth.model.User
import dev.aspirasoft.vread.databinding.ActivityProfileBinding
import dev.aspirasoft.vread.profile.data.source.ProfileDataSource
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: ActivityProfileBinding

    @Inject
    lateinit var repo: ProfileDataSource

    @Inject
    lateinit var users: UserRepository

    private lateinit var currentUser: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ActivityProfileBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            val uid = arguments?.getString("user_id") ?: ""
            currentUser = users.getById(uid).getOrNull() ?: User()

            // Show cover photo
            binding.cover.showUser(currentUser.uid)

            binding.profileSummary.adapter = SummaryAdapter(requireActivity().supportFragmentManager)
            binding.summaryIndicators.setupWithViewPager(binding.profileSummary)
            binding.profileSummary.currentItem = 0

            binding.profileContent.adapter = ContentAdapter(requireActivity().supportFragmentManager)
            binding.profileSections.setupWithViewPager(binding.profileContent)
            binding.profileContent.currentItem = 0
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.profileContent.visibility = View.VISIBLE
        binding.profileSections.visibility = View.VISIBLE
        binding.profileContentDisallowed.visibility = View.GONE
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
            args.putBoolean("isOwnProfile", true)
            args.putSerializable("user", currentUser)
            fragment.arguments = args

            return fragment

        }

        override fun getCount(): Int {
            return mPages.size
        }
    }

    companion object {
        fun newInstance(uid: String): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putString("user_id", uid)
            fragment.arguments = args
            return fragment
        }
    }

}