package com.proyecto.myapplicationtinkuy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val title: String,
    val producer: String,
    val region: String,
    val unitPrice: Int,
    val category: String?,      // nombre de Category, o null (Retablo)
    val description: String,
    val photoName: String       // nombre del drawable, ej. "product_alpaca"
)