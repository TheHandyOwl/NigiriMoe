package com.tho.nigirimoe.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import com.tho.nigirimoe.R
import com.tho.nigirimoe.model.*

import kotlinx.android.synthetic.main.activity_table.*

class TableActivity : AppCompatActivity() {

    companion object {

        val EXTRA_TABLE_ITEM = "EXTRA_TABLE_ITEM"

        fun intent(context: Context, tableIndex: Int): Intent {
            val intent = Intent(context, TableActivity::class.java)
            intent.putExtra(EXTRA_TABLE_ITEM, tableIndex)
            return intent
        }
    }

    private val tableIndex: Int by lazy { intent.getIntExtra(EXTRA_TABLE_ITEM, 0) }
    private val table: Table by lazy { Tables[intent.getIntExtra(EXTRA_TABLE_ITEM, 0)] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)

        Log.v("TAG", "Pasando item: ${tableIndex} y mesa: ${table.toString()}")

        //addFakeData()

        setupActionBar()
        setupOrdersList()
        setupNewOrderButton()

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun addFakeData() {
        if(MenuList.courses.size > 0) {
            for (item in 1..tableIndex+1) {
                table.orders.add(Order(MenuList.courses[0], "Sin observaciones"))
            }
        }
    }

    fun setupActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = table.name
    }

    private fun setupOrdersList() {
        val list = findViewById<ListView>(R.id.orders_list)
        val adapter = ArrayAdapter<Order>(this,android.R.layout.simple_list_item_1, table.orders)
        list.adapter = adapter

        list.setOnItemClickListener { _, _, position, _ ->
            Log.v("TAG", "Se ha pulsado en la celda: ${position}")
        }
    }

    private fun setupNewOrderButton() {
        new_order_btn.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

}
