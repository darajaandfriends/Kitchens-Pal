package com.kitchenspal.ui


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.kitchenspal.R
import com.kitchenspal.api.ApiService
import com.kitchenspal.utils.MySharedPreferences

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPassword: TextView
    private lateinit var sharedPreferences: MySharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = MySharedPreferences(this)

        emailEditText = findViewById(R.id.emailLogin)
        passwordEditText = findViewById(R.id.PswLogin)
        loginButton = findViewById(R.id.btLogin)
        forgotPassword = findViewById(R.id.forgotPsw)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (isValidEmail(email) && isValidPassword(password)) {
                val apiService = ApiService

                apiService.login(email, password, { loginResponse ->
                    val message = loginResponse.message
                    val token = loginResponse.token
                    val id = loginResponse.idUser
                    sharedPreferences.saveBoolean(MySharedPreferences.KEY_LOGIN_STATUS, true)
                    sharedPreferences.saveLoginResponse(loginResponse)
                    navigateToMainActivity()
                    Log.d("message", message)
                    Log.d("User ID", id.toString())
                }, { error ->
                    Toast.makeText(this@LoginActivity, "Login failed: $error", Toast.LENGTH_SHORT)
                        .show()
                })
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }

            forgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPassActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@")
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}
