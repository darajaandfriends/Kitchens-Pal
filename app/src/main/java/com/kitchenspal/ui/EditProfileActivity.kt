package com.kitchenspal.ui

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.kitchenspal.R
import com.kitchenspal.api.ApiService
import com.kitchenspal.model.UpdateNama
import com.kitchenspal.response.LoginResponse
import com.kitchenspal.utils.MySharedPreferences
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException

class EditProfileActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: MySharedPreferences
    private lateinit var ubahImageBtn: Button
    private lateinit var imgProfileBaru: ImageView
    private lateinit var ubahNamaEditText: EditText
    private lateinit var simpanProfilBtn: Button
    private var isActivityDestroyed = false
    private val PERMISSION_REQUEST_CODE = 1
    private val PICK_IMAGE_REQUEST = 2
    private val CAMERA_REQUEST_CODE = 3
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        sharedPreferences = MySharedPreferences(this)

        ubahImageBtn = findViewById(R.id.ubahImage)
        ubahNamaEditText = findViewById(R.id.ubahNama)
        simpanProfilBtn = findViewById(R.id.simpanProfil)
        imgProfileBaru = findViewById(R.id.profileBaru)


        ubahImageBtn.setOnClickListener {
            showImagePickerDialog()
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

        simpanProfilBtn.setOnClickListener {
            val newNama = ubahNamaEditText.text.toString()
            val userId = loginResponse?.idUser
            if (newNama.isNotEmpty()) {
                if (userId != null) {
                    ubahProfile(userId.toInt(), newNama)
                    if (selectedImageUri != null) {
                        val newImage = File(selectedImageUri!!.path)
                        ubahImage(userId.toInt(), newImage)
                    }
                }
            } else {
                showErrorMessage("Nama harus diisi")
            }
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf<CharSequence>("Kamera", "Galeri")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pilih Sumber Gambar")
        builder.setItems(options) { _, item ->
            when {
                options[item] == "Kamera" -> {
                    if (checkCameraPermission()) {
                        openCamera()
                    } else {
                        requestCameraPermission()
                    }
                }
                options[item] == "Galeri" -> {
                    openGallery()
                }
            }
        }
        builder.show()
    }


    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    if (data != null) {
                        selectedImageUri = data.data
                        val imageFilePath = getImageFilePath(selectedImageUri)
                        loadImageFromUri(imageFilePath)
                        if (selectedImageUri != null) {
                            val newImage = File(selectedImageUri!!.path)
                            val loginResponse = getLoginResponse()
                            val userId = loginResponse?.idUser
                            userId?.let { ubahImage(it, newImage) }
                        }
                    }
                }
                CAMERA_REQUEST_CODE -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    if (imageBitmap != null) {
                        selectedImageUri = saveImageToInternalStorage(imageBitmap)
                        loadImageFromUri(selectedImageUri.toString())
                        if (selectedImageUri != null) {
                            val newImage = File(selectedImageUri!!.path)
                            val loginResponse = getLoginResponse()
                            val userId = loginResponse?.idUser
                            userId?.let { ubahImage(it, newImage) }
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getImageFilePath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri!!, projection, null, null, null)
        return if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val filePath = cursor.getString(columnIndex)
            cursor.close()
            filePath
        } else {
            uri?.path
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri? {
        val fileName = "temp_image.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val resolver = contentResolver
        var imageUri: Uri? = null

        try {
            val imageCollection =
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            imageUri = resolver.insert(imageCollection, contentValues)
            if (imageUri != null) {
                val outputStream = resolver.openOutputStream(imageUri)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream)
                    outputStream.close()
                    return imageUri
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    private fun loadImageFromUri(uri: String?) {
        Glide.with(this)
            .load(uri)
            .into(imgProfileBaru)
    }

    private fun ubahProfile(userId: Int, newNama: String) {
        val request = UpdateNama(
            newNama
        )
        ApiService.updateNamaUser(userId, request,
            onSuccess = {
                Log.e("EditProfileActivity", "Profil berhasil diubah")
            },
            onError = { errorMessage ->
                showErrorMessage("Terjadi kesalahan: $errorMessage")
            }
        )
    }
    private fun ubahImage(UserId: Int, newImage: File){
        ApiService.uploadImageById(UserId, newImage,
            onSuccess = {

            },
            onError = {

            }
        )

    }

    private fun showErrorMessage(message: String) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    private fun getUserData(userId: Int) {
        ApiService.getUserById(userId,
            onSuccess = { userResponse ->
                val userData = userResponse.data
                val nama = userData.nama
                val image = userData.image

                ubahNamaEditText.setText(nama)

                Glide.with(this)
                    .load(image)
                    .placeholder(R.drawable.ic_profile)
                    .into(imgProfileBaru)
            },
            onError = { errorMessage ->
                showErrorMessage("Terjadi kesalahan: $errorMessage")
            }
        )
    }

    private fun getLoginResponse(): LoginResponse? {
        return sharedPreferences.getLoginResponse()
    }

    override fun onDestroy() {
        super.onDestroy()
        isActivityDestroyed = true
    }
}
