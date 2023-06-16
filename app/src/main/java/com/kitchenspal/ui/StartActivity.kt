package com.kitchenspal.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.kitchenspal.R
import com.kitchenspal.utils.MySharedPreferences

class StartActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var sharedPreferences: MySharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        loginButton = findViewById(R.id.Login)
        registerButton = findViewById(R.id.Register)

        sharedPreferences = MySharedPreferences(this)
        val isLoggedIn = sharedPreferences.getBoolean(MySharedPreferences.KEY_LOGIN_STATUS, false)
        if (isLoggedIn) {
            val intent = Intent(this@StartActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            setContentView(R.layout.activity_start)
            loginButton = findViewById(R.id.Login)
            registerButton = findViewById(R.id.Register)

            loginButton.setOnClickListener {
                val intent = Intent(this@StartActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            registerButton.setOnClickListener {
                val intent = Intent(this@StartActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }

    }

}