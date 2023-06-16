package com.kitchenspal.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kitchenspal.R
import com.kitchenspal.api.ApiService
import com.kitchenspal.model.Product
import com.kitchenspal.model.TokoItem
import com.kitchenspal.ui.adapter.TokoAdapter

class AddressTokoActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var btNextOrder: AppCompatButton
    private lateinit var recyclerViewToko: RecyclerView
    private lateinit var tokoAdapter: TokoAdapter
    private val apiService = ApiService
    private lateinit var tokoList: List<TokoItem>
    private lateinit var mapViewToko: MapView
    private var googleMap: GoogleMap? = null
    private var selectedPosition = -1
    private var LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_toko)

        val title = intent.getStringExtra("title")
        val selectedProducts = intent.getParcelableArrayListExtra<Product>("selectedProducts")
        val image = intent.getStringExtra("image")
        val harga = intent.getIntExtra("harga", 0)
        val quantity = intent.getIntExtra("qty", 0)
        val Latitude = intent.getStringExtra("Latitude")
        val Longitude = intent.getStringExtra("Longitude")
        val address = intent.getStringExtra("address")
        val detail = intent.getStringExtra("detail")

        recyclerViewToko = findViewById(R.id.tokoRecycle)
        recyclerViewToko.layoutManager = LinearLayoutManager(this)
        tokoAdapter = TokoAdapter(emptyList(), object : TokoAdapter.OnItemSelectedListener {
            override fun onItemSelected(position: Int) {
                selectedPosition = position
                updateMapViewWithSelectedToko()
            }
        })
        recyclerViewToko.adapter = tokoAdapter

        btNextOrder = findViewById(R.id.btnNextOrder)
        btNextOrder.setOnClickListener {

            if (selectedProducts != null) {
                val intent = Intent(this, ConfirmOrderActivity::class.java)
                intent.putParcelableArrayListExtra("selectedProducts", ArrayList(selectedProducts))
                intent.putExtra("Latitude", Latitude)
                intent.putExtra("Longitude", Longitude)
                intent.putExtra("address", address)
                intent.putExtra("detail", detail)
                intent.putExtra("selectedPosition", selectedPosition)
                intent.putExtra("tokoId", tokoList[selectedPosition].toko_id)
                startActivity(intent)
            } else {
                val intent = Intent(this, ConfirmOrderActivity::class.java)
                intent.putExtra("title", title)
                intent.putExtra("image", image)
                intent.putExtra("harga", harga)
                intent.putExtra("qty", quantity)
                intent.putExtra("Latitude", Latitude)
                intent.putExtra("Longitude", Longitude)
                intent.putExtra("address", address)
                intent.putExtra("detail", detail)
                intent.putExtra("selectedPosition", selectedPosition)
                intent.putExtra("tokoId", tokoList[selectedPosition].toko_id)
                startActivity(intent)
            }

        }

        mapViewToko = findViewById(R.id.mapViewToko)
        mapViewToko.onCreate(savedInstanceState)
        mapViewToko.getMapAsync(this)
        getTokoData()
    }

    private fun getTokoData() {
        apiService.getAllToko(
            onSuccess = { tokoItems ->
                tokoList = tokoItems
                tokoAdapter.updateList(tokoList)
            },
            onError = { errorMessage ->
                Log.e("error", errorMessage)
            }
        )
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL

        updateMapViewWithSelectedToko()
    }

    private fun updateMapViewWithSelectedToko() {
        googleMap?.clear()

        if (selectedPosition != -1) {
            val selectedToko = tokoList[selectedPosition]
            val tokoLocation = LatLng(
                selectedToko.toko_latitude.toDouble(),
                selectedToko.toko_longitude.toDouble()
            )
            googleMap?.addMarker(
                MarkerOptions().position(tokoLocation).title(selectedToko.toko_nama)
            )

            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(tokoLocation, 15f))
        }
    }
    override fun onResume() {
        super.onResume()
        mapViewToko.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapViewToko.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapViewToko.onStop()
    }

    override fun onPause() {
        mapViewToko.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapViewToko.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapViewToko.onLowMemory()
    }

}
