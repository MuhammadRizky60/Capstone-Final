package com.example.test.ui.editProfile

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import com.example.test.data.pref.UserModel
import com.example.test.data.response.UpdateProfileResponse
import com.example.test.data.retrofit.ApiConfig
import com.example.test.data.util.getImageUri
import com.example.test.data.util.reduceFileImage
import com.example.test.data.util.uriToFile
import com.example.test.databinding.ActivityEditProfileBinding
import com.example.test.ui.ViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.File

class editProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding

    private val viewModel by viewModels<EditViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("username")
        val email = intent.getStringExtra("email")

        binding.editUsername.setText(username)
        binding.editEmail.setText(email)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        viewModel.getSession().observe(this) { user ->
            if (user != null && user.isLogin) {
                val token = user.token
                val userId = user.uid

                binding.saveButton.setOnClickListener {
                    val newUsername = binding.editUsername.text.toString()
                    val newEmail = binding.editEmail.text.toString()
                    updateUserProfile(token, userId, newUsername, newEmail, currentImageUri)
                }

                binding.ivCamera.setOnClickListener { startCamera() }
                binding.ivGalery.setOnClickListener { startGallery() }
            }
        }
    }

    private fun updateUserProfile(token: String, uid: String, name: String?, email: String?, imgUrl: Uri?) {
        lifecycleScope.launch {
            try {
                val apiService = ApiConfig.getApiService()

                val userInfo = mutableMapOf<String, RequestBody>()
                name?.let { userInfo["name"] = it.toRequestBody("text/plain".toMediaType()) }
                email?.let { userInfo["email"] = it.toRequestBody("text/plain".toMediaType()) }

                val imgUrl: MultipartBody.Part? = imgUrl?.let { uri ->
                    val imageFile = uriToFile(uri, this@editProfileActivity).reduceFileImage()
                    val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                    MultipartBody.Part.createFormData("imgUrl", imageFile.name, requestImageFile)
                }

                val response = apiService.updateUser("Bearer $token", uid, userInfo, imgUrl)

                if (response.isSuccessful) {
                    val successResponse = response.body()?.message
                    val updatedUser = response.body()?.dataUpdate

                    successResponse?.let { showToast(it) }

                    if (updatedUser != null) {
                        viewModel.saveSession(UserModel(updatedUser.email ?: "", token, true, updatedUser.name ?: "", updatedUser.userId ?: "", updatedUser.photoProfileUrl ?: ""))
                    }
                    showToast(getString(R.string.success_profile_update))
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, UpdateProfileResponse::class.java)
                    errorResponse.message?.let { showToast(it) }
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, UpdateProfileResponse::class.java)
                errorResponse.message?.let { showToast(it) }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
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
            binding.photoProfile.setImageURI(it)
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

    private fun showToast(message: String) {
        Toast.makeText(this@editProfileActivity, message, Toast.LENGTH_SHORT).show()
    }
}