package com.kitchenspal.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import com.kitchenspal.R
import com.kitchenspal.api.ApiService
import com.kitchenspal.model.UpdateAlamatUser
import com.kitchenspal.model.UserDataModel
import com.kitchenspal.response.LoginResponse
import com.kitchenspal.utils.MySharedPreferences
import java.io.IOException
import java.util.Locale

class UbahAlamatActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var alamatEditText: EditText
    private lateinit var pilihAlamatTextView: TextView
    private lateinit var detailBangunan: TextInputEditText
    private lateinit var btsave: AppCompatButton
    private lateinit var sharedPreferences: MySharedPreferences

    private val locationPermissionCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_alamat)
        mapView = findViewById(R.id.mapViewBaru)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        sharedPreferences = MySharedPreferences(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        val loginResponse = getLoginResponse()
        val userId = loginResponse?.idUser

        alamatEditText = findViewById(R.id.AlamatBaru)
        pilihAlamatTextView = findViewById(R.id.pilihAlamatBaru)
        detailBangunan = findViewById(R.id.detailBangunanBaru)
        btsave = findViewById(R.id.btSimpanAlatBaru)

        pilihAlamatTextView.setOnClickListener {
            getAddressFromMarkerPosition()
        }
        alamatEditText.setOnEditorActionListener { _, _, _ ->
            val address = alamatEditText.text.toString()
            searchAddress(address)
            true
        }
        btsave.setOnClickListener {
            val markerPosition = googleMap.cameraPosition.target
            val newAlamat = alamatEditText.text.toString()
            val newLatitude = markerPosition.latitude.toString()
            val newLongitude = markerPosition.longitude.toString()
            val newDetail = detailBangunan.text.toString()

            if (newAlamat.isEmpty() || newDetail.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi semua field", Toast.LENGTH_SHORT).show()
            } else {
                if (userId != null) {
                    ubahAlamat(userId, newAlamat, newLongitude, newLatitude, newDetail)
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.uiSettings.isZoomControlsEnabled = true

        if (isLocationPermissionGranted()) {
            requestLastLocation()
        } else {
            requestLocationPermission()
        }

        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng).title("Lokasi Baru"))
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            locationPermissionCode
        )
    }

    private fun requestLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLocation = LatLng(location.latitude, location.longitude)
                googleMap.addMarker(MarkerOptions().position(currentLocation).title("Lokasi Saat Ini"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
            }
        }
    }

    private fun searchAddress(address: String) {
        try {
            val addresses = geocoder.getFromLocationName(address, 1)
            if (addresses!!.isNotEmpty()) {
                val location = LatLng(addresses[0].latitude, addresses[0].longitude)
                googleMap.clear()
                googleMap.addMarker(MarkerOptions().position(location).title(address))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getAddressFromMarkerPosition() {
        val selectedMarkerPosition = googleMap.cameraPosition.target
        try {
            val addresses = geocoder.getFromLocation(
                selectedMarkerPosition.latitude,
                selectedMarkerPosition.longitude,
                1
            )
            if (addresses!!.isNotEmpty()) {
                val fullAddress = addresses[0].getAddressLine(0)
                alamatEditText.setText(fullAddress)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLastLocation()
            } else {
                Toast.makeText(
                    this,
                    "Location permission denied. Unable to get current location.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun ubahAlamat(userId: Int, newAlamat: String, newLongitude: String, newLatitude: String, newDetail: String) {
        val userData = UpdateAlamatUser(
            newAlamat,
            newLatitude,
            newLongitude,
            newDetail
        )
        ApiService.updateAlamatUser(userId, userData,
            onSuccess = {
                showSuccessDialog("Alamat berhasil diubah")
            },
            onError = { errorMessage ->
                println("Terjadi kesalahan: $errorMessage")
            }
        )
    }

    private fun showSuccessDialog(message: String) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Sukses")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                onBackPressed()
            }
            .setOnDismissListener {
                onBackPressed()
            }
            .create()

        alertDialog.show()
    }

    private fun getLoginResponse(): LoginResponse? {
        return sharedPreferences.getLoginResponse()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
