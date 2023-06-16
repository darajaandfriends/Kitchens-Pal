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
import com.kitchenspal.response.LoginResponse
import com.kitchenspal.ui.adapter.CartAdapter
import com.kitchenspal.ui.adapter.HistoryAdapter
import com.kitchenspal.utils.MySharedPreferences

class HistoryOrderActivity : AppCompatActivity() {

    private lateinit var historyRecycler: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var sharedPreferences: MySharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_order)
        historyRecycler = findViewById(R.id.historyRecycler)

        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyAdapter = HistoryAdapter(this)
        historyRecycler.adapter = historyAdapter

        sharedPreferences = MySharedPreferences(this)

        getCartItems()
    }

    private fun getCartItems() {
        val loginResponse = getLoginResponse()
        val userId = loginResponse?.idUser

        val apiService = ApiService
        apiService.getHistoryItems(
            userId = userId.toString().toInt(),
            onSuccess = { historyResponse ->
                val data = historyResponse.data
                if (data != null) {
                    historyAdapter.setData(data)
                } else {
                    Log.e("error", "Data is null in cart response")
                }
            },
            onError = { errorMessage ->
                Log.e("error", errorMessage)
            }
        )
    }


    private fun getLoginResponse(): LoginResponse? {
        return sharedPreferences.getLoginResponse()

    }
}