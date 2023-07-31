package com.rizfadh.dicostory.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rizfadh.dicostory.databinding.FragmentHomeBinding
import com.rizfadh.dicostory.ui.adapter.LoadingStateAdapter
import com.rizfadh.dicostory.ui.adapter.StoryAdapter
import com.rizfadh.dicostory.ui.addstory.AddStoryActivity
import com.rizfadh.dicostory.utils.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModelFactory: ViewModelFactory
    private val mainViewModel: MainViewModel by viewModels { viewModelFactory }
    private lateinit var userToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            userToken = it.getString(MainActivity.EXTRA_TOKEN).toString()
        }

        viewModelFactory = ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val storyAdapter = StoryAdapter {
            toDetailActivity(it.id)
        }

        val token = "Bearer $userToken"
        mainViewModel.getStories(token).observe(viewLifecycleOwner) {
            storyAdapter.submitData(lifecycle, it)
        }

        binding.rvHomeStory.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            setHasFixedSize(true)
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { storyAdapter.retry() }
            )
        }

        binding.fabHomeAddStory.setOnClickListener { toAddStoryActivity() }
    }

    private fun toAddStoryActivity() {
        val addStoryIntent = Intent(requireActivity(), AddStoryActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_TOKEN, userToken)
        }
        startActivity(addStoryIntent)
    }

    private fun toDetailActivity(userId: String) {
        val detailIntent = Intent(requireActivity(), DetailActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_TOKEN, userToken)
            putExtra(DetailActivity.USER_ID, userId)
        }
        startActivity(detailIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}