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
import com.example.test.data.response.AddSharingResponse
import com.example.test.data.response.UpdateProfilePhotoResponse
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
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
                    updateUserProfile(token, userId, newUsername, newEmail)
                    currentImageUri.let { uri ->
                        if (uri != null) {
                            uploadImage(this, userId, uri, token)
                        }
                    }
                }

                binding.ivCamera.setOnClickListener { startCamera() }
                binding.ivGalery.setOnClickListener { startGallery() }
            }
        }
    }

    private fun updateUserProfile(token: String, uid: String, name: String, email: String) {
        lifecycleScope.launch {
            try {
                val apiService = ApiConfig.getApiService()
                val response = apiService.updateUser("Bearer $token", uid, mapOf("name" to name, "email" to email))
                if (response.isSuccessful) {
                    val successResponse = response.body()?.message
                    val updatedUser = response.body()?.dataUpdate

                    successResponse?.let { showToast(it) }

                    if (updatedUser != null) {
                        viewModel.saveSession(UserModel(updatedUser.email ?: "", token, true, updatedUser.name ?: "", updatedUser.userId ?: ""))
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

//    private fun uploadProfilePhoto(context: Context, userId: String, fileUri: Uri, token: String) {
//        val file = File(fileUri.path)
//        val requestFile = RequestBody.create(
//            context.contentResolver.getType(fileUri)?.toMediaTypeOrNull(),
//            file
//        )
//        val body = MultipartBody.Part.createFormData("profile_photo", file.name, requestFile)
//
//        val call = ApiConfig.getApiService().uploadProfilePhoto("Bearer $token", userId, body)
//        call.enqueue(object : Callback<UpdateProfilePhotoResponse> {
//            override fun onResponse(
//                call: Call<UpdateProfilePhotoResponse>,
//                response: Response<UpdateProfilePhotoResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val result = response.body()
//                    result?.let {
//                        it.message?.let { it1 -> showToast(it1) }
//                        it.dataUploadProfile?.photoProfileUrl?.let { it1 ->
//                            Log.d("Profile Photo URL",
//                                it1
//                            )
//                        }
//                    }
//                } else {
//                    Log.e("Upload Photo", response.errorBody()?.string() ?: "Unknown error")
//                }
//            }
//
//            override fun onFailure(call: Call<UpdateProfilePhotoResponse>, t: Throwable) {
//                Log.e("Upload Photo", t.message ?: "Unknown error")
//            }
//        })
//    }

    private fun uploadImage(context: Context, userId: String, fileUri: Uri, token: String) {
        val imagePart: MultipartBody.Part? = currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            MultipartBody.Part.createFormData("imgUrl", imageFile.name, requestImageFile)
        }

        lifecycleScope.launch {
            try {
                val apiService = ApiConfig.getApiService()
                val call =
                    imagePart?.let { apiService.uploadProfilePhoto("Bearer $token", userId, it) }

                if (call != null) {
                    call.enqueue(object : Callback<UpdateProfilePhotoResponse> {
                        override fun onResponse(call: Call<UpdateProfilePhotoResponse>, response: Response<UpdateProfilePhotoResponse>) {
                            if (response.isSuccessful) {
                                Log.e(ContentValues.TAG, "onSuccess: ${response.message()}")
                            } else {
                                Log.e(ContentValues.TAG, "onFailure1: ${response.message()}")
                                showToast(response.message())
                            }
                        }

                        override fun onFailure(call: Call<UpdateProfilePhotoResponse>, t: Throwable) {
                            Log.e(ContentValues.TAG, "onFailure2: ${t.message.toString()}")
                            showToast(t.message.toString())
                        }
                    })
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, UpdateProfilePhotoResponse::class.java)
                showToast(errorResponse.message.toString())
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