package com.example.test.data.pref

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false,
    val name: String,
    val uid: String
)
