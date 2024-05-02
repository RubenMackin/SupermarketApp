package com.rubenmackin.supermarketapp.ui.home

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.rubenmackin.supermarketapp.R
import com.rubenmackin.supermarketapp.databinding.ActivityHomeBinding
import com.rubenmackin.supermarketapp.domain.model.Product
import com.rubenmackin.supermarketapp.ui.addproduct.AddProductActivity
import com.rubenmackin.supermarketapp.ui.home.adapter.ProductsAdapter
import com.rubenmackin.supermarketapp.ui.home.adapter.SpacingDecorator
import com.rubenmackin.supermarketapp.ui.home.adapter.TopProductsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    //private val homeViewModel by viewModels<HomeViewModel>()
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var topProductsAdapter: TopProductsAdapter

    private val addProductLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
        if (result.resultCode == Activity.RESULT_OK){
            homeViewModel.getData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        initUI()
    }

    private fun initUI() {
        initShimmers()
        initListeners()
        initList()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.uiState.collect { state ->
                    renderLastProduct(state.lastProduct)
                    renderTopProducts(state.topProduct)
                    renderProducts(state.products)
                }
            }
        }
    }

    private fun initShimmers() {
        binding.viewLastProductShimmer.root.startShimmer()
        binding.shimmerTopProduct.startShimmer()
    }

    private fun initList() {
        productsAdapter = ProductsAdapter()
        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(SpacingDecorator(16))
            adapter = productsAdapter
        }

        topProductsAdapter = TopProductsAdapter()
        binding.rvTopProduct.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = topProductsAdapter
        }
    }

    private fun initListeners() {
        binding.viewToolbar.tvAddProduct.setOnClickListener {
            addProductLauncher.launch(AddProductActivity.create(this))
        }
    }

    private fun renderProducts(products: List<Product>) {
        productsAdapter.updateList(products)
    }

    private fun renderTopProducts(topProduct: List<Product>) {
        if (topProduct.isEmpty()) return

        topProductsAdapter.updateList(topProduct)
        binding.rvTopProduct.isVisible = true
        binding.shimmerTopProduct.isVisible = false
        binding.shimmerTopProduct.stopShimmer()
    }

    private fun renderLastProduct(lastProduct: Product?) {
        if (lastProduct == null) return
        binding.viewLastProduct.tvTitle.text = lastProduct.title
        binding.viewLastProduct.tvDescription.text = lastProduct.description
        Glide.with(this).load(lastProduct.imageUrl).placeholder(R.drawable.ic_placeholder)
            .into(binding.viewLastProduct.ivLastProduct)

        binding.viewLastProduct.root.isVisible = true
        binding.viewLastProductShimmer.root.stopShimmer()
    }
}