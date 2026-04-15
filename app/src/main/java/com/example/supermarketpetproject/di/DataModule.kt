package com.example.supermarketpetproject.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.supermarketpetproject.cart.data.local.database.dao.CartItemDao
import com.example.supermarketpetproject.core.data.coroutines.DefaultDispatchersProvider
import com.example.supermarketpetproject.core.domain.coroutines.DispatchersProvider
import com.example.supermarketpetproject.core.data.local.database.SupermarketDB
import com.example.supermarketpetproject.productlist.data.local.database.dao.ProductDao
import com.example.supermarketpetproject.productlist.data.local.database.dao.PromotionDao
import com.example.supermarketpetproject.productlist.data.repositories.ProductRepositoryImpl
import com.example.supermarketpetproject.productlist.data.repositories.PromotionsRepositoryImpl
import com.example.supermarketpetproject.productlist.data.repositories.SettingsRepositoryImpl
import com.example.supermarketpetproject.productlist.domain.repositories.ProductRepository
import com.example.supermarketpetproject.productlist.domain.repositories.PromotionsRepository
import com.example.supermarketpetproject.productlist.domain.repositories.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

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
    fun providePromotionsRepository(promotionsRepositoryImpl: PromotionsRepositoryImpl) : PromotionsRepository {
        return promotionsRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideProductRepository(productRepositoryImpl: ProductRepositoryImpl) : ProductRepository {
        return productRepositoryImpl
    }

    @Provides
    fun provideProductDao(database: SupermarketDB): ProductDao = database.productDao()

    @Provides
    fun providePromotionDao(database: SupermarketDB): PromotionDao = database.promotionDao()

    @Provides
    fun provideCartItemDao(database: SupermarketDB): CartItemDao = database.cartItemDao()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SupermarketDB {
        return Room.databaseBuilder(context, SupermarketDB::class.java, "market_db").build()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl) : SettingsRepository {
        return settingsRepositoryImpl
    }
}