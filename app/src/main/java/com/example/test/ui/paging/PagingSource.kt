package com.example.test.ui.paging

import android.content.ContentValues
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.test.data.response.DataGetAllItemItem
import com.example.test.data.response.GetAllSharingResponse
import com.example.test.data.retrofit.ApiConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class PagingSource(private val token: String) : PagingSource<Int, DataGetAllItemItem>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, DataGetAllItemItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
    override suspend fun load(params: androidx.paging.PagingSource.LoadParams<Int>): androidx.paging.PagingSource.LoadResult<Int, DataGetAllItemItem> {
        try {
            val position = params.key ?: 1
            return suspendCancellableCoroutine { continuation ->
                Log.d(ContentValues.TAG, "tokenPagingSource: $token")
                val client = ApiConfig.getApiService().getAllSharing("Bearer $token",position, params.loadSize)
                client.enqueue(object : Callback<GetAllSharingResponse> {
                    override fun onResponse(
                        call: Call<GetAllSharingResponse>,
                        response: Response<GetAllSharingResponse>
                    ) {
                        if (response.isSuccessful) {
                            val storyList: List<DataGetAllItemItem> =
                                (response.body()?.dataGetAll ?: emptyList()) as List<DataGetAllItemItem>
                            Log.d(ContentValues.TAG, "pagingSource: $storyList")

                            val page = LoadResult.Page(
                                data = storyList,
                                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                                nextKey = if (storyList.isEmpty()) null else position + 1
                            )
                            continuation.resume(page)

                        } else {
                            Log.e(ContentValues.TAG, "onFailure1: ${response.message()}")
                            continuation.resumeWithException(Exception("Failed to load data"))
                        }
                    }

                    override fun onFailure(call: Call<GetAllSharingResponse>, t: Throwable) {
                        Log.e(ContentValues.TAG, "onFailure2: ${t.message.toString()}")
                        continuation.resumeWithException(t)
                    }
                })
            }
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }

    }
}