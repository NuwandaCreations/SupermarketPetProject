package com.example.supermarketpetproject.productlist.data.repositories

import com.example.supermarketpetproject.core.domain.coroutines.DispatchersProvider
import com.example.supermarketpetproject.productlist.data.local.LocalDataSource
import com.example.supermarketpetproject.productlist.data.local.database.entity.PromotionEntity
import com.example.supermarketpetproject.productlist.data.local.database.entity.toDomain
import com.example.supermarketpetproject.productlist.data.remote.RemoteDataSource
import com.example.supermarketpetproject.productlist.data.remote.response.toEntity
import com.example.supermarketpetproject.productlist.domain.model.Promotion
import com.example.supermarketpetproject.productlist.domain.repositories.PromotionsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class PromotionsRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val dispatchers: DispatchersProvider,
    private val json: Json
): PromotionsRepository {
    private val refreshScope = CoroutineScope(SupervisorJob() + dispatchers.io)
    private val refreshMutex = Mutex()

    override fun getActivePromotions(): Flow<List<Promotion>> {
        return localDataSource.getAllPromotions()
            .map { entities ->
                entities.mapNotNull { promotionEntity ->
                    promotionEntity.toDomain(json)
                }
            }
            .onStart {
                refreshScope.launch {
                    if (!refreshMutex.tryLock()) return@launch
                    try {
                        refreshPromotions()
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

    override suspend fun refreshPromotions() {
        withContext(dispatchers.io) {
            val promotions = remoteDataSource.getPromotions().getOrThrow()
            val promotionsEntity: List<PromotionEntity> = promotions.mapNotNull { it.toEntity(json) }
            localDataSource.savePromotions(promotionsEntity)
        }
    }
}