package com.example.supermarketpetproject.productlist.data.remote.response

import com.example.supermarketpetproject.productlist.data.local.database.entity.ProductEntity
import com.example.supermarketpetproject.productlist.domain.model.Product
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductsResponse(
    @SerialName("products") val products: List<ProductResponse>
)

@Serializable
data class ProductResponse(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("priceCents") val priceCents: Int? = null,
    @SerialName("category") val category: String? = null,
    @SerialName("stock") val stock: Int? = null,
    @SerialName("imageUrl") val imageUrl: String? = null
)

fun ProductResponse.toEntity(): ProductEntity {
    val finalPrice = priceCents?.toDouble()?.div(100.0) ?: 0.0
    return ProductEntity(
        id = id,
        name = name,
        description = description,
        price = finalPrice,
        category = category,
        stock = stock,
        imageUrl = imageUrl
    )
}
