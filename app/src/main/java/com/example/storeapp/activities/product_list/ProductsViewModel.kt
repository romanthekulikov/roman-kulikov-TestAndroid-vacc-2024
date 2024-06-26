package com.example.storeapp.activities.product_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.domain.Repository
import com.example.domain.entity.Product
import com.example.storeapp.StoreApp
import javax.inject.Inject

class ProductsViewModel : ViewModel() {
    @Inject
    lateinit var repository: Repository

    var productList: MutableLiveData<List<Product>> = MutableLiveData(listOf())
        private set
    var currentAmount = MutableLiveData(0.0)
        private set
    var errorLoadDataFlag = MutableLiveData(false)
        private set
    lateinit var selectedProduct: Product
        private set
    var basket = mutableListOf<Product>()
        private set
    var scrollPositionList = 0
        private set

    init {
        StoreApp.appComponent.inject(this)
    }

    suspend fun getData() {
        if (productList.value!!.isEmpty()) {
            productList.postValue(repository.getProductsListFromApi())
            getBasket()
            errorLoadDataFlag.postValue(false)
        }
    }

    suspend fun getBasket() {
        val repoBasket = repository.getProductsListFromDB()
        basket = repoBasket.toMutableList()
        var newAmount = 0.0
        repoBasket.forEach {
            newAmount += it.price * it.count
        }
        currentAmount.postValue(newAmount)
    }

    suspend fun moveToBasket(product: Product) {
        currentAmount.postValue(currentAmount.value!! + product.price)
        basket.add(product)
        repository.moveToBasket(product)
    }

    fun errorLoadDetect() {
        errorLoadDataFlag.postValue(true)
    }

    fun setScrollPosition(position: Int) {
        scrollPositionList = position
    }

    fun setSelectedProduct(product: Product) {
        selectedProduct = product
    }

}