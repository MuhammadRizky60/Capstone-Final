package com.example.test.data.pref

import retrofit2.http.Url

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false,
    val name: String,
    val uid: String
)
