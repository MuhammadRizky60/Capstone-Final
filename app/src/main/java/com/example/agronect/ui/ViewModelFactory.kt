package com.example.agronect.ui

import EditPasswordViewModel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agronect.data.UserRepository
import com.example.agronect.di.Injection
import com.example.agronect.ui.addList.AddListViewModel
import com.example.agronect.ui.detail.DetailViewModel
import com.example.agronect.ui.editProfile.EditViewModel
import com.example.agronect.ui.login.LoginViewModel
import com.example.agronect.ui.main.MainViewModel
import com.example.agronect.ui.sharingpage.SharingPageViewModel

class ViewModelFactory(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddListViewModel::class.java) -> {
                AddListViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SharingPageViewModel::class.java) -> {
                SharingPageViewModel(repository) as T
            }
            modelClass.isAssignableFrom(EditViewModel::class.java) -> {
                EditViewModel(repository) as T
            }
            modelClass.isAssignableFrom(EditPasswordViewModel::class.java) -> {
                EditPasswordViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}