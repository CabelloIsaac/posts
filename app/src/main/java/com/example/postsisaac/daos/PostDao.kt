package com.example.postsisaac.daos

import android.util.Log
import androidx.room.*
import com.example.postsisaac.models.Post

@Dao
interface PostDao {

    @Query("SELECT * FROM Post")
    suspend fun getAll(): List<Post>

    @Query("SELECT * FROM Post WHERE isFavorite = 1")
    suspend fun getFavorites(): List<Post>

    @Query("SELECT * FROM Post WHERE id = :id")
    suspend fun getById(id: Int): Post

    @Update
    suspend fun update(post: Post)

    @Insert
    suspend fun insert(posts: List<Post>)

    @Delete
    suspend fun delete(post: Post)

    @Query("DELETE FROM Post")
     suspend fun deleteAll()

}