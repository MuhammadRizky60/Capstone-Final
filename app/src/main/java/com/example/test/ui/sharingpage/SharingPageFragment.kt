package com.example.test.ui.sharingpage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.data.response.DataGetAllItemItem
import com.example.test.databinding.FragmentSharingpageBinding
import com.example.test.ui.ViewModelFactory
import com.example.test.ui.adapter.LoadingStateAdapter
import com.example.test.ui.adapter.StoriesAdapter
import com.example.test.ui.addList.AddListActivity
import com.example.test.ui.detail.DetailActivity

class SharingPageFragment : Fragment() {
    private val viewModel by viewModels<SharingPageViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private var _binding: FragmentSharingpageBinding? = null
    private val binding get() = _binding!!
    private var token = "token"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSharingpageBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        viewModel.getSession().observe(viewLifecycleOwner) { user ->
//            token = user.token
//            viewModel.getStory(token)
//        }
//        val adapter = StoriesAdapter()
//
//        viewModel.getStory(token).observe(viewLifecycleOwner) { storyList ->
//            adapter.submitData(lifecycle, storyList)
//            binding.rvStories.adapter = adapter.withLoadStateFooter(
//                footer = LoadingStateAdapter {
//                    adapter.retry()
//                }
//            )
//        }
//
//        adapter.setOnItemClickCallback(object : StoriesAdapter.OnItemClickCallback {
//            override fun onItemClicked(data: DataGetAllItemItem) {
//                Intent(this@SharingPageFragment, DetailActivity::class.java).also {
//                    it.putExtra(DetailActivity.ID, data.userId)
//                    startActivity(it)
//                }
//            }
//        })
//
//        viewModel.isLoading.observe(viewLifecycleOwner) {
//            showLoading(it)
//        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            token = user.token
            viewModel.getStory(token).observe(viewLifecycleOwner) { storyList ->
                val adapter = StoriesAdapter()
                adapter.submitData(lifecycle, storyList)
                binding.rvStories.adapter = adapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        adapter.retry()
                    }
                )

                adapter.setOnItemClickCallback(object : StoriesAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: DataGetAllItemItem) {
                        Intent(requireContext(), DetailActivity::class.java).also {
                            it.putExtra(DetailActivity.ID, data.sharingId)
                            startActivity(it)
                        }
                    }
                })
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        setupView()

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvStories.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvStories.addItemDecoration(itemDecoration)

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddListActivity::class.java))
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun showLoading(state: Boolean) {
        binding.progressbar.visibility = if (state) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

