package com.example.supermarketpetproject.productlist.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.supermarketpetproject.productlist.domain.model.Product

@Entity(tableName = "products")
data class ProductEntity (
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val category: String?,
    val stock: Int?,
    val imageUrl: String? = null
)

fun ProductEntity.toDomain(): Product? {
    if (category.isNullOrEmpty()) return null
    return Product(
        id = id,
        name = name,
        description = description.orEmpty(),
        price = price,
        category = category,
        stock = stock ?: 0,
        imageUrl = imageUrl
    )
}