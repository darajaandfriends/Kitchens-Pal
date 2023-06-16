package com.kitchenspal.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kitchenspal.R
import com.kitchenspal.api.ApiService
import com.kitchenspal.model.Product
import com.kitchenspal.model.cartItem
import com.kitchenspal.response.LoginResponse
import com.kitchenspal.ui.adapter.CartAdapter
import com.kitchenspal.utils.MySharedPreferences

class CartActivity : AppCompatActivity() {

    private lateinit var cartRecycler: RecyclerView
    private lateinit var cartCheckout: AppCompatButton
    private lateinit var cartAdapter: CartAdapter
    private lateinit var btHapusItem: TextView
    private lateinit var sharedPreferences: MySharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartRecycler = findViewById(R.id.cartRecycler)
        cartCheckout = findViewById(R.id.cartCheckout)
        btHapusItem = findViewById(R.id.btDelete)
        cartRecycler.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(this)
        cartRecycler.adapter = cartAdapter

        sharedPreferences = MySharedPreferences(this)

        getCartItems()

        btHapusItem.setOnClickListener {
            removeSelectedItems()
        }

        cartCheckout.setOnClickListener {
            if (isAnyProductChecked()) {
                val selectedProducts = mutableListOf<Product>()
                val cartItems = cartAdapter.getProductList().filter { it.isSelected }
                for (cartItem in cartItems) {
                    if (cartItem.isSelected) {
                        selectedProducts.add(cartItem)
                        Log.d("produk", selectedProducts.toString())
                        val intent = Intent(this, AddressCheckoutActivity::class.java)
                        intent.putParcelableArrayListExtra("selectedProducts", ArrayList(selectedProducts))
                        startActivity(intent)
                    }
                }

            } else {
                Toast.makeText(this, "Belum ada produk yang dipilih", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCartItems() {
        val loginResponse = getLoginResponse()
        val userId = loginResponse?.idUser

        val apiService = ApiService
        apiService.getCartItems(
            userId = userId.toString().toInt(),
            onSuccess = { cartResponse ->
                val data = cartResponse.data
                if (data != null) {
                    cartAdapter.setData(data)
                } else {
                    Log.e("error", "Data is null in cart response")
                }
            },
            onError = { errorMessage ->
                Log.e("error", errorMessage)
            }
        )
    }

    private fun removeSelectedItems() {
        val selectedProducts = cartAdapter.getProductList().filter { it.isSelected }
        if (selectedProducts.isNotEmpty()) {
            for (selectedProduct in selectedProducts) {
                val productId = selectedProduct.id
                val apiService = ApiService
                apiService.removeItemFromCart(productId,
                    onSuccess = {
                        runOnUiThread {
                            cartAdapter.removeItem(selectedProduct)
                            Toast.makeText(this, "Item dihapus dari keranjang", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onError = { error ->
                        runOnUiThread {
                            Toast.makeText(this, "Gagal menghapus item dari keranjang: $error", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        } else {
            Toast.makeText(this, "Belum ada produk yang dipilih", Toast.LENGTH_SHORT).show()
        }
    }


    private fun isAnyProductChecked(): Boolean {
        val cartItems = cartAdapter.getProductList()
        for (cartItem in cartItems) {
            if (cartItem.isSelected) {
                return true
            }
        }
        return false
    }

    private fun getLoginResponse(): LoginResponse? {
        return sharedPreferences.getLoginResponse()
    }
}
