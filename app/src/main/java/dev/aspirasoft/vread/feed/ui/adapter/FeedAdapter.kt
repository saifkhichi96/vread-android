package dev.aspirasoft.vread.feed.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.OnTouchListener
import android.widget.ArrayAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.databinding.ViewFeedPostBinding
import dev.aspirasoft.vread.feed.model.Post
import dev.aspirasoft.vread.feed.model.PostLike
import dev.aspirasoft.vread.feed.ui.holder.FeedPostHolder


class FeedAdapter(
    context: Context,
    private val currentUserId: String,
    private val posts: ArrayList<Post>,
) : ArrayAdapter<Post>(context, R.layout.view_feed_post, posts) {

    // FIXME: This should be inside FeedDataSource
    private val likesRef = Firebase.database.getReference("feed/likes/")

    var currentVideo: MediaPlayer? = null

    override fun notifyDataSetChanged() {
        posts.sortByDescending { it.timestamp }
        super.notifyDataSetChanged()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ViewFeedPostBinding.inflate(LayoutInflater.from(context), parent, false)

        val holder = FeedPostHolder(binding)
        showPostAuthor(holder, position)

        val currentPost = posts[position]

        // Show a live counter for post likes
        likesRef.orderByChild(PostLike::postId.name)
            .equalTo(currentPost.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    holder.showLikeCount(snapshot.childrenCount.toInt())
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        // Show like icon
        likesRef.orderByChild(PostLike::postId.name)
            .equalTo(currentPost.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var liked = false
                    snapshot.children.forEach {
                        if (it.getValue(PostLike::class.java)?.userId == currentUserId) {
                            liked = true
                            return@forEach
                        }
                    }
                    holder.showLiked(liked)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        binding.postBody.text = currentPost.content

        // Show timestamp formatted as "X minutes/hours/days ago"
        val timestamp = currentPost.timestamp
        val minutes = (System.currentTimeMillis() - timestamp) / 60000
        val hours = minutes / 60
        val days = hours / 24
        val weeks = days / 7
        val months = days / 30
        val years = days / 365
        val time = when {
            years > 0 -> "$years years ago"
            months > 0 -> "$months months ago"
            weeks > 0 -> "$weeks weeks ago"
            days > 0 -> "$days days ago"
            hours > 0 -> "$hours hours ago"
            minutes > 0 -> "$minutes minutes ago"
            else -> "Just now"
        }
        binding.postTimestamp.text = time
        when (val url = currentPost.attachment) {
            null -> holder.hideAttachmentFrame()
            else -> holder.showAttachment(url)
        }

        binding.postLikeButton.setOnClickListener { onLikeClicked(holder, position) }

        val doubleTapListener = DoubleTapListener(holder, position)
        binding.postMediaImage.setOnTouchListener(doubleTapListener)
        binding.postMediaVideo.setOnTouchListener(doubleTapListener)

        return binding.root
    }

    private fun showPostAuthor(v: FeedPostHolder, position: Int) {
        val authorId = posts[position].postedBy
        v.postAuthorImage.showUser(authorId)
        v.postAuthorName.text = authorId  // fixme: show author name instead of id
        //UsersDao.getUserById(schoolId, authorId) { user: User ->
        //    authorName.text = user.firstName
        //    authorType.text = user.username
        //}
    }

    private fun onLikeClicked(holder: FeedPostHolder, position: Int) {
        val currentPost = posts[position]
        likesRef.orderByChild(PostLike::postId.name)
            .equalTo(currentPost.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val liked = ArrayList<DataSnapshot>()
                    snapshot.children.forEach {
                        if (it.getValue(PostLike::class.java)?.userId == currentUserId) {
                            liked.add(it)
                        }
                    }
                    if (liked.size <= 0) {
                        val like = PostLike(currentUserId, currentPost.id)
                        likesRef.push().setValue(like)
                        holder.showLiked(true)
                    } else {
                        liked.forEach { likesRef.child(it.key!!).removeValue() }
                        holder.showLiked(false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun onVideoClicked(holder: FeedPostHolder, position: Int) {
        holder.mediaPlayer?.let {
            kotlin.runCatching { currentVideo?.stop() }
            currentVideo = it
            if (it.isPlaying) {
                it.pause()
            } else {
                it.start()
                it.isLooping = false
            }
        }
    }

    private inner class DoubleTapListener(
        private val holder: FeedPostHolder,
        position: Int,
    ) : OnTouchListener {

        private val gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                onLikeClicked(holder, position)
                return super.onDoubleTap(e)
            }

            override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                onVideoClicked(holder, position)
                return false
            }
        })

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            gestureDetector.onTouchEvent(event)
            return true
        }
    }

}