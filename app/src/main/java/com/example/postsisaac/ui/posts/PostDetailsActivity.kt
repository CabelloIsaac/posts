package com.example.postsisaac.ui.posts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.postsisaac.R
import com.example.postsisaac.models.Post
import com.example.postsisaac.models.User
import com.example.postsisaac.ui.UserDetailActivity
import com.example.postsisaac.utils.Constants

class PostDetailsActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvBody: TextView
    private lateinit var tvUserName: TextView
    private lateinit var cardViewUser: CardView

    private var id: Int? = null
    private var userId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        title = "Details"

        /* Connect variables to UI elements. */
        tvTitle = findViewById(R.id.tvTItle)
        tvBody = findViewById(R.id.tvBody)
        tvUserName = findViewById(R.id.tvUserName)
        cardViewUser = findViewById(R.id.cardViewUser)

        getIntentExtras()

        getPostData()

        cardViewUser.setOnClickListener {
            if (userId != null)
                goToUserDetailsActivity()
        }

    }

    private fun getIntentExtras() {
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            id = bundle.getInt("id")
            if (id == null) finish()
        } else {
            finish()
        }
    }

    private fun goToUserDetailsActivity() {
        val intent = Intent(this, UserDetailActivity()::class.java)
        intent.putExtra(Constants.ID, userId)
        startActivity(intent)
    }

    private fun getPostData() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://jsonplaceholder.typicode.com/posts/${id}"

        // Request a string response from the provided URL.
        val jsonArrayRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->

                val post = Post(response, true)
                showPostDataOnUIElements(post)

                userId = post.userId
                getUserData(post.userId)

            },
            { error ->
                Log.e("Error", error.toString())
            }
        )

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest)
    }

    private fun getUserData(userId: Int) {
        val queue = Volley.newRequestQueue(this)
        val url = "https://jsonplaceholder.typicode.com/users/${userId}"

        // Request a string response from the provided URL.
        val jsonArrayRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->

                val user = User(response)
                showUserDataOnUIElements(user)

            },
            { error ->
                Log.e("Error", error.toString())
            }
        )

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest)
    }

    private fun showPostDataOnUIElements(post: Post) {
        tvTitle.text = post.title
        tvBody.text = post.body
    }

    private fun showUserDataOnUIElements(user: User) {
        tvUserName.text = user.name
    }

}