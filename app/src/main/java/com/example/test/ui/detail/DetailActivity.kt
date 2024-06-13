package com.example.test.ui.detail

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.test.databinding.ActivityDetailBinding
import com.example.test.ui.ViewModelFactory
import com.example.test.ui.welcome.WelcomeActivity

class DetailActivity : AppCompatActivity() {
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityDetailBinding
    private var token = "token"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        val id = intent.getStringExtra(ID)
        if (id != null) {
            val detailViewModel = obtainViewModel(this@DetailActivity)

            viewModel.getSession().observe(this) { user ->
                if (!user.isLogin) {
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                } else {
                    token = user.token
                    Log.d(ContentValues.TAG, "token detail: $token")
                    Log.d(ContentValues.TAG, "id: $id")
                    detailViewModel.getDetailSharing(token, id)
                    detailViewModel.detail.observe(this) { storyList ->
                        if (storyList != null) {
                            Log.d(ContentValues.TAG, "Story: $storyList")
                            binding.apply {
                                tvUsername.text = storyList.name
                                tvDesc.text = storyList.content
                                Glide.with(binding.root.context)
                                    .load(storyList.imgUrl ?: "")
                                    .into(binding.ivPhoto)
                            }
                        } else {
                            Log.e(ContentValues.TAG, "Error: storyList is null")
                        }
                    }

                    detailViewModel.isLoading.observe(this) {
                        showLoading(it)
                    }
                }
            }
        } else {
            Log.e(ContentValues.TAG, "onCreate: ID is null")
            // Tampilkan pesan kesalahan atau ambil tindakan yang sesuai
        }
    }

    private fun showLoading(state: Boolean) {
        binding.ProgressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: AppCompatActivity): DetailViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[DetailViewModel::class.java]
    }

    companion object {
        const val ID = "id"
    }
}