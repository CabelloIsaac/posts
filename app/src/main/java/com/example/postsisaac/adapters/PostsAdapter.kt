package com.example.postsisaac.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.postsisaac.R
import com.example.postsisaac.models.Post

class PostsAdapter(
    private val dataSet: ArrayList<Post>,
    private val onClick: (Post) -> Unit,
    private val onFavoriteClick: (Post) -> Unit
) :
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    class ViewHolder(
        itemView: View,
        val onClick: (Post) -> Unit,
        val onFavoriteClick: (Post) -> Unit
    ) :
        RecyclerView.ViewHolder(itemView) {

        private var currentPost: Post? = null

        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvBody: TextView = itemView.findViewById(R.id.tvBody)
        val ivReaded: ImageView = itemView.findViewById(R.id.ivReaded)
        val ivFavorite: ImageView = itemView.findViewById(R.id.ivFavorite)
        private val cardView: CardView = itemView.findViewById(R.id.cardView)

        init {

            cardView.setOnClickListener {
                currentPost?.readed = true
                currentPost?.let {
                    onClick(it)
                }
            }

            ivFavorite.setOnClickListener {
                currentPost?.let {
                    onFavoriteClick(it)
                }
            }

        }

        fun bind(post: Post) {
            currentPost = post
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.post_item, viewGroup, false)
        return ViewHolder(view, onClick, onFavoriteClick)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val post = dataSet[position]

        viewHolder.tvTitle.text = post.title
        viewHolder.tvBody.text = post.body

        if (post.readed)
            viewHolder.ivReaded.visibility = View.GONE
        else
            viewHolder.ivReaded.visibility = View.VISIBLE

        if (post.isFavorite == 1)
            viewHolder.ivFavorite.setImageResource(R.drawable.ic_baseline_star_24)
        else
            viewHolder.ivFavorite.setImageResource(R.drawable.ic_baseline_star_border_24)

        viewHolder.bind(post)

    }

    override fun getItemCount() = dataSet.size

}