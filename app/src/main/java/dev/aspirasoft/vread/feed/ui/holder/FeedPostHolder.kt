package dev.aspirasoft.vread.feed.ui.holder

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.databinding.ViewFeedPostBinding
import dev.aspirasoft.vread.feed.ui.view.SquareFrameLayout
import dev.aspirasoft.vread.profile.ui.view.AvatarView

class FeedPostHolder(binding: ViewFeedPostBinding) : ViewHolder(binding.root) {

    val postAuthorImage: AvatarView = binding.postAuthorImage
    val postAuthorName: TextView = binding.postAuthorName
    val postBody: TextView = binding.postBody
    val postMedia: SquareFrameLayout = binding.postMedia
    val postImage: ImageView = binding.postMediaImage
    val postVideo: VideoView = binding.postMediaVideo
    val likeButton: MaterialButton = binding.postLikeButton

    fun showAttachment(attachmentUrl: String) {
        val filename = attachmentUrl.substringBeforeLast("?")
        val filetype = filename.substringAfterLast(".").lowercase()

        val supportedImages = arrayOf("jpg", "jpeg", "png", "webp", "bmp", "tif", "tiff", "svg")
        val supportedVideos = arrayOf("mp4", "3gp", "gif", "avi", "mov", "mkv", "webm")
        when (filetype) {
            in supportedImages -> {
                postImage.visibility = View.VISIBLE
                postVideo.visibility = View.GONE

                Glide.with(postImage)
                    .load(attachmentUrl)
                    .into(postImage)
            }
            in supportedVideos -> {
                postVideo.stopPlayback()
                postVideo.resume()

                Glide.with(postImage)
                    .load(attachmentUrl)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.5f)
                    .centerInside()
                    .into(postImage)

                postVideo.visibility = View.VISIBLE
                postVideo.setVideoURI(Uri.parse(attachmentUrl))
                postVideo.setOnPreparedListener {
                    postImage.visibility = View.GONE
                    postVideo.start()
                    it.isLooping = true
                }
            }
        }

        postMedia.visibility = View.VISIBLE
    }

    fun hideAttachmentFrame() {
        postMedia.visibility = View.GONE
        postImage.visibility = View.GONE
        postVideo.visibility = View.GONE
    }

    /**
     * Show the post as liked/unliked by the current user.
     *
     * @param liked true if the post is liked by the current user, false otherwise
     */
    fun showLiked(liked: Boolean) {
        likeButton.setIconResource(if (liked) R.drawable.ic_like else R.drawable.ic_like_outline)
    }

    /**
     * Show the like count for the post.
     *
     * @param count the number of likes for the post
     */
    fun showLikeCount(count: Int) {
        likeButton.text = "$count" + if (count == 1) " Like" else "  Likes"
    }

}