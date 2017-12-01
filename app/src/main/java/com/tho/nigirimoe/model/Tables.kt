package com.tho.nigirimoe.model

object Tables {
    private val tables: List<Table> = listOf<Table>(
            Table("Mesa01"),
            Table("Mesa02"),
            Table("Mesa03"),
            Table("Mesa04"),
            Table("Mesa05"),
            Table("Mesa06"),
            Table("Mesa07"),
            Table("Mesa08"),
            Table("Mesa09"),
            Table("Mesa10"),
            Table("Mesa11"),
            Table("Mesa12")
    )

    val count
        get() = tables.size

    operator fun get(i: Int) = tables[i]

    fun toArray() = tables.toTypedArray()

}