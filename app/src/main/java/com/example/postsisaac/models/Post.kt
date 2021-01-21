package com.example.postsisaac.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject

@Entity
data class Post(
    @PrimaryKey
    var id: Int,
    var userId: Int,
    var title: String,
    var body: String,
    var isFavorite: Int,
    var readed: Boolean
) {

    constructor (json: JSONObject, isFavorite: Int, readed: Boolean) : this(
        json.getInt("id"),
        json.getInt("userId"),
        json.getString("title"),
        json.getString("body"),
        isFavorite,
        readed
    )

}