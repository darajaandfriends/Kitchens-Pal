package com.kitchenspal.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.kitchenspal.R
import com.kitchenspal.api.ApiService
import com.kitchenspal.model.ResetPasswordRequest
import com.kitchenspal.model.UpdatePasswordRequest

class ForgotPassActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var nextbutton: AppCompatButton


    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)

        emailEditText = findViewById(R.id.emailForgot)
        nextbutton = findViewById(R.id.btnext)


        forgotPassword()


    }

    private fun forgotPassword() {
        nextbutton.setOnClickListener {
                val email = emailEditText.text.toString()
                if (email.isEmpty()) {
                    Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (isValidEmail(email)){
                    Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val request = ResetPasswordRequest(email)
                ApiService.requestResetPassword(request,
                    onSuccess = { response ->
                        val resetPasswordToken = response.token
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.putExtra("token",resetPasswordToken)
                        startActivity(intent)

                    },
                    onError = { errorMessage ->
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )

        }

    }
    private fun isValidEmail(email: String): Boolean {
        return email.contains("@")
    }

}