package dev.aspirasoft.vread.feed.data.source

import dev.aspirasoft.vread.core.data.DataSource
import dev.aspirasoft.vread.feed.model.Post
import io.github.saifkhichi96.android.db.RemoteDatabase
import javax.inject.Inject

class FeedDataSource @Inject constructor(db: RemoteDatabase) : DataSource<Post>(db, Post::class.java) {

    override val root = "feed/posts/"

}