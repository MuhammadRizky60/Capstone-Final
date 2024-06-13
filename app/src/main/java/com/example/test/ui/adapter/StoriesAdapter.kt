package com.example.test.ui.adapter

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.test.data.response.DataGetAllItem

import com.example.test.databinding.ItemStoriesBinding

class StoriesAdapter : PagingDataAdapter<DataGetAllItem, StoriesAdapter.MyViewHolder>(DIFF_CALLBACK){
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        holder.binding.root.setOnClickListener {
            if (user != null) {
                onItemClickCallback?.onItemClicked(user)
            }
        }
        user?.let { holder.bind(it) }
    }

    inner class MyViewHolder(val binding: ItemStoriesBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: DataGetAllItem) {

            Log.d(ContentValues.TAG, "bind: $story")
            binding.tvName.text = "${story.name}"
            binding.tvDetail.text = "${story.content}"

            Glide.with(binding.root.context)
                .load(story.imgUrl)
                .into(binding.ivImage)
        }
    }
    interface OnItemClickCallback {
        fun onItemClicked(data: DataGetAllItem)
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataGetAllItem>() {
            override fun areItemsTheSame(
                oldItem: DataGetAllItem,
                newItem: DataGetAllItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DataGetAllItem,
                newItem: DataGetAllItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}