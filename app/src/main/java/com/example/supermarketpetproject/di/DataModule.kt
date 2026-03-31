package com.example.supermarketpetproject.di

import android.content.Context
import androidx.room.Room
import com.example.supermarketpetproject.core.data.coroutines.DefaultDispatchersProvider
import com.example.supermarketpetproject.core.domain.coroutines.DispatchersProvider
import com.example.supermarketpetproject.productlist.data.local.database.SupermarketDB
import com.example.supermarketpetproject.productlist.data.local.database.dao.ProductDao
import com.example.supermarketpetproject.productlist.data.local.database.dao.PromotionDao
import com.example.supermarketpetproject.productlist.data.repositories.ProductRepositoryImp
import com.example.supermarketpetproject.productlist.domain.repositories.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDispatchersProvider(defaultDispatchersProvider: DefaultDispatchersProvider): DispatchersProvider {
        return defaultDispatchersProvider
    }

    @Provides
    @Singleton
    fun provideProductRepository(productRepositoryImp: ProductRepositoryImp) : ProductRepository {
        return productRepositoryImp
    }

    @Provides
    fun provideProductDao(database: SupermarketDB): ProductDao = database.productDao()

    @Provides
    fun providePromotionDao(database: SupermarketDB): PromotionDao = database.promotionDao()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SupermarketDB {
        return Room.databaseBuilder(context, SupermarketDB::class.java, "market_db").build()
    }
}