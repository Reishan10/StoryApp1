package com.dicoding.storyapp.ui.story

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityDetailStoryBinding
import com.dicoding.storyapp.utils.withDateFormat


class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    companion object {
        const val NAME = "name"
        const val CREATE_AT = "create_at"
        const val DESCRIPTION = "description"
        const val PHOTO_URL = "photoUrl"
    }

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detail_story)

        val photoUrl = intent.getStringExtra(PHOTO_URL)
        val name = intent.getStringExtra(NAME)
        val create_at = intent.getStringExtra(CREATE_AT)
        val description = intent.getStringExtra(DESCRIPTION)

        Glide.with(binding.root.context)
            .load(photoUrl)
            .into(binding.ivDetailPhoto)
        binding.tvDetailName.text = name
        binding.tvDetailCreatedTime.text = create_at?.withDateFormat()
        binding.tvDetailDescription.text = description

    }
}