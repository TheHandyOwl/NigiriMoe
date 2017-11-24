package com.tho.nigirimoe.model

import java.io.Serializable

data class Order (val course: Course, var observations: String) : Serializable {
    override fun toString(): String {
        var tempString: String = ""
        if (observations.length != 0) {
            tempString = ": ${observations}"
        }
        return "${course.name}${tempString}"
    }
}