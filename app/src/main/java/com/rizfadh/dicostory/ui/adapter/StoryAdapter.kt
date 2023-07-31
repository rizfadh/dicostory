package com.rizfadh.dicostory.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rizfadh.dicostory.R
import com.rizfadh.dicostory.data.api.response.StoryResult
import com.rizfadh.dicostory.databinding.ItemStoryBinding
import com.rizfadh.dicostory.utils.dateFormat

class StoryAdapter(
    private val onItemClick: (StoryResult) -> Unit
) : PagingDataAdapter<StoryResult, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(private val binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(storyResult: StoryResult) {
            Glide.with(itemView.context)
                .load(storyResult.photoUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading))
                .into(binding.ivItemPhoto)
            binding.ivItemPhoto.contentDescription = itemView.context.getString(R.string.picture_from, storyResult.name)
            binding.tvItemName.text = storyResult.name
            binding.tvItemDescription.text = storyResult.description
            binding.tvItemCreatedAt.text = storyResult.createdAt.dateFormat()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        story?.let { storyResult ->
            holder.bind(storyResult)
            holder.itemView.setOnClickListener {
                onItemClick(storyResult)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<StoryResult> =
            object : DiffUtil.ItemCallback<StoryResult>() {
                override fun areItemsTheSame(oldUser: StoryResult, newUser: StoryResult): Boolean {
                    return oldUser.id == newUser.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldUser: StoryResult, newUser: StoryResult): Boolean {
                    return oldUser == newUser
                }
            }
    }
}