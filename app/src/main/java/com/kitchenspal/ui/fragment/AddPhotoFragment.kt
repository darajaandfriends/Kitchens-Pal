package com.kitchenspal.ui.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.kitchenspal.R

class AddPhotoFragment : Fragment() {

    private lateinit var webView: WebView
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var fileChooserCallback: String? = null

    private val FILE_CHOOSER_RESULT_CODE = 1
    private val STORAGE_PERMISSION_REQUEST_CODE = 2


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_photo, container, false)

        webView = view.findViewById(R.id.webView)
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true


        // Mengatur WebViewClient untuk menghindari membuka halaman web di browser eksternal
        webView.webViewClient = WebViewClient()
        loadWebViewUrl()

        // Mengatur WebChromeClient untuk meng-handle pemilihan file
        webView.webChromeClient = object : WebChromeClient() {
            // Fungsi untuk memilih file
            override fun onShowFileChooser(
                webView: WebView,
                callback: ValueCallback<Array<Uri>>,
                params: FileChooserParams
            ): Boolean {
                // Menyimpan callback dan params untuk digunakan nanti
                filePathCallback = callback
                val intent: Intent = params.createIntent()
                fileChooserCallback = params.acceptTypes[0]

                // Memeriksa izin akses penyimpanan
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Jika izin belum diberikan, minta izin akses penyimpanan
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION_REQUEST_CODE
                    )
                } else {
                    // Jika izin sudah diberikan, meluncurkan Activity pemilihan file
                    startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE)
                }
                return true
            }
        }

        // Memeriksa izin akses penyimpanan
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Jika izin belum diberikan, minta izin akses penyimpanan
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        } else {
            // Jika izin sudah diberikan, muat URL yang diinginkan
            loadWebViewUrl()
        }

        return view
    }

    private fun loadWebViewUrl() {
        // Muat URL yang diinginkan
        webView.loadUrl("http://34.128.121.71/kitchen-pal-detection")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin akses penyimpanan diberikan

            } else {
                // Izin akses penyimpanan ditolak
                showPermissionDeniedDialog()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Izin Diperlukan")
        dialogBuilder.setMessage("Aplikasi memerlukan izin akses penyimpanan untuk berfungsi dengan baik. Mohon izinkan akses penyimpanan di pengaturan aplikasi.")
        dialogBuilder.setPositiveButton("Pengaturan") { dialog, _ ->
            dialog.dismiss()
            openAppSettings()
        }
        dialogBuilder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (resultCode == Activity.RESULT_OK && filePathCallback != null) {
                val result: Array<Uri>? = if (data != null) data.data?.let { arrayOf(it) } else null

                // Menampilkan file yang dipilih di WebView
                filePathCallback?.onReceiveValue(result)
                filePathCallback = null
            } else {
                // Membatalkan pemilihan file
                filePathCallback?.onReceiveValue(null)
                filePathCallback = null
            }
        }
    }
}
