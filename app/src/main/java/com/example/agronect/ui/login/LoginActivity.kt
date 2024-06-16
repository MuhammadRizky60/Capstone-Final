package com.example.agronect.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.agronect.R
import com.example.agronect.data.pref.UserModel
import com.example.agronect.data.response.LoginResponse
import com.example.agronect.data.retrofit.ApiConfig
import com.example.agronect.databinding.ActivityLoginBinding
import com.example.agronect.ui.ViewModelFactory
import com.example.agronect.ui.main.MainActivity
import com.example.agronect.ui.signup.SignupActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.linkRegister.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        setupView()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            showLoading(true)
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (!isValidEmail(email)) {
                showLoading(false)
                showToast(getString(R.string.invalid_email))
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig.getApiService()
                    val loginResponse = apiService.login(email, password)
                    val successResponse = loginResponse.message
                    val token = loginResponse.accessToken
                    val name = loginResponse.name
                    val imgUrl = loginResponse.photoProfileUrl
                    val uid = loginResponse.userId
                    showToast(successResponse)

                    if (token != null && name != null && uid != null) {
                        val userModel =
                            imgUrl?.let { it1 -> UserModel(email, token, true, name, uid, it1) }
                        if (userModel != null) {
                            viewModel.saveSession(userModel)
                        }
                    }

                    showLoading(false)
                    showToast(getString(R.string.success_login))
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()

                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
                    showToast(errorResponse.message)
                    showLoading(false)
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}