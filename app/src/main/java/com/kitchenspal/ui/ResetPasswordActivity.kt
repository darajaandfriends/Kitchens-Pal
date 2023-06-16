package com.kitchenspal.ui

import android.annotation.SuppressLint
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.kitchenspal.R
import com.kitchenspal.api.ApiService
import com.kitchenspal.model.UpdatePasswordRequest

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var newPass: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var resetButton: AppCompatButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val reset = intent.getStringExtra("token")
        newPass = findViewById(R.id.newPasseditText)
        confirmPassword = findViewById(R.id.confirmNew)
        resetButton = findViewById(R.id.btReset)

        resetButton.setOnClickListener {
            val newPassword = newPass.text.toString()
            val confirmedPassword = confirmPassword.text.toString()

            if (newPassword.isEmpty() || confirmedPassword.isEmpty()) {
                Toast.makeText(this, "Mohon isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPassword.length < 6){
                Toast.makeText(this, "Minimal Password 6", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmedPassword) {
                Toast.makeText(this, "Konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val token = reset

            if (token != null) {
                val request = UpdatePasswordRequest(newPassword)

                ApiService.resetPassword(token, request,
                    onSuccess = { response ->
                        val alertDialog = AlertDialog.Builder(this)
                        alertDialog.setTitle("Password Disimpan")
                        alertDialog.setMessage("Password Anda telah berhasil disimpan.")
                        alertDialog.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                            onBackPressed()
                        }
                        alertDialog.setOnDismissListener(DialogInterface.OnDismissListener {
                            onBackPressed()
                        })
                        alertDialog.show()
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    },
                    onError = { errorMessage ->
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(this, "Token tidak tersedia", Toast.LENGTH_SHORT).show()
            }

        }

    }

}