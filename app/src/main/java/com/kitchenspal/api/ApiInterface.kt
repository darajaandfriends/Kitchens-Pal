package com.kitchenspal.api

import com.google.gson.JsonObject
import com.kitchenspal.model.AddCartTransaction
import com.kitchenspal.model.CheckoutItem
import com.kitchenspal.model.Ingredients
import com.kitchenspal.model.ResetPasswordRequest
import com.kitchenspal.model.ResetPasswordResponse
import com.kitchenspal.model.UpdateAlamatUser
import com.kitchenspal.model.UpdateImage
import com.kitchenspal.model.UpdateNama
import com.kitchenspal.model.UpdatePassword
import com.kitchenspal.model.UpdatePasswordRequest
import com.kitchenspal.model.UpdatePasswordResponse
import com.kitchenspal.model.cartItem
import com.kitchenspal.request.LoginRequest
import com.kitchenspal.request.RegisterRequest
import com.kitchenspal.response.CartResponse
import com.kitchenspal.response.HistoryResponse
import com.kitchenspal.response.LoginResponse
import com.kitchenspal.response.LogoutResponse
import com.kitchenspal.response.RegisterResponse
import com.kitchenspal.response.TokoResponse
import com.kitchenspal.response.TransactionResponse
import com.kitchenspal.response.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiInterface {
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @PUT("user/{id}")
    fun updateAlamatUser(@Path("id") userId: Int, @Body request: UpdateAlamatUser): Call<Unit>

    @PUT("user/{id}")
    fun updatePasswordUser(@Path("id") userId: Int, @Body request: UpdatePassword): Call<Unit>
    @PUT("user/{id}")
    fun updateNamaUser(@Path("id") userId: Int, @Body request: UpdateNama): Call<Unit>

    @Multipart
    @PUT("user/{id}")
    fun uploadImageById(
        @Path("id") userId: Int,
        @Part image: MultipartBody.Part
    ): Call<Unit>

    @GET("cart")
    fun getCartItems(@Query("id_user") userId: Int): Call<CartResponse>

    @GET("cart/riwayat-pembelian")
    fun getHistoriItems(@Query("id_user") userId: Int): Call<HistoryResponse>


    @GET("toko")
    fun getAllToko(): Call<TokoResponse>

    @POST("cart/transaction")
    fun addCartTransaction(@Body transaction: AddCartTransaction): Call<TransactionResponse>

    @POST("cart/ceckout")
    fun addCheckout(@Body checkoutItem: CheckoutItem): Call<Unit>

    @POST("cart")
    fun addItemToCart(@Body cartItem: cartItem): Call<Unit>

    @DELETE("cart/{Id}")
    fun removeItemFromCart(@Path("Id") itemId: Int): Call<Unit>

    @GET("user/{id}")
    fun getUserById(@Path("id") userId: Int): Call<UserResponse>

    @GET("searching/ingredient")
    fun searchIngredient(@Query("keyword") keyword: String): Call<JsonObject>

    @POST("logout")
    fun logout(@Header("Authorization") token: String): Call<LogoutResponse>

    @POST("reset-password")
    fun requestResetPassword(
        @Body request: ResetPasswordRequest
    ): Call<ResetPasswordResponse>

    @PUT("reset-password/{token}")
    fun resetPassword(
        @Path("token") token: String,
        @Body request: UpdatePasswordRequest
    ): Call<UpdatePasswordResponse>

    @GET("ingredient")
    fun getIngredients(): Call<JsonObject>

    @GET("recipe")
    fun getRecipes(): Call<JsonObject>
}