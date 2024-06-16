package com.example.agronect.ui.profile

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.agronect.R
import com.example.agronect.databinding.FragmentProfileBinding
import com.example.agronect.ui.ViewModelFactory
import com.example.agronect.ui.editPassword.editPasswordActivity
import com.example.agronect.ui.editProfile.EditViewModel
import com.example.agronect.ui.editProfile.editProfileActivity
import com.example.agronect.ui.main.MainViewModel
import com.example.agronect.ui.welcome.WelcomeActivity

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<EditViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private var token = "token"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }

        binding.editButton.setOnClickListener {
            val intent = Intent(requireContext(), editProfileActivity::class.java)
            intent.putExtra("username", binding.nameEditTextLayout.text.toString())
            intent.putExtra("email", binding.emailEditTextLayout.text.toString())
            intent.putExtra("imgUrl", viewModel.getSession().value?.imgUrl)
            startActivity(intent)
        }

        binding.btnChangePassword.setOnClickListener {
            startActivity(Intent(requireContext(), editPasswordActivity::class.java))
        }

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                val intent = Intent(requireContext(), WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                activity?.finish()
            } else {
                val mainViewModel = obtainViewModel()

                token = user.token
                Log.d(ContentValues.TAG, "token: $token")
                binding.emailEditTextLayout.text = user.email
                binding.nameEditTextLayout.text = user.name

                user.imgUrl?.let { url ->
                    Glide.with(this)
                        .load(url)
                        .into(binding.photoProfile)
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun obtainViewModel(): MainViewModel {
        val factory = ViewModelFactory.getInstance(requireActivity().application)
        return ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
