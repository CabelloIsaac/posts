package com.example.postsisaac.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.postsisaac.utils.Constants
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
        json.getInt(Constants.ID),
        json.getInt(Constants.USER_ID),
        json.getString(Constants.TITLE),
        json.getString(Constants.BODY),
        isFavorite,
        readed
    )

}