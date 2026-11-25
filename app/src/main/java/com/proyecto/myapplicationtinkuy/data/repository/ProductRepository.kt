package com.proyecto.myapplicationtinkuy.data.repository

import com.proyecto.myapplicationtinkuy.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeAll(): Flow<List<ProductEntity>>
    suspend fun getById(id: String): ProductEntity?
    suspend fun seedIfEmpty()
}