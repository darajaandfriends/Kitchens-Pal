package com.kitchenspal.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.kitchenspal.R
import com.kitchenspal.api.ApiService
import com.kitchenspal.model.Product
import com.kitchenspal.model.cartItem
import com.kitchenspal.response.LoginResponse
import com.kitchenspal.utils.MySharedPreferences

class IngredientDetailsActivity : AppCompatActivity() {

    private lateinit var imageIngredientDetail: ImageView
    private lateinit var titleIngredientDetail: TextView
    private lateinit var deskripsiIngredientDetail: TextView
    private lateinit var quantityTextView: TextView
    private lateinit var sharedPreferences: MySharedPreferences
    private lateinit var plusButton: TextView
    private lateinit var minusButton: TextView
    private var quantity: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient_details)

        val title = intent.getStringExtra("title")
        val image = intent.getStringExtra("image")
        val deskripsi = intent.getStringExtra("deskripsi")
        val harga = intent.getIntExtra("harga", 0)

        imageIngredientDetail = findViewById(R.id.imageIngredientDetail)
        titleIngredientDetail = findViewById(R.id.titleIngredientDetail)
        deskripsiIngredientDetail = findViewById(R.id.deskripsiIngredientDetail)
        quantityTextView = findViewById(R.id.quantityIngredients)
        plusButton = findViewById(R.id.plusButton)
        minusButton = findViewById(R.id.minButton)
        sharedPreferences = MySharedPreferences(this)

        titleIngredientDetail.text = title
        deskripsiIngredientDetail.text = deskripsi

        Glide.with(this)
            .load(image)
            .into(imageIngredientDetail)

        setupQuantity()

        val pesanIngredientsButton: AppCompatButton = findViewById(R.id.pesanIngredients)
        val addCartIngredientsButton: AppCompatButton = findViewById(R.id.addCartIngredients)

        pesanIngredientsButton.setOnClickListener {
            val intent = Intent(this, AddressCheckoutActivity::class.java)
            intent.putExtra("title", titleIngredientDetail.text)
            intent.putExtra("image", image)
            intent.putExtra("harga", harga)
            intent.putExtra("qty", quantityTextView.text.toString().toInt())
            startActivity(intent)
        }

        addCartIngredientsButton.setOnClickListener {
            addItemToCart()
        }
    }

    private fun setupQuantity() {
        plusButton.setOnClickListener {
            quantity++
            updateQuantity()
        }

        minusButton.setOnClickListener {
            if (quantity > 100) {
                quantity--
                updateQuantity()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateQuantity() {
        quantityTextView.text = "$quantity gr"
    }


    private fun addItemToCart() {
        val apiService = ApiService
        val loginResponse = getLoginResponse()
        val userId = loginResponse?.idUser
        val title = intent.getStringExtra("title").toString()
        val image = intent.getStringExtra("image").toString()
        val harga = intent.getIntExtra("harga", 0)
        val product = cartItem(
            title,
            image, harga, quantityTextView.text.toString().toInt(), userId.toString().toInt())

        apiService.addItemToCart(product,
            onSuccess = {
                Toast.makeText(this, "Item berhasil ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
            },
            onError = { error ->
                Toast.makeText(this, "Gagal: $error", Toast.LENGTH_LONG).show()
            }
        )
    }
    private fun getLoginResponse(): LoginResponse? {
        return sharedPreferences.getLoginResponse()
    }
}
