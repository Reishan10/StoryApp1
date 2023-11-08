package com.dicoding.storyapp.ui.story

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.model.ListStoryItem
import com.dicoding.storyapp.databinding.ItemStoryBinding
import com.dicoding.storyapp.utils.withDateFormat

class StoryAdapter(private val listReview: List<ListStoryItem>) :
    RecyclerView.Adapter<StoryAdapter.MyViewHolderStory>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderStory {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolderStory(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolderStory, position: Int) {

        val data = listReview[position]
        holder.bind(data)
    }

    override fun getItemCount() = listReview.size

    class MyViewHolderStory(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            Glide.with(binding.root.context)
                .load(data.photoUrl)
                .into(binding.imgItemPhoto)

            binding.tvItemName.text = data.name
            binding.tvItemCreated.text = data.createdAt.withDateFormat()
            binding.tvItemDescription.text = data.description
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.NAME, data.name)
                intent.putExtra(DetailStoryActivity.CREATE_AT, data.createdAt)
                intent.putExtra(DetailStoryActivity.DESCRIPTION, data.description)
                intent.putExtra(DetailStoryActivity.PHOTO_URL, data.photoUrl)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        androidx.core.util.Pair(binding.imgItemPhoto, "photo"),
                        androidx.core.util.Pair(binding.tvItemName, "name"),
                        androidx.core.util.Pair(binding.tvItemCreated, "createdate"),
                        androidx.core.util.Pair(binding.tvItemDescription, "description"),
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }
}