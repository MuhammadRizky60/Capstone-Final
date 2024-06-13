package com.example.test.ui.sharingpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.test.data.UserRepository
import com.example.test.data.pref.UserModel
import com.example.test.data.response.DataGetAllItem
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//class SharingPageViewModel (private val repository: UserRepository) : ViewModel() {
//    private val _story = MutableLiveData<List<ListStoryItem>>()
//    val story: LiveData<List<ListStoryItem>> = _story
//
//    private val _isLoading = MutableLiveData<Boolean>()
//    val isLoading: LiveData<Boolean> = _isLoading
//
//    fun getSession(): LiveData<UserModel> {
//        return repository.getSession().asLiveData()
//    }
//
//    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> =
//        repository.getStories(token).cachedIn(viewModelScope)
//}

class SharingPageViewModel(private val repository: UserRepository) : ViewModel() {

    private val _story = MutableLiveData<List<DataGetAllItem>>()
    val story: LiveData<List<DataGetAllItem>> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

//    fun getAllSharing(token: String) {
//        _isLoading.value = true
//        repository.getAllSharing(token).enqueue(object : Callback<GetAllSharingResponse> {
//            override fun onResponse(
//                call: Call<GetAllSharingResponse>,
//                response: Response<GetAllSharingResponse>
//            ) {
//                _isLoading.value = false
//                if (response.isSuccessful) {
//                    _story.value = response.body()?.dataGetAll?.flatten()?.filterNotNull()
//                } else {
//                    // Handle error case
//                }
//            }
//
//            override fun onFailure(call: Call<GetAllSharingResponse>, t: Throwable) {
//                _isLoading.value = false
//                // Handle failure case
//            }
//        })
//    }

    fun getStory(token: String): LiveData<PagingData<DataGetAllItem>> =
        repository.getAllSharing(token).cachedIn(viewModelScope)
}
