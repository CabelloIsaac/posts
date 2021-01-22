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
    private var lat: String = "0"
    private var lng: String = "0"

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

    }

    private fun openUserLocationOnGoogleMaps() {
        val uri: Uri =
            Uri.parse("https://www.google.com/maps/search/?api=1&query=${lat},${lng}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
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
                lat = user.address.geo.lat
                lng = user.address.geo.lng
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