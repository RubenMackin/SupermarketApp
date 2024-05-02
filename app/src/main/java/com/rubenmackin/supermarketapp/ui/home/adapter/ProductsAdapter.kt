package com.rubenmackin.supermarketapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rubenmackin.supermarketapp.R
import com.rubenmackin.supermarketapp.domain.model.Product

class ProductsAdapter(private var products : List<Product> = emptyList()): RecyclerView.Adapter<ProductsViewHolder>() {

    fun updateList(listProducts : List<Product>){
        this.products = listProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_products, parent, false)
        return ProductsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        holder.render(products[position])
    }
}