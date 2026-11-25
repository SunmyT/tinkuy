package com.proyecto.myapplicationtinkuy.data.mapper

import androidx.annotation.DrawableRes
import com.proyecto.myapplicationtinkuy.Product
import com.proyecto.myapplicationtinkuy.R
import com.proyecto.myapplicationtinkuy.data.local.entity.ProductEntity

internal fun Product.toEntity() = ProductEntity(
    id = name,
    title = title,
    producer = producer,
    region = region,
    unitPrice = unitPrice,
    category = category?.name,
    description = description,
    photoName = name // la clave es el id del producto
)

internal fun seedProducts(): List<ProductEntity> =
    Product.entries.map { it.toEntity() }

@DrawableRes
internal fun ProductEntity.photoRes(): Int = when (id) {
    Product.Alpaca.name -> R.drawable.product_alpaca
    Product.Ceramic.name -> R.drawable.product_ceramic
    Product.Sheep.name -> R.drawable.product_wool
    Product.Retablo.name -> R.drawable.product_retablo
    Product.Coffee.name -> R.drawable.product_coffee
    else -> R.drawable.product_alpaca
}