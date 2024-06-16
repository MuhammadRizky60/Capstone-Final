package com.example.agronect.ui.editProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.agronect.data.UserRepository
import com.example.agronect.data.pref.UserModel
import kotlinx.coroutines.launch

class EditViewModel(private val repository: UserRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}