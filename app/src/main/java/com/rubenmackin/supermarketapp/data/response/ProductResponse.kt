package com.rubenmackin.supermarketapp.data.response

import com.rubenmackin.supermarketapp.domain.model.Product

data class ProductResponse (
    val id: String = "",
    val image: String = "",
    val name: String = "",
    val description: String = "",
    val price: String = ""
){
    fun toDomain():Product{
        return Product(
            id = id,
            imageUrl = image,
            title = name,
            description = description,
            price = price
        )
    }
}