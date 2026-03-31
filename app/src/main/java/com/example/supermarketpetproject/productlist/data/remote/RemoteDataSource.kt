package com.example.supermarketpetproject.productlist.data.remote

import androidx.datastore.core.IOException
import com.example.supermarketpetproject.core.domain.model.AppError
import com.example.supermarketpetproject.productlist.data.remote.response.ProductResponse
import com.example.supermarketpetproject.productlist.data.remote.response.PromotionResponse
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class RemoteDataSource @Inject constructor(val supermaketApiService: SupermarketApiService) {
    suspend fun getProducts(): Result<List<ProductResponse>> {
        return try {
            val response = supermaketApiService.getProducts()
            Result.success(response.products)
        } catch (e: Exception) {
            Result.failure(mapToDomainError(e))
        }
    }

    suspend fun getPromotions(): Result<List<PromotionResponse>> {
        return try {
            val response = supermaketApiService.getPromotions()
            Result.success(response.promotions)
        } catch (e: Exception) {
            Result.failure(mapToDomainError(e))
        }
    }

    private fun mapToDomainError(e: Exception): AppError {
        return when (e) {
            is UnknownHostException -> AppError.NetworkError
            is SocketTimeoutException -> AppError.NetworkError
            is IOException -> AppError.NetworkError
            is HttpException -> {
                when (e.code()) {
                    404 -> AppError.NotFoundError
                    else -> AppError.NetworkError
                }
            }
            else -> AppError.UnknownError(e.message)
        }
    }
}