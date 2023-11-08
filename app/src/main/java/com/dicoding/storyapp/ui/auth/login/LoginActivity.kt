package com.dicoding.storyapp.ui.auth.login

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
import com.dicoding.storyapp.databinding.ActivityLoginBinding
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.ui.auth.register.RegisterActivity
import com.dicoding.storyapp.ui.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeView()
        initializeViewModel()
        setActions()
        animateViewEntrance()

        binding.haveAccountTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun animateViewEntrance() {
        val haveAccountTextView =
            ObjectAnimator.ofFloat(binding.haveAccountTextView, View.ALPHA, 1f).setDuration(400)
        val tvLogin = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(400)
        val tvLoginEmail =
            ObjectAnimator.ofFloat(binding.tvLoginEmail, View.ALPHA, 1f).setDuration(400)
        val edLoginEmail =
            ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(400)
        val tvEdLoginPassword =
            ObjectAnimator.ofFloat(binding.tvEdLoginPassword, View.ALPHA, 1f).setDuration(400)
        val edLoginPassword =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(400)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(400)

        AnimatorSet().apply {
            playSequentially(
                tvLogin,
                tvLoginEmail,
                edLoginEmail,
                tvEdLoginPassword,
                edLoginPassword,
                haveAccountTextView,
                login,
            )
            startDelay = 500
        }.start()
    }

    private fun initializeView() {
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

    private fun initializeViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserDataStorePreferences.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.let { vmSignin ->
            vmSignin.loginResponse.observe(this) { signin ->
                vmSignin.saveUserData(
                    signin.loginResult.name,
                    signin.loginResult.userId,
                    signin.loginResult.token
                )
            }
            vmSignin.message.observe(this) { message ->
                if (message == "200") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.info)
                    builder.setMessage(R.string.validate_login_success)
                    builder.setIcon(R.drawable.ic_check_green)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }, 2000)
                }
            }
            vmSignin.error.observe(this) { error ->
                if (error == "400") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.info)
                    builder.setMessage(R.string.label_invalid_email)
                    builder.setIcon(R.drawable.ic_close_red)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                    }, 2000)
                }
                if (error == "401") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.info)
                    builder.setMessage(R.string.login_user_not_found)
                    builder.setIcon(R.drawable.ic_close_red)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                    }, 2000)
                }
            }
            vmSignin.isLoading.observe(this) {
                showLoading(it)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarLogin.visibility = View.VISIBLE
        } else {
            binding.progressBarLogin.visibility = View.GONE
        }
    }

    private fun setActions() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = getString(R.string.input_email)
                }

                password.isEmpty() -> {
                    binding.edLoginPassword.error = getString(R.string.input_password)
                }

                password.length < 8 -> {
                    binding.edLoginPassword.error = getString(R.string.label_validation_password)
                }

                else -> {
                    loginViewModel.login(email, password)
                }
            }
        }
    }
}
