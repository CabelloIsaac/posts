package com.example.postsisaac.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.postsisaac.R
import com.example.postsisaac.databinding.ActivityUserDetailBinding
import com.example.postsisaac.models.User
import com.example.postsisaac.utils.Constants
import com.google.android.material.appbar.CollapsingToolbarLayout


class UserDetailActivity : AppCompatActivity() {

    private var id: Int? = null
    private lateinit var toolbarLayout: CollapsingToolbarLayout
    private lateinit var binding: ActivityUserDetailBinding
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(findViewById(R.id.toolbar))
        toolbarLayout = findViewById(R.id.toolbar_layout)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        getIntentExtras()

        getUserData()

        binding.content.tvOpenOnGoogleMaps.setOnClickListener {
            openUserLocationOnGoogleMaps()
        }

        binding.content.tvWebsite.setOnClickListener {
            openUserWebsite()
        }

        binding.content.tvPhone.setOnClickListener {
            openUserPhone()
        }
    }

    private fun openUserLocationOnGoogleMaps() {
        if (user != null) {
            val uri: Uri =
                Uri.parse("https://www.google.com/maps/search/?api=1&query=${user!!.address.geo.lat},${user!!.address.geo.lng}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    private fun openUserWebsite() {
        if (user != null) {
            val uri: Uri =
                Uri.parse("https://${user!!.website}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    private fun openUserPhone() {
        if (user != null) {
            val uri: Uri =
                Uri.parse("tel:${user!!.phone}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
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
                this.user = user
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

        binding.content.tvEmail.text = user.email
        binding.content.tvPhone.text = user.phone
        binding.content.tvWebsite.text = user.website
        binding.content.tvAddress.text =
            "${user.address.street}, ${user.address.suite}, ${user.address.city}, ${user.address.zipcode}"
        binding.content.tvCompanyName.text = user.company.name
        binding.content.tvCompanyCatchPhrase.text = user.company.catchPhrase
    }

}