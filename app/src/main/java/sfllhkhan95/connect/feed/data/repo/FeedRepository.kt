package sfllhkhan95.connect.feed.data.repo

import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.github.saifkhichi96.android.db.LocalDatabase
import kotlinx.coroutines.tasks.await
import sfllhkhan95.connect.core.data.Repository
import sfllhkhan95.connect.feed.data.source.FeedDataSource
import sfllhkhan95.connect.feed.model.Attachment
import sfllhkhan95.connect.feed.model.Post
import sfllhkhan95.connect.storage.FileManager
import javax.inject.Inject

class FeedRepository @Inject constructor(
    dataSource: FeedDataSource,
    cache: LocalDatabase
) : Repository<Post>(dataSource, cache) {

    suspend fun add(
        context: Context,
        content: String,
        author: String,
        attachment: Attachment?
    ): Post {
        val post = Post(
            id = dataSource.db.createEmptyChild(dataSource.root),
            content = content,
            postedBy = author,
        )

        if (attachment != null) {
            val fm = FileManager.newInstance(context, dataSource.root)
            val filename = "${post.id}.${attachment.type}"
            fm.upload(filename, attachment.data).await()

            val storage = Firebase.storage.getReference(dataSource.root)
            post.attachment = storage.child(filename)
                .downloadUrl
                .await()
                .toString()
        }

        add(post.id, post)
        return post
    }

}