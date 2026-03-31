package com.example.supermarketpetproject.productlist.data.remote

import com.example.supermarketpetproject.productlist.data.remote.response.ProductsResponse
import com.example.supermarketpetproject.productlist.data.remote.response.PromotionsResponse
import retrofit2.http.GET

interface SupermarketApiService {
    @GET("data/products.json")
    suspend fun getProducts(): ProductsResponse

    @GET("data/promotions.json")
    suspend fun getPromotions(): PromotionsResponse
}