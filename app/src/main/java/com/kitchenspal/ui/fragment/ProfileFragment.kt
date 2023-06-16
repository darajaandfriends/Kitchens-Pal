package com.kitchenspal.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.kitchenspal.R
import com.kitchenspal.ui.UbahPasswordActivity
import com.kitchenspal.api.ApiService
import com.kitchenspal.response.LoginResponse
import com.kitchenspal.ui.EditProfileActivity
import com.kitchenspal.ui.HistoryOrderActivity
import com.kitchenspal.ui.StartActivity
import com.kitchenspal.ui.UbahAlamatActivity
import com.kitchenspal.utils.MySharedPreferences

class ProfileFragment : Fragment() {

    private lateinit var namaProfileTextView: TextView
    private lateinit var emailProfileTextView: TextView
    private lateinit var alamatProfileTextView: TextView
    private lateinit var btnEditProfile: TextView
    private lateinit var btnLogout: AppCompatButton
    private lateinit var btnUbahPassword: AppCompatButton
    private lateinit var btnHistory: AppCompatButton
    private lateinit var btnUbahAlamat: AppCompatButton
    private lateinit var imgProfile: ImageView
    private lateinit var sharedPreferences: MySharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        namaProfileTextView = view.findViewById(R.id.namaProfile)
        emailProfileTextView = view.findViewById(R.id.emailProfile)
        alamatProfileTextView = view.findViewById(R.id.alamatProfile)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnLogout = view.findViewById(R.id.logOut)
        imgProfile = view.findViewById(R.id.imgProfile)
        btnUbahPassword = view.findViewById(R.id.ubahPassword)
        btnHistory = view.findViewById(R.id.HistoryOrder)
        btnUbahAlamat = view.findViewById(R.id.ubahAlamat)

        sharedPreferences = MySharedPreferences(requireContext())
        btnHistory.setOnClickListener {
            val intent = Intent(requireContext(), HistoryOrderActivity::class.java)
            startActivity(intent)
        }
        btnEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }
        btnUbahAlamat.setOnClickListener {
            val intent = Intent(requireContext(), UbahAlamatActivity::class.java)
            startActivity(intent)
        }
        btnLogout.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin logout?")
                .setPositiveButton("Ya") { dialog, _ ->
                    dialog.dismiss()
                    logout()
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            alertDialog.show()
        }

        val loginResponse = getLoginResponse()
        if (loginResponse != null) {
            val userId = loginResponse.idUser
            if (userId != null) {
                getUserData(userId)
            } else {
                Log.e("ProfileFragment", "IdUser is null")
            }
        }

        btnUbahPassword.setOnClickListener {
            val intent = Intent(requireContext(), UbahPasswordActivity::class.java)
            startActivity(intent)
        }


        return view
    }

    private fun getLoginResponse(): LoginResponse? {
        return sharedPreferences.getLoginResponse()
    }

    private fun getUserData(userId: Int) {
        ApiService.getUserById(userId,
            onSuccess = { userResponse ->
                val userData = userResponse.data
                val namaPengguna = userData.nama
                val email = userData.email
                val alamat = userData.alamat
                val image = userData.image

                namaProfileTextView.text = namaPengguna
                emailProfileTextView.text = email
                alamatProfileTextView.text = alamat

                Glide.with(requireContext())
                    .load(image)
                    .placeholder(R.drawable.ic_profile)
                    .error("error")
                    .into(imgProfile)
            },
            onError = { errorMessage ->
                println("Terjadi kesalahan: $errorMessage")
            }
        )
    }


    private fun logout() {
        val loginResponse = getLoginResponse()
        if (loginResponse != null) {
            val token = loginResponse.token
            if (token != null) {
                ApiService.logout(token,
                    onSuccess = {
                        val intent = Intent(requireContext(), StartActivity::class.java)
                        startActivity(intent)
                        requireActivity().finishAffinity()
                        sharedPreferences.saveBoolean(MySharedPreferences.KEY_LOGIN_STATUS, false)
                        sharedPreferences.clearLoginResponse()
                    },
                    onError = { errorMessage ->
                        Log.e("ProfileFragment", "Failed to logout: $errorMessage")
                    }
                )
            } else {
                Log.e("ProfileFragment", "Token is null")
            }
        } else {
            Log.e("ProfileFragment", "LoginResponse is null")
        }
    }

}

