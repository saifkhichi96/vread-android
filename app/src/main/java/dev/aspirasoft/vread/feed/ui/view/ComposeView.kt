package dev.aspirasoft.vread.feed.ui.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.core.util.FileUtils.getLastPathSegmentOnly
import dev.aspirasoft.vread.core.util.PermissionUtils
import dev.aspirasoft.vread.feed.model.Attachment
import dev.aspirasoft.vread.profile.ui.view.AvatarView

class ComposeView : RelativeLayout {

    private lateinit var mAvatarView: AvatarView
    private lateinit var mComposeText: TextInputEditText
    private lateinit var mSubmitButton: MaterialButton
    private lateinit var mAttachmentButton: MaterialButton

    private var pickRequestCode = RESULT_ACTION_PICK_MATERIAL

    private var attachment: Attachment? = null

    var onNewPostListener: ((postContent: String, attachment: Attachment?) -> Unit)? = null

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
        View.inflate(context, R.layout.view_compose, this)

        mComposeText = findViewById(R.id.compose_text)
        mSubmitButton = findViewById(R.id.submit_button)
        mSubmitButton.setOnClickListener {
            if (!mComposeText.text.isNullOrBlank()) {
                val postContent = mComposeText.text.toString()
                onNewPostListener?.invoke(postContent, attachment)

                mComposeText.text?.clear()
                mAttachmentButton.text = ""
            }
        }

        mAttachmentButton = findViewById(R.id.attachment_button)
        mAttachmentButton.setOnClickListener {
            if (context is Activity) onAttachmentButtonClicked(context)
        }

        mAvatarView = findViewById(R.id.avatar)
        Firebase.auth.currentUser?.uid?.let { mAvatarView.showUser(it) }

        setComposingEnabled(true)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RESULT_ACTION_PICK_MATERIAL -> {
                    data?.data?.let { onAttachmentPicked(it) }
                }
            }
        }
    }

    fun onRequestPermissionsResult(context: Activity, requestCode: Int, grantResults: IntArray) {
        if (requestCode == RC_WRITE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFile(context, pickRequestCode)
            }
        }
    }

    private fun onAttachmentButtonClicked(context: Activity) {
        if (PermissionUtils.requestPermissionIfNeeded(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                context.getString(R.string.permission_storage),
                RC_WRITE_PERMISSION
            )
        ) pickFile(context, RESULT_ACTION_PICK_MATERIAL)
    }

    private fun pickFile(context: Activity, requestCode: Int) {
        this.pickRequestCode = requestCode
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.type = "*/*"
        i.addCategory(Intent.CATEGORY_OPENABLE)
        val mimetypes = arrayOf("image/*", "video/*")
        i.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
        context.startActivityForResult(i, requestCode)
    }

    private fun onAttachmentPicked(data: Uri) {
        data.getLastPathSegmentOnly(context)?.let { name ->
            mAttachmentButton.text = name
            attachment = Attachment(name, data)
        }
    }

    fun setComposingEnabled(enabled: Boolean) {
        mComposeText.isEnabled = enabled
    }

    override fun toString(): String {
        // Get query from the search bar and sanitize it
        var query = mComposeText.text.toString().trim { it <= ' ' }
        query = query.replace("<[^>]*>".toRegex(), "")    // Strip html tags
        query = query.replace("\n", "")            // Replace newline characters
        query = query.replace("\t", "")            // Replace tab characters
        query = query.replace("[ ]+".toRegex(), " ")      // Replace double spaces with single spaces
        return query
    }

    companion object {
        const val RESULT_ACTION_PICK_MATERIAL = 100
        const val RC_WRITE_PERMISSION = 200
    }

}