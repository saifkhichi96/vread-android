package dev.aspirasoft.vread.books.model

import android.graphics.Bitmap
import android.text.Html
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.api.services.books.v1.model.Volume
import com.google.api.services.books.v1.model.Volume.VolumeInfo
import com.saifkhichi.storage.CloudFileStorage
import dev.aspirasoft.vread.R
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.Serializable
import java.util.*

data class Book(
    var id: String = "",
    var isbn: String = "",
    var title: String = "",
    var sortTitle: String = "",
    var authors: String = "",
    var category: String = "",
    var subCategory: String = "",
    var publishedBy: String = "",
    var publishedOn: Int = 0,
    var pageCount: Int = 0,
    var format: Format = Format.Paperback,
    var lang: String = "",
    var excerpt: String = "",
) : Serializable {

    fun cover(): String = "$id.jpg"

    /**
     * Returns the ISBN-13 version of the book's ISBN, if available.
     */
    fun isbn13() = kotlin.runCatching { ISBN(isbn).toString() }.getOrDefault(isbn)

    /**
     * Fill in missing details from Google Books.
     *
     * @param volume The volume to update with.
     * @param overwrite Whether to overwrite existing values. Defaults to true.
     */
    fun updateWith(volume: Volume, overwrite: Boolean = true) {
        val volumeInfo: VolumeInfo? = volume.volumeInfo

        if (overwrite || isbn.isBlank()) isbn = volumeInfo?.industryIdentifiers?.find { it.type == "ISBN_13" }?.identifier ?: ""
        if (overwrite || title.isBlank()) title = volumeInfo?.title ?: ""
        if (overwrite || authors.isBlank()) authors = volumeInfo?.authors?.joinToString(", ") ?: ""
        if (overwrite || publishedBy.isBlank()) publishedBy = volumeInfo?.publisher ?: ""
        if (overwrite || publishedOn <= 0) publishedOn = Integer.parseInt(volumeInfo?.publishedDate?.split("-")?.first() ?: "0")
        if (overwrite || pageCount <= 0) pageCount = volumeInfo?.pageCount ?: 0
        if (overwrite || lang.isBlank()) lang = Locale(volumeInfo?.language ?: "en").displayLanguage
        if (overwrite || excerpt.isBlank()) excerpt =
            Html.fromHtml(volume.searchInfo?.textSnippet ?: "", Html.FROM_HTML_MODE_LEGACY).toString()
    }

    /**
     * Gets and shows the book cover.
     *
     * Gets the book cover image from [CloudFileStorage] and shows it in an ImageView. If cover
     * does not exist in the cloud storage, the [onCoverError] callback is invoked to try to
     * look for book covers matching the current ISBN elsewhere.
     *
     * @param context The current [LifecycleOwner].
     * @param target The [ImageView] where the book cover will be displayed.
     * @param invalidate If true, image is refreshed from server instead of using cached. Default is false.
     */
    fun getBookCover(context: LifecycleOwner, target: ImageView, invalidate: Boolean = false) {
        val bookStorage = CloudFileStorage(target.context, "library")
        bookStorage.download(this.cover(), invalidate) { result ->
            try {
                onCoverFound(result.getOrNull()!!, target)
            } catch (ex: Exception) {
                onCoverError(context, target)
            }
        }
    }

    /**
     * Saves the book cover.
     *
     * Uploads a new book cover image to the [CloudFileStorage]. Any existing cover image
     * for this book are deleted from remote storage. The local cache is also updated, and
     * the new image is displayed in the UI.
     *
     * @param context The current [LifecycleOwner].
     * @param target The [ImageView] where the book cover will be displayed.
     * @param coverImage The new cover image to save.
     */
    fun setBookCover(context: LifecycleOwner, target: ImageView, coverImage: ByteArray) {
        context.lifecycleScope.launch {
            if (id.isNotBlank()) {
                val bookStorage = CloudFileStorage(target.context, "library")
                val error = bookStorage.upload(this@Book.cover(), coverImage).exceptionOrNull()
                if (error == null) this@Book.getBookCover(context, target, invalidate = true)
            }
        }
    }

    /**
     * Called when the book cover image is successfully downloaded.
     *
     * Shows the located cover image in the give ImageView.
     *
     * @param coverImage A local [File] containing the cover image.
     * @param target The [ImageView] where the book cover will be displayed.
     */
    private fun onCoverFound(coverImage: File, target: ImageView) {
        Glide.with(target)
            .load(coverImage)
            .thumbnail()
            .placeholder(R.drawable.placeholder_book_cover)
            .error(R.drawable.placeholder_book_cover)
            .into(target)
    }

    /**
     * Called when the book cover image cannot be found.
     *
     * @param context The current [LifecycleOwner].
     * @param target The [ImageView] where the book cover will be displayed.
     */
    private fun onCoverError(context: LifecycleOwner, target: ImageView) {
        if (this.isbn13().isNotBlank()) {
            val queue = Volley.newRequestQueue(target.context)
            getCoverUrlFromGoogleBooks(queue) { coverUrl ->
                getCoverImageFromGoogleBooks(queue, coverUrl) { cover ->
                    setBookCover(context, target, cover.toByteArray())
                }
            }
        }
    }

    private fun getCoverUrlFromGoogleBooks(requestQueue: RequestQueue, listener: Response.Listener<String>) {
        val url = "https://www.googleapis.com/books/v1/volumes?q=isbn:${this.isbn13()}"
        val metadataRequest = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            kotlin.runCatching {
                val coverUrl = response
                    .getJSONArray("items")
                    .getJSONObject(0)
                    .getJSONObject("volumeInfo")
                    .getJSONObject("imageLinks")
                    .getString("thumbnail")
                    .replace("http://", "https://")

                listener.onResponse(coverUrl)
            }
        }, {})
        requestQueue.add(metadataRequest)
    }

    private fun getCoverImageFromGoogleBooks(queue: RequestQueue, url: String, listener: Response.Listener<Bitmap>) {
        val imageRequest = ImageRequest(
            url,
            listener::onResponse,
            128,
            256,
            ImageView.ScaleType.CENTER_CROP,
            Bitmap.Config.ARGB_8888
        ) {}

        queue.add(imageRequest)
    }

    companion object {
        fun Bitmap.toByteArray(): ByteArray {
            val stream = ByteArrayOutputStream()
            this.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray: ByteArray = stream.toByteArray()
            this.recycle()
            return byteArray
        }
    }

}