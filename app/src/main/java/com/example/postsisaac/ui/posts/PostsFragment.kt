package com.example.postsisaac.ui.posts

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.postsisaac.R
import com.example.postsisaac.adapters.PostsAdapter
import com.example.postsisaac.models.Post
import com.example.postsisaac.utils.Constants
import com.example.postsisaac.utils.PostsDb
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class PostsFragment : Fragment() {

    private var posts: ArrayList<Post> = ArrayList()
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var db: PostsDb
    private lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_posts, container, false)

        recyclerView = root.findViewById(R.id.recycler_view)
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout)

        postsAdapter =
            PostsAdapter(
                posts,
                { post -> adapterOnClick(post) },
                { post -> adapterOnFavoriteClick(post) })

        swipeRefreshLayout.setOnRefreshListener {
            fetchPostsFromApi()
        }

        recyclerView.adapter = postsAdapter

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)

        initDB()

        return root
    }

    private val itemTouchHelperCallback =
        object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                lifecycleScope.launch {
                    db.postDao().delete(posts[viewHolder.adapterPosition])
                    Snackbar.make(view!!, getString(R.string.post_deleted), Snackbar.LENGTH_SHORT)
                        .show()
                    loadPostsFromDb()
                }
            }
        }

    override fun onResume() {
        loadPostsFromDb()
        super.onResume()
    }

    private fun initDB() {
        if (activity != null) {
            db = Room.databaseBuilder(
                activity?.applicationContext!!,
                PostsDb::class.java, Constants.POSTS
            ).build()
        }
    }

    private fun loadPostsFromDb(canFetchFromServer: Boolean = true) {
        lifecycleScope.launch {
            posts.clear()
            posts.addAll(db.postDao().getAll())
            if (posts.isEmpty() && canFetchFromServer)
                fetchPostsFromApi()

            postsAdapter.notifyDataSetChanged()

            if (swipeRefreshLayout.isRefreshing)
                swipeRefreshLayout.isRefreshing = false

        }
    }

    private fun fetchPostsFromApi() {

        if (!swipeRefreshLayout.isRefreshing)
            swipeRefreshLayout.isRefreshing = true

        val remotePosts: ArrayList<Post> = ArrayList()

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
                }

                addPostsToDb(remotePosts)

            },
            { error ->
                Log.e("Error", error.toString())
            }
        )

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest)
    }

    private fun adapterOnClick(post: Post) {
        val intent = Intent(context, PostDetailsActivity()::class.java)
        intent.putExtra(Constants.ID, post.id)
        startActivity(intent)
        postsAdapter.notifyDataSetChanged()
        updatePostStatus(post)
    }

    private fun adapterOnFavoriteClick(post: Post) {
        post.isFavorite = if (post.isFavorite == 1) 0 else 1
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
            db.postDao().deleteAll()
            db.postDao().insert(remotePosts)
            loadPostsFromDb()
        }
    }

    private fun showDeleteAllDialog() {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.remove_all))
            .setMessage(getString(R.string.remove_all_message))
            .setPositiveButton(
                android.R.string.ok
            ) { _, _ ->
                deleteAllPosts()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun deleteAllPosts() {
        lifecycleScope.launch {
            db.postDao().deleteAll()
            loadPostsFromDb(canFetchFromServer = false)
            Snackbar.make(recyclerView, getString(R.string.all_posts_deleted), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.refresh_posts)) {
                    fetchPostsFromApi()
                }
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_posts, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_refresh)
            fetchPostsFromApi()

        if (id == R.id.action_remove_all)
            showDeleteAllDialog()

        return super.onOptionsItemSelected(item)
    }

}