package com.example.postsisaac.models

import org.json.JSONObject

data class Post(var userId: Int, var id: Int, var title: String, var body: String) {

    constructor (json: JSONObject) : this(
        json.getInt("userId"),
        json.getInt("id"),
        json.getString("title"),
        json.getString("body")
    )

}