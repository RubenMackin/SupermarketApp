package com.rubenmackin.supermarketapp.ui.home.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rubenmackin.supermarketapp.databinding.ItemProductsBinding
import com.rubenmackin.supermarketapp.domain.model.Product

class ProductsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val binding = ItemProductsBinding.bind(view)

    fun render(product: Product){
        binding.apply {
            Glide.with(binding.tvTitle.context).load(product.imageUrl).into(binding.ivProduct)
            tvTitle.text = product.title
            tvDescription.text = product.description
            tvPrice.text = "${product.price} $"
        }
    }
}