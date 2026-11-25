package com.proyecto.myapplicationtinkuy.ui.products

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.myapplicationtinkuy.data.local.AppDatabase
import com.proyecto.myapplicationtinkuy.data.local.entity.ProductEntity
import com.proyecto.myapplicationtinkuy.data.repository.ProductRepository
import com.proyecto.myapplicationtinkuy.data.repository.ProductRepositoryImpl
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel(app: Application) : AndroidViewModel(app) {

    private val repository: ProductRepository =
        ProductRepositoryImpl(AppDatabase.get(app).productDao())

    val products: StateFlow<List<ProductEntity>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch { repository.seedIfEmpty() }
    }
}