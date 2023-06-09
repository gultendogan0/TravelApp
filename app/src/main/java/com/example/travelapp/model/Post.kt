package com.example.travelapp.model

import com.google.firebase.Timestamp

data class Post(
    val title:String,
    val city: String,
    val description:String,
    val downloadUrl: String,
    val email: String) {
}