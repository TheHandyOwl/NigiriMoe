package com.tho.nigirimoe.model

import java.io.Serializable

data class Course (val name: String,
                   val price: Float,
                   val image: String,
                   val description: String) : Serializable