package com.tho.nigirimoe.model

data class Table (val name: String, var orders: MutableList<Order> = mutableListOf())