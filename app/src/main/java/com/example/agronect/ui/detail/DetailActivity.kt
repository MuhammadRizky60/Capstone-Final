package com.example.agronect.ui.detail

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.agronect.databinding.ActivityDetailBinding
import com.example.agronect.ui.ViewModelFactory
import com.example.agronect.ui.welcome.WelcomeActivity

import java.text.SimpleDateFormat
import java.util.Locale

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
        val date = intent.getStringExtra(DATE)
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

                            // Format ulang tanggal
                            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                            val outputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                            val formattedDate = try {
                                val parsedDate = inputDateFormat.parse(date)
                                outputDateFormat.format(parsedDate)
                            } catch (e: Exception) {
                                date
                            }

                            binding.apply {
                                tvUsername.text = storyList.name
                                tvDate.text = formattedDate
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
        const val DATE = "date"
    }
}
