package com.kitchenspal.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.kitchenspal.R
import com.kitchenspal.api.ApiService
import com.kitchenspal.model.cartItem
import com.kitchenspal.response.LoginResponse
import com.kitchenspal.utils.MySharedPreferences

class RecipeDetailsActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var deskripsiTextView: TextView
    private lateinit var ingredientTextView: TextView
    private lateinit var plusButton: TextView
    private lateinit var minusButton: TextView
    private lateinit var sharedPreferences: MySharedPreferences
    private lateinit var quantityTextView: TextView
    private var quantity: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        sharedPreferences = MySharedPreferences(this)
        val title = intent.getStringExtra("title").toString()
        val image = intent.getStringExtra("image").toString()
        val harga = intent.getIntExtra("harga", 0)
        val deskripsi = intent.getStringExtra("deskripsi")


        imageView = findViewById(R.id.imageRecipeDetail)
        deskripsiTextView = findViewById(R.id.deskripsiRecipeDetail)
        ingredientTextView = findViewById(R.id.ingredientRecipeDetail)
        plusButton = findViewById(R.id.plusButton)
        minusButton = findViewById(R.id.minButton)
        quantityTextView = findViewById(R.id.quantityTextView)

        deskripsiTextView.text = deskripsi
        Glide.with(this)
            .load(image)
            .into(imageView)

        Quanty()

        val pesanRecipeButton: AppCompatButton = findViewById(R.id.pesanRecipe)
        val addCartRecipeButton: AppCompatButton = findViewById(R.id.addCartRecipe)

        pesanRecipeButton.setOnClickListener {
            val intent = Intent(this, AddressCheckoutActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("image", image)
            intent.putExtra("harga", harga)
            intent.putExtra("qty", quantityTextView.text.toString().toInt())
            startActivity(intent)
        }

        addCartRecipeButton.setOnClickListener {
            addItemToCart()
        }


    }
    private fun Quanty(){
        plusButton.setOnClickListener {
            quantity++
            updateQuantity()
        }

        minusButton.setOnClickListener {
            if (quantity > 0) {
                quantity--
                updateQuantity()
            }
        }
    }
    private fun updateQuantity() {
        quantityTextView.text = quantity.toString()
    }
    private fun addItemToCart() {
        val apiService = ApiService
        val loginResponse = getLoginResponse()
        val userId = loginResponse?.idUser
        val title = intent.getStringExtra("title").toString()
        val image = intent.getStringExtra("image").toString()
        val harga = intent.getIntExtra("harga", 0)
        val product = cartItem(title, image, harga, quantity, userId.toString().toInt())

        apiService.addItemToCart(product,
            onSuccess = {
                Toast.makeText(this, "Item berhasil ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
            },
            onError = { error ->
                Toast.makeText(this, "Gagal: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }
    private fun getLoginResponse(): LoginResponse? {
        return sharedPreferences.getLoginResponse()
    }


}