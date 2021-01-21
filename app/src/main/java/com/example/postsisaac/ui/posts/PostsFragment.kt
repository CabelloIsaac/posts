package com.example.postsisaac.ui.posts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.postsisaac.R
import com.example.postsisaac.adapters.PostsAdapter
import com.example.postsisaac.models.Post
import com.example.postsisaac.utils.Constants
import com.example.postsisaac.utils.PostsDb
import kotlinx.coroutines.launch

class PostsFragment : Fragment() {

    private lateinit var postViewModel: PostsViewModel
    private var posts: ArrayList<Post> = ArrayList()
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var db: PostsDb

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        postViewModel =
            ViewModelProvider(this).get(PostsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_posts, container, false)
        postsAdapter =
            PostsAdapter(
                posts,
                { post -> adapterOnClick(post) },
                { post -> adapterOnFavoriteClick(post) })

        val recyclerView: RecyclerView = root.findViewById(R.id.recycler_view)
        recyclerView.adapter = postsAdapter

        initDB()

        loadPostsFromDb()

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loadPostsFromDb()
    }


    private fun initDB() {
        if (activity != null) {
            db = Room.databaseBuilder(
                activity?.applicationContext!!,
                PostsDb::class.java, "posts"
            ).build()
        }
    }

    private fun loadPostsFromDb() {
        lifecycleScope.launch {
            posts.clear()
            posts.addAll(db.postDao().getAll())

            Log.d("PostsFragment", "There is ${posts.size} posts in Room")

            if (posts.isEmpty())
                fetchPostsFromApi()

            postsAdapter.notifyDataSetChanged()

        }
    }

    private fun fetchPostsFromApi() {

        var remotePosts: ArrayList<Post> = ArrayList()

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)
        val url = "https://jsonplaceholder.typicode.com/posts"

        // Request a string response from the provided URL.
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->

                for (i in 0 until response.length()) {
                    val belongsToFirst20 = i < 20
                    val post = Post(response.getJSONObject(i), 0, !belongsToFirst20)
                    remotePosts.add(post)
                    addPostsToDb(remotePosts)
                    postsAdapter.notifyDataSetChanged()
                }

                Log.d("PostsFragment", "There is ${remotePosts.size} posts in Server")

            },
            { error ->
                Log.e("Error", error.toString())
            }
        )

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest)
    }

    /* Opens FlowerDetailActivity when RecyclerView item is clicked. */
    private fun adapterOnClick(post: Post) {
        val intent = Intent(context, PostDetailsActivity()::class.java)
        intent.putExtra(Constants.ID, post.id)
        startActivity(intent)
        postsAdapter.notifyDataSetChanged()
        updatePostStatus(post)
    }

    /* Opens FlowerDetailActivity when RecyclerView item is clicked. */
    private fun adapterOnFavoriteClick(post: Post) {
        if (post.isFavorite == 1)
            post.isFavorite = 0
        else
            post.isFavorite = 1

        println("eqweqweqwe")

        updatePostStatus(post)
    }

    private fun updatePostStatus(post: Post) {
        lifecycleScope.launch {
            db.postDao().update(post)
            loadPostsFromDb()
        }
    }

    private fun addPostsToDb(remotePosts: ArrayList<Post>) {
        lifecycleScope.launch {
//
            // Limpiamos la base de datos
            db.postDao().deleteAll()
            db.postDao().insert(remotePosts)
            loadPostsFromDb()

        }
    }

}