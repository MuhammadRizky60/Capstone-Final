package com.example.agronect.ui.addPlant

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.agronect.R
import com.example.agronect.data.response.AddSharingResponse
import com.example.agronect.data.retrofit.ApiConfig
import com.example.agronect.data.util.getImageUri
import com.example.agronect.data.util.reduceFileImage
import com.example.agronect.data.util.uriToFile
import com.example.agronect.databinding.AddPlantBinding
import com.example.agronect.ui.ViewModelFactory
import com.example.agronect.ui.addList.AddListViewModel
import com.example.agronect.ui.main.MainActivity
import com.example.agronect.ui.welcome.WelcomeActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class AddPlantActivity : AppCompatActivity(){
    private lateinit var binding: AddPlantBinding
    private val viewModel by viewModels<AddListViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var token = "token"

    private var currentImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                token = user.token
                binding.galleryButton.setOnClickListener { startGallery() }
                binding.cameraButton.setOnClickListener { startCamera() }
                binding.uploadButton.setOnClickListener {
                    uploadImage()
                }
            }
        }
    }

    private fun backToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")

            // Contoh deskripsi konten
            val content = "Testing a response sharing"

            // Membuat RequestBody untuk konten teks
            val contentPart = content.toRequestBody("text/plain".toMediaTypeOrNull())

            // Membuat RequestBody untuk file gambar
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "imgUrl",
                imageFile.name,
                requestImageFile
            )

            showLoading(true)

            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig.getApiService()
                    apiService.uploadImage("Bearer $token", contentPart, multipartBody).enqueue(object : Callback<AddSharingResponse> {
                        override fun onResponse(
                            call: Call<AddSharingResponse>,
                            response: Response<AddSharingResponse>
                        ) {
                            showLoading(false)
                            if (response.isSuccessful) {
                                Log.e(ContentValues.TAG, "onSuccess: ${response.message()}")
                                backToMainActivity()
                            } else {
                                Log.e(ContentValues.TAG, "onFailure1: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<AddSharingResponse>, t: Throwable) {
                            showLoading(false)
                            Log.e(ContentValues.TAG, "onFailure2: ${t.message.toString()}")
                        }
                    })
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, AddSharingResponse::class.java)
                    showToast(errorResponse.message.toString())
                    showLoading(false)
                }
            }
        } ?: showToast(getString(R.string.empty_image))
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}