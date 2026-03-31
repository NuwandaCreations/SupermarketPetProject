package com.example.supermarketpetproject.productlist.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.supermarketpetproject.productlist.data.local.database.dao.ProductDao
import com.example.supermarketpetproject.productlist.data.local.database.dao.PromotionDao
import com.example.supermarketpetproject.productlist.data.local.database.entity.ProductEntity
import com.example.supermarketpetproject.productlist.data.local.database.entity.PromotionEntity

@Database(
    entities = [ProductEntity::class, PromotionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SupermarketDB : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun promotionDao(): PromotionDao
}