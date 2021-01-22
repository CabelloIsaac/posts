package com.example.postsisaac.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.postsisaac.R
import com.example.postsisaac.adapters.PostsAdapter
import com.example.postsisaac.models.Post
import com.example.postsisaac.ui.posts.PostDetailsActivity
import com.example.postsisaac.utils.Constants
import com.example.postsisaac.utils.PostsDb
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private lateinit var favoritesViewModel: FavoritesViewModel
    private var posts: ArrayList<Post> = ArrayList()
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var db: PostsDb

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        favoritesViewModel =
            ViewModelProvider(this).get(FavoritesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_favorites, container, false)
        postsAdapter =
            PostsAdapter(
                posts,
                { post -> adapterOnClick(post) },
                { post -> adapterOnFavoriteClick(post) })

        val recyclerView: RecyclerView = root.findViewById(R.id.recycler_view)
        recyclerView.adapter = postsAdapter

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        initDB()

        loadPostsFromDb()

        return root
    }

    override fun onResume() {
        loadPostsFromDb()
        super.onResume()
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
                    Snackbar.make(view!!, "Deleted", Snackbar.LENGTH_SHORT).show()
                    loadPostsFromDb()
                }
            }
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
            posts.addAll(db.postDao().getFavorites())

            postsAdapter.notifyDataSetChanged()

        }
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