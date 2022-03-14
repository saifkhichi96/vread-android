package dev.aspirasoft.vread.profile.ui.view

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.auth.data.repo.UserRepository
import dev.aspirasoft.vread.auth.model.User
import dev.aspirasoft.vread.profile.ui.activity.ProfileActivity
import dev.aspirasoft.vread.storage.FileManager
import kotlinx.android.synthetic.main.view_avatar.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AvatarView : MaterialCardView {

    @Inject
    lateinit var repo: UserRepository

    var openProfileOnClick = true

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.view_avatar, this)
        user_photo.setImageResource(R.drawable.placeholder_avatar)
    }

    /**
     * Shows the profile photo of the specified user.
     *
     * @param uid [User.uid] of the user whose avatar is to be displayed
     */
    fun showUser(uid: String, update: Boolean = false) = CoroutineScope(Dispatchers.Main).launch {
        setOpenProfileOnClick(uid)

        val fm = FileManager.newInstance(context, "users/$uid/")
        fm.download("profile.png", invalidate = update).let { result ->
            val imageFile = result.getOrNull()
            val imageRequest = Glide.with(context).asBitmap()
            when (imageFile) {
                null -> imageRequest.load(R.drawable.placeholder_avatar)
                else -> imageRequest.load(imageFile)
            }.centerCrop()
                .placeholder(R.drawable.placeholder_avatar)
                .into(user_photo)
        }
    }

    private fun setOpenProfileOnClick(uid: String) {
        if (openProfileOnClick) {
            CoroutineScope(Dispatchers.Main).launch {
                val user = repo.getById(uid).getOrNull()
                setOnClickListener {
                    val i = Intent(context, ProfileActivity::class.java)
                    i.putExtra("currentUser", user)
                    ContextCompat.startActivity(context, i, null)
                }
            }
        }
    }

}