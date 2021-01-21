package com.example.postsisaac.ui.posts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.postsisaac.R
import com.example.postsisaac.adapters.PostsAdapter
import com.example.postsisaac.models.Post
import org.json.JSONObject

class PostsFragment : Fragment() {

    private lateinit var postViewModel: PostsViewModel

    //    private lateinit var btGet: Button
//    private lateinit var textView: TextView
    private var posts: ArrayList<Post> = ArrayList()
    private lateinit var postsAdapter: PostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        postViewModel =
            ViewModelProvider(this).get(PostsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_posts, container, false)
        postsAdapter = PostsAdapter(posts) { flower -> adapterOnClick(flower) }

        val recyclerView: RecyclerView = root.findViewById(R.id.recycler_view)
        recyclerView.adapter = postsAdapter

//        textView = root.findViewById(R.id.text_home)
//        btGet = root.findViewById(R.id.btGet)
//        postViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

//        btGet.setOnClickListener {
//            getPosts()
//        }

        getPosts()

        return root
    }

    private fun getPosts() {

        posts.clear()

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)
        val url = "https://jsonplaceholder.typicode.com/posts"

        // Request a string response from the provided URL.
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->

                for (i in 0 until response.length()) {
                    val post = createPostObjectFrom(response.getJSONObject(i))
                    addPostToList(post)
                }

                postsAdapter.notifyDataSetChanged()

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
        intent.putExtra("id", post.id)
        startActivity(intent)
    }


    private fun createPostObjectFrom(json: JSONObject): Post {
        return Post(
            json.getInt("userId"),
            json.getInt("id"),
            json.getString("title"),
            json.getString("body")
        )
    }

    private fun addPostToList(post: Post) {
        posts.add(post)
    }

}