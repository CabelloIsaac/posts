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

        val root = inflater.inflate(R.layout.fragment_favorites, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.recycler_view)

        favoritesViewModel =
            ViewModelProvider(this).get(FavoritesViewModel::class.java)

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)

        postsAdapter =
            PostsAdapter(
                posts,
                { post -> adapterOnClick(post) },
                { post -> adapterOnFavoriteClick(post) })

        recyclerView.adapter = postsAdapter

        itemTouchHelper.attachToRecyclerView(recyclerView)

        initDB()

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
                    Snackbar.make(view!!, getString(R.string.post_deleted), Snackbar.LENGTH_SHORT)
                        .show()
                    loadPostsFromDb()
                }
            }
        }

    private fun initDB() {
        if (activity != null) {
            db = Room.databaseBuilder(
                activity?.applicationContext!!,
                PostsDb::class.java, Constants.POSTS
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
}