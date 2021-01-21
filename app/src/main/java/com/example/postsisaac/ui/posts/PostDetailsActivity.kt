package com.example.postsisaac.ui.posts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.postsisaac.R
import com.example.postsisaac.models.Post
import com.example.postsisaac.models.User
import com.example.postsisaac.ui.UserDetailActivity
import com.example.postsisaac.utils.Constants
import com.example.postsisaac.utils.PostsDb
import kotlinx.coroutines.launch


class PostDetailsActivity : AppCompatActivity() {

    private var menu: Menu? = null
    private var menuFavorite: MenuItem? = null

    private lateinit var db: PostsDb
    private lateinit var tvTitle: TextView
    private lateinit var tvBody: TextView
    private lateinit var tvUserName: TextView
    private lateinit var cardViewUser: CardView

    private var id: Int? = null
    private var userId: Int? = null
    private var post: Post? = null

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

        initDB()
        getIntentExtras()

        getPostData()

        cardViewUser.setOnClickListener {
            if (userId != null)
                goToUserDetailsActivity()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        setResult(0)
        super.onBackPressed()
    }

    private fun initDB() {
        db = Room.databaseBuilder(
            applicationContext!!,
            PostsDb::class.java, "posts"
        ).build()
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
        lifecycleScope.launch {

            Log.d("PostDetailActivity", "Loading Post Data")

            post = db.postDao().getById(id!!)
            showPostDataOnUIElements()
            Log.d("PostDetailActivity", "${post!!.isFavorite}")

            userId = post!!.userId
            getUserData(post!!.userId)

        }
    }

//    private fun getPostData() {
//        val queue = Volley.newRequestQueue(this)
//        val url = "https://jsonplaceholder.typicode.com/posts/${id}"
//
//        // Request a string response from the provided URL.
//        val jsonArrayRequest = JsonObjectRequest(
//            Request.Method.GET, url, null,
//            { response ->
//
//                val post = Post(response, true)
//                showPostDataOnUIElements(post)
//
//                userId = post.userId
//                getUserData(post.userId)
//
//            },
//            { error ->
//                Log.e("Error", error.toString())
//            }
//        )
//
//        // Add the request to the RequestQueue.
//        queue.add(jsonArrayRequest)
//    }

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

    private fun showPostDataOnUIElements() {

        Log.d("PostDetailActivity", "showPostDataOnUIElements")

        tvTitle.text = post!!.title
        tvBody.text = post!!.body

        if (menuFavorite != null)
            showFavoriteIcon()

    }

    private fun showFavoriteIcon() {

        Log.d("PostDetailActivity", "showFavoriteIcon ${post != null && post!!.isFavorite == 1}")

        if (post != null && post!!.isFavorite == 1) {
            menuFavorite!!.setIcon(R.drawable.ic_baseline_favorite_24)
//            Log.d(TAG, "onCreateOptionsMenu: favoriteItemIcon is checked")
        } else {
            menuFavorite!!.setIcon(R.drawable.ic_baseline_business_24)
        }
    }

    private fun showUserDataOnUIElements(user: User) {
        tvUserName.text = user.name
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_post_detail, menu)
        menuFavorite = menu.findItem(R.id.action_favorite)

        showFavoriteIcon()

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_favorite -> {

                if (post!!.isFavorite == 1) {
                    post!!.isFavorite = 0
                } else {
                    post!!.isFavorite = 1
                }

                updatePostFavoriteStatus()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updatePostFavoriteStatus() {
        lifecycleScope.launch {

            Log.d("PostDetailActivity", "updatePostFavoriteStatus")
            db.postDao().update(post!!)
            getPostData()
        }
    }

}