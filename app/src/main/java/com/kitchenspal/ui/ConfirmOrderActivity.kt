package com.kitchenspal.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.kitchenspal.R
import com.kitchenspal.api.ApiService
import com.kitchenspal.model.AddCartTransaction
import com.kitchenspal.model.CheckoutItem
import com.kitchenspal.model.Product
import com.kitchenspal.response.LoginResponse
import com.kitchenspal.utils.MySharedPreferences

class ConfirmOrderActivity : AppCompatActivity() {

    private lateinit var btOrder: Button
    private lateinit var titleTextView: TextView
    private lateinit var tvAddressLine: TextView
    private lateinit var tvCity: TextView
    private lateinit var tvQty: TextView
    private var apiService= ApiService
    private lateinit var tvTotalPayment: TextView
    private lateinit var sharedPreferences: MySharedPreferences
    
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_order)

        sharedPreferences = MySharedPreferences(this)

        val selectedProducts = intent.getParcelableArrayListExtra<Product>("selectedProducts")
        val title = intent.getStringExtra("title")
        val image = intent.getStringExtra("image")
        val harga = intent.getIntExtra("harga", 0)
        val quantity = intent.getIntExtra("qty", 0)
        val address = intent.getStringExtra("address")
        val detail = intent.getStringExtra("detail")
        val tokoId = intent.getIntExtra("tokoId", 0)

        titleTextView = findViewById(R.id.tvProduct)
        tvAddressLine = findViewById(R.id.tvAddressLine)
        tvCity = findViewById(R.id.tvCity)
        tvQty = findViewById(R.id.tvQty)
        tvTotalPayment = findViewById(R.id.tvTotalHarga)

        titleTextView.text = title
        tvAddressLine.text = address
        tvCity.text = detail
        tvQty.text = quantity.toString()
        var totalPayment = 0

        if (harga == null) {
            totalPayment = harga * quantity
            tvTotalPayment.text = "Rp. $totalPayment,-"
        } else {
            selectedProducts?.let {
                val selectedTitleText = StringBuilder()
                val selectedQtyText = StringBuilder()

                for (product in it) {
                    val productTitle = product.title
                    val productPrice = product.price
                    val productQuantity = product.quantity

                    selectedTitleText.append("$productTitle\n")
                    selectedQtyText.append("$productQuantity\n")

                    totalPayment += productPrice * productQuantity
                }

                titleTextView.text = selectedTitleText.toString()
                tvQty.text = selectedQtyText.toString()
                Log.d("produkk", totalPayment.toString())
                tvTotalPayment.text = "Rp. $totalPayment,-"
            }
        }


        btOrder = findViewById(R.id.btnPesan)
        btOrder.setOnClickListener {
            val loginResponse = getLoginResponse()
            val userId = loginResponse?.idUser

            if (title.isNullOrEmpty() || image.isNullOrEmpty()){
                val selectedCarts = mutableMapOf<String, Int>()
                selectedProducts?.let {
                    for ((index, product) in it.withIndex()) {
                        val key = "selectedCarts[$index]"
                        val value = product.id
                        selectedCarts[key] = value
                    }

                }
                val addCartTransaction =AddCartTransaction(
                    userId = userId.toString().toIntOrNull() ?: 0,
                    selectedCarts = selectedCarts
                )
                apiService.addCartTransaction(
                    transaction = addCartTransaction,
                    onSuccess = {
                        println("Cart transaction added successfully")
                        val intent = Intent(this, OrderProcessActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onError = { errorMessage ->

                        println("Error adding cart transaction: $errorMessage")
                    }
                )

            }else{
                val checkoutItem = CheckoutItem(
                    title.toString(),
                    image.toString(),
                    totalPayment,
                    tvQty.text.toString().toIntOrNull() ?: 0,
                    userId.toString().toIntOrNull() ?: 0,
                )
                apiService.addCheckout(checkoutItem,
                    onSuccess = {
                        println("Checkout item added successfully.")
                        val intent = Intent(this, OrderProcessActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onError = { errorMessage ->
                        println("Error adding checkout item: $errorMessage")
                    }
                )


            }

        }


    }

    private fun getLoginResponse(): LoginResponse? {
        return sharedPreferences.getLoginResponse()
    }

}