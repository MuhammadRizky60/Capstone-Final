package com.example.test.ui.detail

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.test.data.UserRepository
import com.example.test.data.pref.UserModel
import com.example.test.data.response.DataGetById
import com.example.test.data.response.DetailSharingResponse
import com.example.test.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(private val repository: UserRepository) : ViewModel() {
    private val mdetail = MutableLiveData<DataGetById>()
    val detail: LiveData<DataGetById> = mdetail

    private val misLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = misLoading

    fun getDetailSharing(token: String, id: String) {
        misLoading.value = true
        val client = ApiConfig.getApiService().getDetailSharing("Bearer $token", id)
        client.enqueue(object : Callback<DetailSharingResponse> {
//            override fun onResponse(
//                call: Call<DetailSharingResponse>,
//                response: Response<DetailSharingResponse>
//            ) {
//                misLoading.value = false
//                if (response.isSuccessful) {
//                    response.body()?.dataGetById?.let { dataGetById ->
//                        mdetail.value = dataGetById
//                    } ?: run {
//                        Log.e(ContentValues.TAG, "onResponse: dataGetById is null")
//                    }
//                } else {
//                    Log.e(ContentValues.TAG, "onFailure1: ${response.errorBody()?.string()}")
//                }
//            }

            override fun onResponse(
                call: Call<DetailSharingResponse>,
                response: Response<DetailSharingResponse>
            ) {
                misLoading.value = false
                if (response.isSuccessful) {
                    mdetail.value = response.body()?.dataGetById as DataGetById
                } else {
                    Log.e(ContentValues.TAG, "onFailure1: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailSharingResponse>, t: Throwable) {
                misLoading.value = false
                Log.e(ContentValues.TAG, "onFailure2: ${t.message.toString()}")
            }
        })
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}

