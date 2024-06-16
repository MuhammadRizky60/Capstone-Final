package com.example.agronect.data.pref

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false,
    val name: String,
    val uid: String,
    val imgUrl: String
)
