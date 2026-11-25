package com.proyecto.myapplicationtinkuy.data.repository

import com.proyecto.myapplicationtinkuy.data.local.dao.ProductDao
import com.proyecto.myapplicationtinkuy.data.local.entity.ProductEntity
import com.proyecto.myapplicationtinkuy.data.mapper.seedProducts
import kotlinx.coroutines.flow.Flow

class ProductRepositoryImpl(private val dao: ProductDao) : ProductRepository {

    override fun observeAll(): Flow<List<ProductEntity>> = dao.observeAll()

    override suspend fun getById(id: String): ProductEntity? = dao.getById(id)

    // Carga los 5 productos del enum solo si la tabla está vacía
    override suspend fun seedIfEmpty() {
        if (dao.count() == 0) dao.upsertAll(seedProducts())
    }
}