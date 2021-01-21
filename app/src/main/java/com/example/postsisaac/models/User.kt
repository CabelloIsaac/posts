package com.example.postsisaac.models

import com.example.postsisaac.utils.Constants
import org.json.JSONObject

data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val address: Address,
    val phone: String,
    val website: String,
    val company: Company
) {
    constructor (json: JSONObject) : this(
        json.getInt(Constants.ID),
        json.getString(Constants.NAME),
        json.getString(Constants.USERNAME),
        json.getString(Constants.EMAIL),
        Address(json.getJSONObject(Constants.ADDRESS)),
        json.getString(Constants.PHONE),
        json.getString(Constants.WEBSITE),
        Company(json.getJSONObject(Constants.COMPANY))
    )
}

data class Address(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String,
    val geo: Geo
) {
    constructor (json: JSONObject) : this(
        json.getString(Constants.STREET),
        json.getString(Constants.SUITE),
        json.getString(Constants.CITY),
        json.getString(Constants.ZIPCODE),
        Geo(json.getJSONObject(Constants.GEO))
    )
}

data class Geo(
    val lat: String,
    val lng: String
) {
    constructor (json: JSONObject) : this(
        json.getString(Constants.LAT),
        json.getString(Constants.LNG),
    )
}

data class Company(
    val name: String,
    val catchPhrase: String,
    val bs: String
) {
    constructor (json: JSONObject) : this(
        json.getString(Constants.NAME),
        json.getString(Constants.CATCH_PHRASE),
        json.getString(Constants.BS),
    )
}
