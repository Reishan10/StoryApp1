package com.dicoding.storyapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.UserDataStorePreferences
import com.dicoding.storyapp.data.model.ListStoryItem
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.ui.auth.login.LoginActivity
import com.dicoding.storyapp.ui.auth.login.LoginViewModel
import com.dicoding.storyapp.ui.story.AddStoryActivity
import com.dicoding.storyapp.ui.story.StoryAdapter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var loginViewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.app_name)

        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserDataStorePreferences.getInstance(dataStore))
        )[MainViewModel::class.java]

        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserDataStorePreferences.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.getUserData().observe(this) { user ->
            if (user.userId.isEmpty()) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                mainViewModel.getListStory(user.token)
            }
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvListStory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvListStory.addItemDecoration(itemDecoration)

        mainViewModel.storyList.observe(this) { listStory ->
            setReviewData(listStory)
        }
        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding.btnAddStory.setOnClickListener {
            val i = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(i)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_language -> {
                val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(mIntent)
            }

            R.id.action_logout -> {
                loginViewModel.logout()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun setReviewData(listStory: List<ListStoryItem>) {
        val adapter = StoryAdapter(listStory as ArrayList<ListStoryItem>)
        binding.rvListStory.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}