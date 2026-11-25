package com.proyecto.myapplicationtinkuy.data.mapper

import android.content.Context
import com.proyecto.myapplicationtinkuy.Product
import com.proyecto.myapplicationtinkuy.data.local.entity.ProductEntity

/** Convierte el enum actual en fila de base de datos (sirve para la carga inicial). */
internal fun Product.toEntity(context: Context) = ProductEntity(
    id = name,
    title = title,
    producer = producer,
    region = region,
    unitPrice = unitPrice,
    category = category?.name,
    description = description,
    photoName = context.resources.getResourceEntryName(photo)
)

/** Lista completa para sembrar la base la primera vez. */
internal fun seedProducts(context: Context): List<ProductEntity> =
    Product.entries.map { it.toEntity(context) }

/** Obtiene el id del drawable a partir del nombre guardado. */
fun ProductEntity.photoRes(context: Context): Int =
    context.resources.getIdentifier(photoName, "drawable", context.packageName)