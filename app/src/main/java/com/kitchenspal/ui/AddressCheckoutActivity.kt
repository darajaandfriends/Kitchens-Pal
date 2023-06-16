package com.kitchenspal.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.kitchenspal.model.Product
import com.kitchenspal.model.UserDataModel
import com.kitchenspal.response.LoginResponse
import com.kitchenspal.utils.MySharedPreferences
import java.io.IOException
import java.util.Locale

class AddressCheckoutActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapViewCheckout: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var alamatEditText: EditText
    private lateinit var pilihAlamatTextView: TextView
    private lateinit var detailBangunan: TextInputEditText
    private lateinit var btNextOrder: AppCompatButton
    private lateinit var sharedPreferences: MySharedPreferences
    private val locationPermissionCode = 1
    private lateinit var btAlamatTersedia: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_checkout)

        mapViewCheckout = findViewById(R.id.mapViewCheckout)
        mapViewCheckout.onCreate(savedInstanceState)
        mapViewCheckout.getMapAsync(this)

        sharedPreferences = MySharedPreferences(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        val loginResponse = getLoginResponse()
        val userId = loginResponse?.idUser

        alamatEditText = findViewById(R.id.AlamatCheckout)
        pilihAlamatTextView = findViewById(R.id.pilihMapsCheckout)
        detailBangunan = findViewById(R.id.detailCheckout)
        btAlamatTersedia = findViewById(R.id.pilihAlamatTersedia)
        btNextOrder = findViewById(R.id.btnNextOrder)

        pilihAlamatTextView.setOnClickListener {
            getAddressFromMarkerPosition()
        }

        btAlamatTersedia.setOnClickListener {
            val selectedMarkerPosition = googleMap.cameraPosition.target
            val addresses = geocoder.getFromLocation(selectedMarkerPosition.latitude, selectedMarkerPosition.longitude, 1)
            if (addresses!!.isNotEmpty()) {
                val address = addresses[0]
                val addressLine = address.getAddressLine(0)
                val city = address.locality
                val postalCode = address.postalCode
                val country = address.countryName

                val alamatTersedia = "$addressLine, $city, $postalCode, $country"
                alamatEditText.setText(alamatTersedia)
                val userId = userId ?: ""
                Toast.makeText(this, "UserId: $userId, Alamat Tersedia: $alamatTersedia", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Tidak dapat mengambil alamat tersedia", Toast.LENGTH_SHORT).show()
            }
        }

        alamatEditText.setOnEditorActionListener { _, _, _ ->
            val address = alamatEditText.text.toString()
            searchAddress(address)
            true
        }

        btNextOrder.setOnClickListener {
            val markerPosition = googleMap.cameraPosition.target
            val title = intent.getStringExtra("title")
            val selectedProducts = intent.getParcelableArrayListExtra<Product>("selectedProducts")
            Log.d("produk", selectedProducts.toString())
            val image = intent.getStringExtra("image")
            val harga = intent.getIntExtra("harga", 0)
            val quantity = intent.getIntExtra("qty", 0)
            val Latitude = markerPosition.latitude.toString()
            val Longitude = markerPosition.longitude.toString()
            val address = alamatEditText.text.toString()
            val detail = detailBangunan.text.toString()
            if (address.isNotEmpty() && detail.isNotEmpty()) {
                if (selectedProducts != null) {
                    val intent = Intent(this, AddressTokoActivity::class.java)
                    intent.putParcelableArrayListExtra("selectedProducts", ArrayList(selectedProducts))
                    intent.putExtra("Latitude", Latitude)
                    intent.putExtra("Longitude", Longitude)
                    intent.putExtra("address", address)
                    intent.putExtra("detail", detail)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, AddressTokoActivity::class.java)
                    intent.putExtra("title", title)
                    intent.putExtra("image", image)
                    intent.putExtra("harga", harga)
                    intent.putExtra("qty", quantity)
                    intent.putExtra("Latitude", Latitude)
                    intent.putExtra("Longitude", Longitude)
                    intent.putExtra("address", address)
                    intent.putExtra("detail", detail)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this, "Harap lengkapi alamat dan detail bangunan", Toast.LENGTH_SHORT).show()
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

    private fun getLoginResponse(): LoginResponse? {
        return sharedPreferences.getLoginResponse()
    }
    override fun onResume() {
        super.onResume()
        mapViewCheckout.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapViewCheckout.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapViewCheckout.onStop()
    }

    override fun onPause() {
        mapViewCheckout.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapViewCheckout.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapViewCheckout.onLowMemory()
    }
}