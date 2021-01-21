package com.example.postsisaac.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.postsisaac.R
import com.example.postsisaac.models.User
import com.example.postsisaac.utils.Constants
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_user_detail.*
import kotlinx.android.synthetic.main.content_scrolling.*

class UserDetailActivity : AppCompatActivity() {

    private var id: Int? = null
    private lateinit var toolbarLayout: CollapsingToolbarLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        setSupportActionBar(findViewById(R.id.toolbar))
        toolbarLayout = findViewById(R.id.toolbar_layout)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        getIntentExtras()

        getUserData()

    }

    private fun getIntentExtras() {
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            id = bundle.getInt(Constants.ID)
            if (id == null) finish()
        } else {
            finish()
        }
    }

    private fun getUserData() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://jsonplaceholder.typicode.com/users/${id}"

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

    @SuppressLint("SetTextI18n")
    private fun showUserDataOnUIElements(user: User) {
        toolbarLayout.title = "${user.name} (${user.username})"
        tvEmail.text = user.email
        tvPhone.text = user.phone
        tvWebsite.text = user.website
        tvAddress.text =
            "${user.address.street}, ${user.address.suite}, ${user.address.city}, ${user.address.zipcode}"
        tvCompanyName.text = user.company.name
        tvCompanyCatchPhrase.text = user.company.catchPhrase
    }

}