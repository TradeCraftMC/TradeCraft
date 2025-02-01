package dev.tradecraft.tradecraft.economy.interfaces

import dev.tradecraft.tradecraft.economy.data.Brand
import dev.tradecraft.tradecraft.economy.data.Listing

interface Vendor {
    fun fetchBrands(): List<Brand>
    fun createBrand(brand: Brand): String
    fun removeBrand(brandId: String)
}