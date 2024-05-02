package com.rubenmackin.supermarketapp.ui.home.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rubenmackin.supermarketapp.databinding.ItemTopProductBinding
import com.rubenmackin.supermarketapp.domain.model.Product

class TopProductsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemTopProductBinding.bind(view)

    fun render(product: Product) {
        binding.apply {
            tvTitle.text = product.title
            Glide.with(tvTitle.context).load(product.imageUrl).into(ivTopProduct)
        }
    }
}