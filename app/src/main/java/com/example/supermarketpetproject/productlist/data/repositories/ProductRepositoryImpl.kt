package com.example.supermarketpetproject.productlist.data.repositories

import com.example.supermarketpetproject.core.domain.coroutines.DispatchersProvider
import com.example.supermarketpetproject.productlist.data.local.LocalDataSource
import com.example.supermarketpetproject.productlist.data.local.database.entity.toDomain
import com.example.supermarketpetproject.productlist.data.remote.RemoteDataSource
import com.example.supermarketpetproject.productlist.data.remote.response.toEntity
import com.example.supermarketpetproject.productlist.domain.model.Product
import com.example.supermarketpetproject.productlist.domain.repositories.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    val remoteDataSource: RemoteDataSource,
    val localDataSource: LocalDataSource,
    val dispatchers: DispatchersProvider
) : ProductRepository {
    private val refreshScope = CoroutineScope(SupervisorJob() + dispatchers.io)
    private val refreshMutex = Mutex()

    override fun getProducts(): Flow<List<Product>> {
        return localDataSource.getAllProducts()
            .map { entities ->
                entities.mapNotNull { productEntity ->
                    productEntity.toDomain()
                }
            }
            .onStart {
                refreshScope.launch {
                    if (!refreshMutex.tryLock()) return@launch
                    try {
                        refreshProduct()
                    } catch (_: Exception) {

                    } finally {
                        refreshMutex.unlock()
                    }

                }
            }
            .catch {
                //TODO LOG
            }
    }

    override fun getProductById(id: String): Flow<Product?> {
        return localDataSource.getProductById(id)
            .map { entity ->  entity?.toDomain() }
            .catch { e ->
                //analytic.trackError(e)
            }
    }

    override suspend fun refreshProduct() {
        withContext(dispatchers.io) {
            val products = remoteDataSource.getProducts().getOrThrow()
            val productsEntities = products.map { it.toEntity() }
            localDataSource.saveProducts(productsEntities)
        }
    }
}