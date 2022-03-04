package sfllhkhan95.connect.feed

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import sfllhkhan95.connect.R
import sfllhkhan95.connect.feed.data.repo.FeedRepository
import sfllhkhan95.connect.feed.model.Post
import sfllhkhan95.connect.feed.ui.adapter.FeedAdapter
import sfllhkhan95.connect.feed.ui.view.ComposeView
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() {

    /**
     * List of all feed posts.
     */
    private var postsList = ArrayList<Post>()

    private lateinit var feedAdapter: FeedAdapter

    private var videoView: VideoView? = null

    private var composeView: ComposeView? = null

    @Inject
    lateinit var feed: FeedRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_feed, container, false)

        try {
            val args = requireArguments()
            val userId = args.getString(ARG_USER)!!
            feedAdapter = FeedAdapter(requireContext(), userId, postsList)
            refreshFeed()

            composeView = v.findViewById(R.id.compose_view)
            composeView?.onNewPostListener = { postContent, attachment ->
                lifecycleScope.launch {
                    val status = Snackbar.make(
                        v,
                        requireContext().getString(R.string.status_posting),
                        Snackbar.LENGTH_INDEFINITE
                    ).also { it.show() }

                    try {
                        val post = feed.add(requireContext(), postContent, userId, attachment)
                        status.setText(requireContext().getString(R.string.status_post_success))

                        postsList.add(post)
                        feedAdapter.notifyDataSetChanged()
                    } catch (ex: Exception) {
                        status.setText(ex.message ?: requireContext().getString(R.string.status_post_failed))
                    }

                    Handler(Looper.getMainLooper()).postDelayed(status::dismiss, 1500L)
                }
            }

            val contentList = v.findViewById<ListView>(R.id.posts_list)
            contentList.adapter = feedAdapter
        } catch (ex: Exception) {

        }

        return v
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        composeView?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        composeView?.onRequestPermissionsResult(requireActivity(), requestCode, grantResults)
    }

    override fun onPause() {
        super.onPause()
        videoView?.stopPlayback()
        videoView?.resume()
    }

    override fun onStop() {
        super.onStop()
        videoView?.stopPlayback()
        videoView?.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView?.stopPlayback()
        videoView?.resume()
    }

    private fun refreshFeed() {
        postsList.clear()
        lifecycleScope.launch {
            val posts = feed.getAll()
            postsList.addAll(posts)
            feedAdapter.notifyDataSetChanged()
        }
    }

    companion object {

        private const val ARG_USER = "current_user"

        fun newInstance(userId: String): FeedFragment {
            val args = Bundle()
            args.putString(ARG_USER, userId)
            val fragment = FeedFragment()
            fragment.arguments = args
            return fragment
        }

    }

}