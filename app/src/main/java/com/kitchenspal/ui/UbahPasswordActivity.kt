package com.kitchenspal.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.kitchenspal.R
import com.kitchenspal.api.ApiService
import com.kitchenspal.model.UpdatePassword
import com.kitchenspal.model.UpdatePasswordRequest
import com.kitchenspal.response.LoginResponse
import com.kitchenspal.utils.MySharedPreferences

class UbahPasswordActivity : AppCompatActivity() {

    private lateinit var ubahPass: EditText
    private lateinit var confirmUbah: EditText
    private lateinit var btUbahPassword: AppCompatButton
    private lateinit var sharedPreferences: MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_password)

        sharedPreferences = MySharedPreferences(this)

        ubahPass = findViewById(R.id.UbahPass)
        confirmUbah = findViewById(R.id.confirmUbah)
        btUbahPassword = findViewById(R.id.btUbahPassword)

        val loginResponse = getLoginResponse()
        val userId = loginResponse?.idUser

        btUbahPassword.setOnClickListener {
            val newPassword = ubahPass.text.toString()
            val confirmedPassword = confirmUbah.text.toString()

            if (newPassword.isEmpty() || confirmedPassword.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi semua field", Toast.LENGTH_SHORT).show()
            } else if (newPassword != confirmedPassword) {
                Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show()
            } else {
                if (userId != null) {
                    ubahPassword(userId, newPassword)
                }
            }
        }
    }

    private fun ubahPassword(userId: Int, newPassword: String) {
            val request = UpdatePassword(
                newPassword
            )
            ApiService.updatePasswordUser(userId, request,
                onSuccess = {
                    Log.e("ajshdjhasjkd", userId.toString())
                    Log.e("ajshdjhasjkd", request.toString())
                    showSuccessDialog("Password berhasil diubah")
                }
            ) { errorMessage ->
                println("Terjadi kesalahan: $errorMessage")
            }
    }

    private fun showSuccessDialog(message: String) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Sukses")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setOnDismissListener {
            }
            .create()

        alertDialog.show()
    }

    private fun getLoginResponse(): LoginResponse? {
        return sharedPreferences.getLoginResponse()
    }
}
