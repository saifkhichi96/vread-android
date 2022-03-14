package dev.aspirasoft.vread.profile.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.auth.model.User
import dev.aspirasoft.vread.auth.util.AccountManager
import dev.aspirasoft.vread.core.ui.activity.SecureActivity
import dev.aspirasoft.vread.notifications.util.Notifier
import kotlinx.android.synthetic.main.activity_profile_edit.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class EditProfileActivity : SecureActivity() {

    @Inject
    lateinit var repo: AccountManager

    private var saving = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        // Set up action bar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        avatar.openProfileOnClick = false
        editAvatarButton.setOnClickListener { chooseImage(PICK_AVATAR_REQUEST) }
        cover.setOnClickListener { chooseImage(PICK_COVER_REQUEST) }
    }

    override fun onStart() {
        super.onStart()
        updateUI(signedInUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null && data.data != null) {
            try {
                when (requestCode) {
                    PICK_AVATAR_REQUEST -> data.data?.apply { updateAvatar(this) }
                    PICK_COVER_REQUEST -> data.data?.apply { updateCover(this) }
                }
            } catch (ignored: Exception) {

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.action_save -> {
                if (!saving) {
                    saving = true
                    Toast.makeText(this, "Updating details...", Toast.LENGTH_SHORT).show()

                    signedInUser.let { user ->
                        // Update user data
                        user.bio = bio.text.toString()
                        user.phone = phone.text.toString()
                        user.website = website.text.toString()
                        user.country = location.text.toString()

                        lifecycleScope.launch {
                            try {
                                repo.updateUserAccount(user)
                                auth.saveSessionData(user)
                                onBackPressed()
                            } catch (ex: Exception) {
                                makeToast(ex.message ?: ex::class.java.simpleName)
                                saving = false
                            }
                        }
                    }
                    return true
                }
                return false
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun chooseImage(requestCode: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode)
    }

    private fun updateAvatar(uri: Uri) {
        val builder = Notifier.with(this).apply {
            setContentTitle("Avatar Upload")
            setContentText("Upload in progress")
            setSmallIcon(R.drawable.ic_action_upload)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }

        // Issue the initial notification with zero progress
        val maxProgress = 100
        builder.setProgress(maxProgress, 0, false)
        Notifier.notify(this@EditProfileActivity, builder)

        FirebaseStorage.getInstance()
            .getReference("users/${signedInUser.uid}/profile.png")
            .putFile(uri)
            .addOnProgressListener { taskSnapshot ->
                val progress = (maxProgress.toDouble() * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                builder.setProgress(maxProgress, progress.roundToInt(), false)
            }
            .addOnCompleteListener {
                builder.setContentText("Upload complete").setProgress(0, 0, false)
                Notifier.notify(this@EditProfileActivity, builder, true)
            }
            .addOnSuccessListener {
                avatar.showUser(signedInUser.uid, true)
            }
    }

    private fun updateCover(uri: Uri) {
        val builder = Notifier.with(this).apply {
            setContentTitle("Cover Upload")
            setContentText("Upload in progress")
            setSmallIcon(R.drawable.ic_action_upload)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }

        val maxProgress = 100
        FirebaseStorage.getInstance()
            .getReference("users/${signedInUser.uid}/cover.jpg")
            .putFile(uri)
            .addOnProgressListener { taskSnapshot ->
                val progress = (maxProgress.toDouble() * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                builder.setProgress(maxProgress, progress.roundToInt(), false)
            }
            .addOnCompleteListener {
                builder.setContentText("Upload complete").setProgress(0, 0, false)
                Notifier.notify(this@EditProfileActivity, builder, true)
            }
            .addOnSuccessListener {
                lifecycleScope.launch { cover.showUser(signedInUser.uid, true) }
            }
    }

    private fun updateUI(user: User) {
        avatar.showUser(user.uid) // Show profile picture
        cover.showUser(user.uid)  // Show cover photo

        // Show other details
        name.text = "${user.firstName} ${user.lastName}"
        username.text = "@${user.username}"
        bio.setText(user.bio)
        phone.setText(user.phone)
        email.setText(user.email)
        website.setText(user.website)
        location.setText(user.country)
    }

    companion object {
        const val PICK_AVATAR_REQUEST = 100
        const val PICK_COVER_REQUEST = 200
    }

}