package com.example.agronect.di

import android.content.Context
import com.example.agronect.data.UserRepository
import com.example.agronect.data.pref.UserPreference
import com.example.agronect.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}