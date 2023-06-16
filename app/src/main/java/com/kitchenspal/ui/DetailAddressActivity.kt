package com.kitchenspal.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
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

import java.io.IOException
import java.util.*

class DetailAddressActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var alamatEditText: EditText
    private lateinit var pilihAlamatTextView: AppCompatTextView
    private lateinit var detailBangunan: TextInputEditText
    private lateinit var btRegister: AppCompatButton

    private var selectedLatitude: Double = 0.0
    private var selectedLongitude: Double = 0.0

    private val locationPermissionCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_address)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())

        alamatEditText = findViewById(R.id.Alamat)
        pilihAlamatTextView = findViewById(R.id.pilihAlamat)
        detailBangunan = findViewById(R.id.detailBangunan)
        btRegister = findViewById(R.id.btRegister)

        val nama = intent.getStringExtra("nama").toString()
        val email = intent.getStringExtra("email").toString()
        val password = intent.getStringExtra("password").toString()

        pilihAlamatTextView.setOnClickListener {
            getAddressFromMarkerPosition()
        }
        alamatEditText.setOnEditorActionListener { _, _, _ ->
            val address = alamatEditText.text.toString()
            searchAddress(address)
            true
        }
        btRegister.setOnClickListener {
            val markerPosition = googleMap.cameraPosition.target
            val latitude = markerPosition.latitude.toString()
            val longitude = markerPosition.longitude.toString()
            val alamat = alamatEditText.text.toString()
            val detail = detailBangunan.text.toString()


            ApiService.register(
                nama,
                email,
                password,
                latitude,
                longitude,
                alamat,
                detail,
                onSuccess = { responseRegister ->
                    // Registrasi berhasil, tangani respons di sini
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                },
                onError = {errorRegister ->

                    // Terjadi kesalahan pada registrasi, tangani error di sini
                    Toast.makeText(this@DetailAddressActivity, "Registrasi gagal: $errorRegister", Toast.LENGTH_SHORT).show()
                }

            )

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


