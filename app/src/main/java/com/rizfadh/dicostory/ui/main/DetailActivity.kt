package com.rizfadh.dicostory.ui.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rizfadh.dicostory.R
import com.rizfadh.dicostory.data.api.response.StoryResult
import com.rizfadh.dicostory.databinding.ActivityDetailBinding
import com.rizfadh.dicostory.utils.Result
import com.rizfadh.dicostory.utils.ViewModelFactory
import com.rizfadh.dicostory.utils.alert
import com.rizfadh.dicostory.utils.dateFormat

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModelFactory: ViewModelFactory
    private val mainViewModel: MainViewModel by viewModels { viewModelFactory }
    private lateinit var userToken: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelFactory = ViewModelFactory.getInstance(this)

        intent.apply {
            getStringExtra(MainActivity.EXTRA_TOKEN)?.let {
                userToken = it
            }
            getStringExtra(USER_ID)?.let {
                userId = it
            }
        }

        val token = "Bearer $userToken"
        mainViewModel.getStoryDetail(token, userId).observe(this) {
            when(it) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    setData(it.data)
                }
                is Result.Empty -> alert(
                    this,
                    getString(R.string.error),
                    getString(R.string.result_empty)
                )
                is Result.Error -> alert(this, getString(R.string.error), it.error)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setData(storyResult: StoryResult) {
        binding.apply {
            tvDetailName.text = storyResult.name
            tvDetailCreatedAt.text = storyResult.createdAt.dateFormat()
            tvDetailDescription.text = storyResult.description
            Glide.with(this@DetailActivity)
                .load(storyResult.photoUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading))
                .into(binding.ivDetailPhoto)
            ivDetailPhoto.contentDescription = getString(R.string.picture_from, storyResult.name)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val USER_ID = "user_id"
    }
}