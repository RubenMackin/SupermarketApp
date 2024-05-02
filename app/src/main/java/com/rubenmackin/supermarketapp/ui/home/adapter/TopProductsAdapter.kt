package com.rubenmackin.supermarketapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rubenmackin.supermarketapp.R
import com.rubenmackin.supermarketapp.domain.model.Product

class TopProductsAdapter (private var products:List<Product> = emptyList()) : RecyclerView.Adapter<TopProductsViewHolder>() {

    fun updateList(listProducts : List<Product>){
        this.products = listProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopProductsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_top_product, parent, false)
        return TopProductsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: TopProductsViewHolder, position: Int) {
        holder.render(products[position])
    }
}