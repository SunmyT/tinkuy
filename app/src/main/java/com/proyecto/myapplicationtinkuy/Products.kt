package com.proyecto.myapplicationtinkuy

import java.text.Normalizer
import java.util.Locale

private val combiningMarks = Regex("\\p{M}+")
private val whitespace = Regex("\\s+")

private fun String.searchable(): String =
    Normalizer.normalize(lowercase(Locale.ROOT), Normalizer.Form.NFD).replace(combiningMarks, "")

internal enum class Category(val label: String) {
    All("Todos"),
    Textiles("Textiles"),
    Ceramics("Cerámica"),
    Food("Alimentos");

    fun products(query: String): List<Product> {
        val terms = query.trim().searchable().split(whitespace).filter(String::isNotEmpty)
        return Product.entries.filter { product ->
            (this == All || product.category == this) && terms.all { it in product.searchText }
        }
    }
}

internal enum class Product(
    val title: String,
    val producer: String,
    val region: String,
    val unitPrice: Int,
    val category: Category?,
    val description: String,
    val photo: Int
) {
    Alpaca("Tejido de Alpaca", "Tejidos Killa", "Cusco", 85, Category.Textiles,
        "Prenda artesanal de fibra de alpaca, tejida a mano.", R.drawable.product_alpaca),
    Ceramic("Cerámica de Chulucanas", "Cerámicas Rumi", "Piura", 80, Category.Ceramics,
        "Figuras decorativas de la tradición alfarera de Chulucanas.", R.drawable.product_ceramic),
    Sheep("Tejido de Oveja", "Tejidos Killa", "Cusco", 69, Category.Textiles,
        "Prenda de lana de oveja tejida a mano.", R.drawable.product_wool),
    Retablo("Retablo ayacuchano", "Artesanía de Huamanga", "Ayacucho", 120, null,
        "Retablo artesanal con figuras pintadas a mano.", R.drawable.product_retablo),
    Coffee("Café peruano en grano", "Productores de café", "Perú", 28, Category.Food,
        "Granos verdes de café peruano para tostar.", R.drawable.product_coffee);

    val maker: String get() = "$producer · $region"
    val price: String get() = soles(unitPrice)
    val searchText: String = listOf(title, producer, region, category?.label.orEmpty(), description)
        .joinToString(" ").searchable()
}

internal fun soles(amount: Int): String = "S/ $amount.00"
