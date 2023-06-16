package com.kitchenspal.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.kitchenspal.R

class OrderProcessActivity : AppCompatActivity() {

    private lateinit var btBackHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_process)
        btBackHome = findViewById(R.id.kembaliKeHomeButton)
        btBackHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
    }
}