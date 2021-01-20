package com.example.postsisaac.ui.posts

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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.postsisaac.R

class PostsFragment : Fragment() {

    private lateinit var postViewModel: PostsViewModel
    private lateinit var btGet: Button
    private lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        postViewModel =
            ViewModelProvider(this).get(PostsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_posts, container, false)
        textView = root.findViewById(R.id.text_home)
        btGet = root.findViewById(R.id.btGet)
        postViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })


        btGet.setOnClickListener {
            getData()
        }





        return root
    }

    fun getData() {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)
        val url = "https://www.google.com"

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                textView.text = "Response is: ${response.substring(0, 500)}"
            },
            Response.ErrorListener {
                Log.e("Error", it.toString())
                textView.text = "That didn't work!" +it.toString()
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

}