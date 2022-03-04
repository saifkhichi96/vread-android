package sfllhkhan95.connect.feed.data.source

import io.github.saifkhichi96.android.db.RemoteDatabase
import sfllhkhan95.connect.core.data.DataSource
import sfllhkhan95.connect.feed.model.Post
import javax.inject.Inject

class FeedDataSource @Inject constructor(db: RemoteDatabase) : DataSource<Post>(db, Post::class.java) {

    override val root = "feed/posts/"

}