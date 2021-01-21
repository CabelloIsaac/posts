package com.example.postsisaac.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.postsisaac.R
import com.example.postsisaac.models.Post

class PostsAdapter(private val dataSet: ArrayList<Post>, private val onClick: (Post) -> Unit) :
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(itemView: View, val onClick: (Post) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvBody: TextView = itemView.findViewById(R.id.tvBody)
        private var currentPost: Post? = null

        init {
            // Define click listener for the ViewHolder's View.
            val cardView: CardView = itemView.findViewById(R.id.cardView)

            cardView.setOnClickListener {
                currentPost?.let {
                    onClick(it)
                }
            }

        }

        /* Bind flower name and image. */
        fun bind(post: Post) {
            currentPost = post
        }

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.post_item, viewGroup, false)

        return ViewHolder(view, onClick)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val post = dataSet[position]

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.tvTitle.text = post.title
        viewHolder.tvBody.text = post.body

        viewHolder.bind(post)

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}