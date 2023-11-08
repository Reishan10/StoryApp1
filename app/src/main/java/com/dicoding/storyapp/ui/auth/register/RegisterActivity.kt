package com.dicoding.storyapp.ui.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.UserDataStorePreferences
import com.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.ui.auth.login.LoginActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()

        binding.haveAccountTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun playAnimation() {
        val haveAccountTextView =
            ObjectAnimator.ofFloat(binding.haveAccountTextView, View.ALPHA, 1f).setDuration(400)
        val tvRegister = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f).setDuration(400)
        val tvRegisterName =
            ObjectAnimator.ofFloat(binding.tvRegisterName, View.ALPHA, 1f).setDuration(400)
        val edRegisterName =
            ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(400)
        val tvRegisterEmail =
            ObjectAnimator.ofFloat(binding.tvRegisterEmail, View.ALPHA, 1f).setDuration(400)
        val edRegisterEmail =
            ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(400)
        val tvRegisterPassword =
            ObjectAnimator.ofFloat(binding.tvRegisterPassword, View.ALPHA, 1f).setDuration(400)
        val edRegisterPassword =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(400)
        val register =
            ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(400)

        AnimatorSet().apply {
            playSequentially(
                tvRegister,
                tvRegisterName,
                edRegisterName,
                tvRegisterEmail,
                edRegisterEmail,
                tvRegisterPassword,
                edRegisterPassword,
                haveAccountTextView,
                register,
            )
            startDelay = 500
        }.start()
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

    private fun setupViewModel() {
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserDataStorePreferences.getInstance(dataStore))
        )[RegisterViewModel::class.java]

        registerViewModel?.let { registervm ->
            registervm.message.observe(this) { message ->
                if (message == "201") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.info)
                    builder.setMessage(R.string.validate_register_success)
                    builder.setIcon(R.drawable.ic_check_green)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 2000)
                }
            }
            registervm.error.observe(this) { error ->
                if (error == "400") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.info)
                    builder.setMessage(R.string.validate_register_failed)
                    builder.setIcon(R.drawable.ic_close_red)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                    }, 2000)
                }
            }
            registervm.isLoading.observe(this) {
                showLoading(it)
            }

        }
    }

    private fun showLoading(isLoading: Boolean) {

        if (isLoading) {
            binding.progressBarRegister.visibility = View.VISIBLE
        } else {
            binding.progressBarRegister.visibility = View.GONE
        }
    }


    private fun setupAction() {
        binding.registerButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            when {
                name.isEmpty() -> {
                    binding.edRegisterName.error = getString(R.string.input_name)
                }

                email.isEmpty() -> {
                    binding.edRegisterEmail.error = getString(R.string.input_email)
                }

                password.isEmpty() -> {
                    binding.edRegisterPassword.error = getString(R.string.input_password)
                }

                password.length < 6 -> {
                    binding.edRegisterPassword.error = getString(R.string.label_validation_password)
                }

                else -> {
                    registerViewModel.register(name, email, password)
                }
            }
        }
    }
}