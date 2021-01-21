package com.example.postsisaac.models

import org.json.JSONObject

data class Post(
    var userId: Int,
    var id: Int,
    var title: String,
    var body: String,
    var readed: Boolean
) {

    constructor (json: JSONObject, readed: Boolean) : this(
        json.getInt("userId"),
        json.getInt("id"),
        json.getString("title"),
        json.getString("body"),
        readed
    )

}