package sfllhkhan95.connect.profile.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sfllhkhan95.connect.R
import sfllhkhan95.connect.auth.model.User
import sfllhkhan95.connect.storage.FileManager


class CoverView : AppCompatImageView {

    constructor(context: Context) : super(context) {
        setImageResource(R.drawable.placeholder_cover)
        scaleType = ScaleType.CENTER_CROP
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setImageResource(R.drawable.placeholder_cover)
        scaleType = ScaleType.CENTER_CROP
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setImageResource(R.drawable.placeholder_cover)
        scaleType = ScaleType.CENTER_CROP
    }

    /**
     * Shows the profile photo of the specified user.
     *
     * @param uid [User.uid] of the user whose avatar is to be displayed
     */
    fun showUser(uid: String, update: Boolean = false) = CoroutineScope(Dispatchers.Main).launch {
        val fm = FileManager.newInstance(context, "users/$uid/")
        fm.download("cover.jpg", invalidate = update).let { result ->
            val imageFile = result.getOrNull()
            val imageRequest = Glide.with(context).asBitmap()
            when (imageFile) {
                null -> imageRequest.load(R.drawable.placeholder_cover)
                else -> imageRequest.load(imageFile)
            }.centerCrop()
                .placeholder(R.drawable.placeholder_cover)
                .into(this@CoverView)
        }
    }

}