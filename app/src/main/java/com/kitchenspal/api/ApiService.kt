package com.kitchenspal.api

import android.util.Log
import com.google.gson.JsonObject
import com.kitchenspal.model.AddCartTransaction
import com.kitchenspal.model.CheckoutItem
import com.kitchenspal.model.Ingredients
import com.kitchenspal.model.Recipe
import com.kitchenspal.model.ResetPasswordRequest
import com.kitchenspal.model.ResetPasswordResponse
import com.kitchenspal.model.TokoItem
import com.kitchenspal.model.UpdateAlamatUser
import com.kitchenspal.model.UpdateNama
import com.kitchenspal.model.UpdatePassword
import com.kitchenspal.model.UpdatePasswordRequest
import com.kitchenspal.model.UpdatePasswordResponse
import com.kitchenspal.model.cartItem
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.kitchenspal.response.RegisterResponse
import com.kitchenspal.response.LoginResponse
import com.kitchenspal.request.LoginRequest
import com.kitchenspal.request.RegisterRequest
import com.kitchenspal.response.CartResponse
import com.kitchenspal.response.HistoryResponse
import com.kitchenspal.response.LogoutResponse
import com.kitchenspal.response.TokoResponse
import com.kitchenspal.response.TransactionResponse
import com.kitchenspal.response.UserResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object ApiService {
    private const val BASE_URL = "http://34.128.121.71/api/v1/auth/"

    fun login(
        email: String,
        password: String,
        onSuccess: (LoginResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)

        val loginRequest = LoginRequest(email, password)

        val call = apiInterface.login(loginRequest)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        onSuccess(loginResponse)

                    } else {
                        onError("Empty response body")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody.isNullOrEmpty()) {
                        "Unknown error"
                    } else {
                        try {
                            val errorJson = JSONObject(errorBody)
                            errorJson.getString("message")
                        } catch (e: JSONException) {
                            "Unknown error"
                        }
                    }
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onError("Request failed: " + t.message)
            }
        })
    }

    fun register(
        nama: String,
        email: String,
        password: String,
        latitude: String,
        longitude: String,
        alamat: String,
        detail: String,
        onSuccess: (RegisterResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)

        val registerRequest = RegisterRequest(nama, email, password, latitude, longitude, alamat, detail)

        val call = apiInterface.register(registerRequest)
        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                callRegister: Call<RegisterResponse>,
                responseRegister: Response<RegisterResponse>
            ) {
                if (responseRegister.isSuccessful) {
                    val registerResponse = responseRegister.body()
                    if (registerResponse != null) {
                        onSuccess(registerResponse)
                    } else {
                        onError("Empty response body")
                    }
                } else {
                    val errorBody = responseRegister.errorBody()?.string()
                    val errorMessage = if (errorBody.isNullOrEmpty()) {
                        "Unknown error"
                    } else {
                        try {
                            val errorJson = JSONObject(errorBody)
                            errorJson.getString("message")
                        } catch (e: JSONException) {
                            "Unknown error"
                        }
                    }
                    Log.e("Register", errorMessage)
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                val errorMessage = "Request failed: " + t.message
                Log.e("Register", errorMessage)
                onError(errorMessage)
            }
        })
    }
    fun updateAlamatUser(
        userId: Int,
        request: UpdateAlamatUser,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://34.128.121.71/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiInterface::class.java)

        val call = apiService.updateAlamatUser(userId, request)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody.isNullOrEmpty()) {
                        "Unknown error"
                    } else {
                        try {
                            val errorJson = JSONObject(errorBody)
                            errorJson.getString("message")
                        } catch (e: JSONException) {
                            "Unknown error"
                        }
                    }
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onError("Request failed: ${t.message}")
            }
        })
    }
    fun updatePasswordUser(
        userId: Int,
        request: UpdatePassword,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://34.128.121.71/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiInterface::class.java)

        val call = apiService.updatePasswordUser(userId, request)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody.isNullOrEmpty()) {
                        "Unknown error"
                    } else {
                        try {
                            val errorJson = JSONObject(errorBody)
                            errorJson.getString("message")
                        } catch (e: JSONException) {
                            "Unknown error"
                        }
                    }
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onError("Request failed: ${t.message}")
            }
        })
    }

    fun updateNamaUser(
        userId: Int,
        request: UpdateNama,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://34.128.121.71/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiInterface::class.java)

        val call = apiService.updateNamaUser(userId, request)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody.isNullOrEmpty()) {
                        "Unknown error"
                    } else {
                        try {
                            val errorJson = JSONObject(errorBody)
                            errorJson.getString("message")
                        } catch (e: JSONException) {
                            "Unknown error"
                        }
                    }
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onError("Request failed: ${t.message}")
            }
        })
    }

    fun uploadImageById(
        userId: Int,
        imageFile: File,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://34.128.121.71/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiInterface::class.java)

        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        val call = apiService.uploadImageById(userId, imagePart)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody.isNullOrEmpty()) {
                        "Unknown error"
                    } else {
                        try {
                            val errorJson = JSONObject(errorBody)
                            errorJson.getString("message")
                        } catch (e: JSONException) {
                            "Unknown error"
                        }
                    }
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onError("Request failed: ${t.message}")
            }
        })
    }


    fun getUserById(
        userId: Int,
        onSuccess: (UserResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://34.128.121.71/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiInterface::class.java)

        val call = apiService.getUserById(userId)
        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    if (userResponse != null) {
                        onSuccess(userResponse)
                    } else {
                        onError("Empty response body")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody.isNullOrEmpty()) {
                        "Unknown error"
                    } else {
                        try {
                            val errorJson = JSONObject(errorBody)
                            errorJson.getString("message")
                        } catch (e: JSONException) {
                            "Unknown error"
                        }
                    }
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                onError("Request failed: ${t.message}")
            }
        })
    }

    fun getAllToko(
        onSuccess: (List<TokoItem>) -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://34.128.121.71/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)

        val call = apiInterface.getAllToko()
        call.enqueue(object : Callback<TokoResponse> {
            override fun onResponse(call: Call<TokoResponse>, response: Response<TokoResponse>) {
                if (response.isSuccessful) {
                    val tokoResponse = response.body()
                    val tokoItemList = tokoResponse?.tokoList
                    if (tokoItemList != null) {
                        onSuccess(tokoItemList)
                    } else {
                        onError("Failed to parse response")
                    }
                } else {
                    onError("Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TokoResponse>, t: Throwable) {
                onError(t.message ?: "Request failed")
            }
        })
    }


    fun logout(
        token: String,
        onSuccess: (LogoutResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        val call = apiInterface.logout("Bearer $token")
        call.enqueue(object : Callback<LogoutResponse> {
            override fun onResponse(
                call: Call<LogoutResponse>,
                response: Response<LogoutResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        onSuccess(responseBody)
                    } else {
                        onError("Empty response body")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                onError("Request failed: ${t.message}")
            }
        })
    }


    fun requestResetPassword(
        request: ResetPasswordRequest,
        onSuccess: (ResetPasswordResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        val call = apiInterface.requestResetPassword(request)
        call.enqueue(object : Callback<ResetPasswordResponse> {
            override fun onResponse(
                call: Call<ResetPasswordResponse>,
                response: Response<ResetPasswordResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        onSuccess(responseBody)
                    } else {
                        onError("Empty response body")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
                onError("Request failed: ${t.message}")
            }
        })
    }

    fun addItemToCart(
        cartItem: cartItem,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://34.128.121.71/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        apiInterface.addItemToCart(cartItem).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onError(t.message ?: "Request failed")
            }
        })
    }
    fun addCheckout(
        checkoutItem: CheckoutItem,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://34.128.121.71/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        apiInterface.addCheckout(checkoutItem).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onError(t.message ?: "Request failed")
            }
        })
    }

    fun addCartTransaction(
        transaction: AddCartTransaction,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://34.128.121.71/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        apiInterface.addCartTransaction(transaction).enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(call: Call<TransactionResponse>, response: Response<TransactionResponse>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                onError(t.message ?: "Request failed")
            }
        })
    }

    fun removeItemFromCart(
        itemId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://34.128.121.71/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        apiInterface.removeItemFromCart(itemId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onError(t.message ?: "Request failed")
            }
        })
    }

    fun getCartItems(
        userId: Int,
        onSuccess: (CartResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://34.128.121.71/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiInterface::class.java)

        val call = apiService.getCartItems(userId)
        call.enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                if (response.isSuccessful) {
                    val cartResponse = response.body()
                    if (cartResponse != null) {
                        onSuccess(cartResponse)
                    } else {
                        onError("Empty response body")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody.isNullOrEmpty()) {
                        "Unknown error"
                    } else {
                        try {
                            val errorJson = JSONObject(errorBody)
                            errorJson.getString("message")
                        } catch (e: JSONException) {
                            "Unknown error"
                        }
                    }
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                onError("Request failed: ${t.message}")
            }
        })
    }

    fun getHistoryItems(
        userId: Int,
        onSuccess: (HistoryResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://34.128.121.71/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiInterface::class.java)

        val call = apiService.getHistoriItems(userId)
        call.enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(call: Call<HistoryResponse>, response: Response<HistoryResponse>) {
                if (response.isSuccessful) {
                    val historyResponse = response.body()
                    if (historyResponse != null) {
                        onSuccess(historyResponse)
                    } else {
                        onError("Empty response body")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody.isNullOrEmpty()) {
                        "Unknown error"
                    } else {
                        try {
                            val errorJson = JSONObject(errorBody)
                            errorJson.getString("message")
                        } catch (e: JSONException) {
                            "Unknown error"
                        }
                    }
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                onError("Request failed: ${t.message}")
            }
        })
    }

    fun resetPassword(
        token: String,
        request: UpdatePasswordRequest,
        onSuccess: (UpdatePasswordResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        val call = apiInterface.resetPassword(token, request)
        call.enqueue(object : Callback<UpdatePasswordResponse> {
            override fun onResponse(
                call: Call<UpdatePasswordResponse>,
                response: Response<UpdatePasswordResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        onSuccess(responseBody)
                    } else {
                        onError("Empty response body")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<UpdatePasswordResponse>, t: Throwable) {
                onError("Request failed: ${t.message}")
            }
        })
    }

    fun ingredients(
        onSuccess: (List<Ingredients>) -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://34.128.121.71/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        val call = apiInterface.getIngredients()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        try {
                            val jsonObject = JSONObject(responseBody.toString())
                            val rows = jsonObject.getJSONArray("rows")

                            val ingredientList = mutableListOf<Ingredients>()

                            for (i in 0 until rows.length()) {
                                val item = rows.getJSONObject(i)
                                val id = item.getInt("id")
                                val ingredientName = item.getString("ingredient_nama")
                                val ingredientJenis= item.getString("ingredient_jenis")
                                val ingredientImage= item.getString("ingredient_image")
                                val ingredientDeskripsi= item.getString("ingredient_deskripsi")
                                val ingredientHarga = item.getInt("ingredient_harga")

                                val ingredient = Ingredients(
                                    id,
                                    ingredientName,
                                    ingredientJenis,
                                    ingredientImage,
                                    ingredientDeskripsi,
                                    ingredientHarga
                                )
                                ingredientList.add(ingredient)
                            }

                            onSuccess(ingredientList)
                        } catch (e: JSONException) {
                            onError("Failed to parse response")
                        }
                    } else {
                        onError("Empty response body")
                    }
                } else {
                    val errorMessage = "Response code: ${response.code()}"
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                val errorMessage = "Request failed: ${t.message}"
                onError(errorMessage)
            }
        })
    }


    fun recipe(
        onSuccess: (List<Recipe>) -> Unit,
        onError: (String) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://34.128.121.71/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        val call = apiInterface.getRecipes()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        try {
                            val jsonObject = JSONObject(responseBody.toString())
                            val rows = jsonObject.getJSONArray("rows")

                            val recipeList = mutableListOf<Recipe>()

                            for (i in 0 until rows.length()) {
                                val row = rows.getJSONObject(i)
                                val id = row.getInt("id")
                                val recipeNama = row.getString("recipe_nama")
                                val recipeImage = row.getString("recipe_image")
                                val recipeIngredient = row.getString("recipe_ingredients")
                                val recipeDeskripsi = row.getString("recipe_deskripsi")
                                val recipeHarga = row.getInt("recipe_harga")


                                val recipe = Recipe(id, recipeNama, recipeIngredient, recipeImage, recipeDeskripsi, recipeHarga)
                                recipeList.add(recipe)
                            }

                            onSuccess(recipeList)
                        } catch (e: JSONException) {
                            onError("Failed to parse response")
                        }
                    } else {
                        onError("Empty response body")
                    }
                } else {
                    val errorMessage = "Response code: ${response.code()}"
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                val errorMessage = "Request failed: ${t.message}"
                onError(errorMessage)
            }
        })
    }
}