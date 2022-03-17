package dev.aspirasoft.vread.feed.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.feed.model.Post
import dev.aspirasoft.vread.feed.model.PostLike
import dev.aspirasoft.vread.profile.ui.view.AvatarView


class FeedAdapter(
    context: Context,
    private val currentUserId: String,
    private val posts: ArrayList<Post>,
) : ArrayAdapter<Post>(context, R.layout.view_feed_post, posts) {

    // FIXME: This should be inside FeedDataSource
    private val likesRef = Firebase.database.getReference("feed/likes/")

    override fun notifyDataSetChanged() {
        posts.sortByDescending { it.timestamp }
        super.notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View = LayoutInflater.from(context).inflate(R.layout.view_feed_post, parent, false)

        showPostAuthor(v, position)

        val likeButton: MaterialButton = v.findViewById(R.id.iv_like)
        val postImage: ImageView = v.findViewById(R.id.iv_feed)
        val likesCountLabel: TextView = v.findViewById(R.id.tv_likes)
        val postCaption: TextView = v.findViewById(R.id.txt_descc)
        val postVideo: VideoView = v.findViewById(R.id.vdd_feed)

        val currentPost = posts[position]

        // Show a live counter for post likes
        likesRef.orderByChild(PostLike::postId.name)
            .equalTo(currentPost.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val likeCount = snapshot.childrenCount.toInt()
                    likesCountLabel.text = "$likeCount" + if (likeCount == 1) " Like" else "  Likes"
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
                    showLiked(likeButton, liked)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        // Show post description
        postCaption.text = currentPost.content

        // Show attachment
        val attachmentUrl = currentPost.attachment
        if (attachmentUrl != null) {
            showAttachment(attachmentUrl, postImage, postVideo)
        }

        likeButton.setOnClickListener { onLikeClicked(position, likeButton) }

        val doubleTapListener = DoubleTapListener(position, likeButton)
        postImage.setOnTouchListener(doubleTapListener)
        postVideo.setOnTouchListener(doubleTapListener)
        return v
    }

    private fun showAttachment(attachmentUrl: String, imageView: ImageView, videoView: VideoView) {
        val filename = attachmentUrl.substringBeforeLast("?")
        val filetype = filename.substringAfterLast(".").lowercase()

        val supportedImages = arrayOf("jpg", "jpeg", "png", "webp", "bmp", "tif", "tiff", "svg")
        val supportedVideos = arrayOf("mp4", "3gp", "gif", "avi", "mov", "mkv", "webm")
        when (filetype) {
            in supportedImages -> {
                imageView.visibility = VISIBLE
                videoView.visibility = GONE

                Glide.with(context)
                    .load(attachmentUrl)
                    .into(imageView)
            }
            in supportedVideos -> {
                videoView.stopPlayback()
                videoView.resume()

                Glide.with(context)
                    .load(attachmentUrl)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.5f)
                    .centerInside()
                    .into(imageView)

                videoView.visibility = VISIBLE
                videoView.setVideoURI(Uri.parse(attachmentUrl))
                videoView.setOnPreparedListener {
                    imageView.visibility = GONE
                    videoView.start()
                    it.isLooping = true
                }
            }
        }
    }

    private fun showLiked(likeButton: MaterialButton, liked: Boolean) {
        val color = if (liked) R.color.colorError else R.color.colorOnPrimary // fixme: use attr colors
        likeButton.iconTint = ColorStateList.valueOf(ContextCompat.getColor(context, color))
        likeButton.setIconResource(if (liked) R.drawable.ic_like else R.drawable.ic_like_outline)
    }

    private fun showPostAuthor(v: View, position: Int) {
        val authorId = posts[position].postedBy

        // Show author photo
        val postAuthorPhoto = v.findViewById<AvatarView>(R.id.post_author_photo)
        postAuthorPhoto.showUser(authorId)

        // FIXME: Show author name
        val authorName: TextView = v.findViewById(R.id.post_author_name)
        val authorType: TextView = v.findViewById(R.id.post_author_type)
        //UsersDao.getUserById(schoolId, authorId) { user: User ->
        //    authorName.text = user.firstName
        //    authorType.text = user.username
        //}
    }

    private fun onLikeClicked(position: Int, likeButton: MaterialButton) {
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
                        showLiked(likeButton, true)
                    } else {
                        liked.forEach {
                            likesRef.child(it.key!!).removeValue()
                        }
                        showLiked(likeButton, false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private inner class DoubleTapListener(
        position: Int,
        private val likeButton: MaterialButton,
    ) : OnTouchListener {

        private val gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                onLikeClicked(position, likeButton)
                return super.onDoubleTap(e)
            }

            override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
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