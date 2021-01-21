package com.example.postsisaac.utils

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.postsisaac.daos.PostDao
import com.example.postsisaac.models.Post

@Database(
    entities = [Post::class],
    version = 1
)
abstract class PostsDb : RoomDatabase() {

    abstract fun postDao(): PostDao

}