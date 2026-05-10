package com.example.supermarketpetproject.core.builders

import com.example.supermarketpetproject.productlist.domain.model.Product

class ProductBuilder {
    private var id: String = "ProductId"
    private var name: String = "ProductName"
    private var description: String = "ProductDescription"
    private var price: Double = 10.0
    private var category: String = "ProductCategory"
    private var stock: Int = 10
    private var imageUrl: String? = null

    fun withId(id: String) = apply { this.id = id }
    fun withName(name: String) = apply { this.name = name }
    fun withDescription(description: String) = apply { this.description = description }
    fun withPrice(price: Double) = apply { this.price = price }
    fun withCategory(category: String) = apply { this.category = category }
    fun withStock(stock: Int) = apply { this.stock = stock }
    fun withImageUrl(imageUrl: String?) = apply { this.imageUrl = imageUrl }

    fun build() = Product(id, name, description, price, category, stock, imageUrl)
}

fun product(block: ProductBuilder.() -> Unit): Product = ProductBuilder().apply(block).build()