package com.example.supermarketpetproject.di

import com.example.supermarketpetproject.core.data.coroutines.DefaultDispatchersProvider
import com.example.supermarketpetproject.core.domain.coroutines.DispatchersProvider
import com.example.supermarketpetproject.productlist.data.repositories.ProductRepositoryImp
import com.example.supermarketpetproject.productlist.domain.repositories.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}