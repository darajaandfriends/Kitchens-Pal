package com.kitchenspal.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.kitchenspal.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        nameEditText = findViewById(R.id.nameRegister)
        emailEditText = findViewById(R.id.emailRegister)
        passwordEditText = findViewById(R.id.pswRegister)
        confirmPasswordEditText = findViewById(R.id.confirmpsw)
        nextButton = findViewById(R.id.nextRegister)

        nextButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (isValidName(name) && isValidEmail(email) && isValidPassword(password) && isValidConfirmPassword(password, confirmPassword)) {

                val intent = Intent(this, DetailAddressActivity::class.java)
                intent.putExtra("nama", name)
                intent.putExtra("email", email)
                intent.putExtra("password", password)
                startActivity(intent)

            } else {
                Toast.makeText(this@RegisterActivity, "Invalid input or passwords don't match", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidName(name: String): Boolean {
        return name.isNotBlank()
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@")
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    private fun isValidConfirmPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
}
