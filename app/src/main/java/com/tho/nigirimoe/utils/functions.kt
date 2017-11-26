package com.tho.nigirimoe.utils

import org.json.JSONArray

fun JSONArray.toListArray(): List<String> {
    return (0 until length())
            .map { get(it).toString() }
            .toMutableList()
}