package com.tho.nigirimoe.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.tho.nigirimoe.R
import com.tho.nigirimoe.model.MenuList
import com.tho.nigirimoe.model.Order
import com.tho.nigirimoe.model.Table
import com.tho.nigirimoe.model.Tables
import kotlinx.android.synthetic.main.activity_table.*

class TableActivity : AppCompatActivity() {

    companion object {

        val EXTRA_TABLE_ITEM = "EXTRA_TABLE_ITEM"
        val REQ_MENU_AND_ORDER = 33
        val REQ_EDIT_ORDER = 66

        fun intent(context: Context, tableIndex: Int): Intent {
            val intent = Intent(context, TableActivity::class.java)
            intent.putExtra(EXTRA_TABLE_ITEM, tableIndex)
            return intent
        }
    }

    private val tableIndex: Int by lazy { intent.getIntExtra(EXTRA_TABLE_ITEM, 0) }
    private val table: Table by lazy { Tables[intent.getIntExtra(EXTRA_TABLE_ITEM, 0)] }

    private lateinit var adapter: ArrayAdapter<Order>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)

        //addFakeData()

        setupActionBar()
        setupOrdersList()
        setupNewOrderButton()

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Para probar con datos inventados
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
        adapter = ArrayAdapter<Order>(this,android.R.layout.simple_list_item_1, Tables[tableIndex].orders)
        list.adapter = adapter

        list.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, EditCourseActivity::class.java)
            intent.putExtra(EditCourseActivity.EXTRA_TABLE_ITEM, tableIndex)
            intent.putExtra(EditCourseActivity.EXTRA_ORDER_ITEM_INDEX, position)
            intent.putExtra(EditCourseActivity.EXTRA_ORDER_ITEM, Tables[tableIndex].orders[position])
            startActivityForResult(intent, REQ_EDIT_ORDER)
        }
    }

    private fun setupNewOrderButton() {
        new_order_btn.setOnClickListener { _ ->
            startActivityForResult(MenuActivity.intent(this, tableIndex), REQ_MENU_AND_ORDER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> when (requestCode) {
                REQ_MENU_AND_ORDER -> {
                    if (data != null) {
                        val order = data.getSerializableExtra(EditCourseActivity.EXTRA_ORDER_ITEM) as? Order
                        if (order != null) {
                            table.orders.add(order)
                            adapter.notifyDataSetChanged()
                            Toast.makeText(this, "Añadimos pedido nuevo", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                REQ_EDIT_ORDER -> {
                    if (data != null) {
                        val order = data.getSerializableExtra(EditCourseActivity.EXTRA_ORDER_ITEM) as? Order
                        val orderIndex = data.getSerializableExtra(EditCourseActivity.EXTRA_ORDER_ITEM_INDEX) as? Int
                        if (orderIndex != null) {
                            if (table.orders[orderIndex].observations != order?.observations) {
                                table.orders[orderIndex].observations = order?.observations ?: ""
                                adapter.notifyDataSetChanged()
                                Toast.makeText(this, "Modificamos observaciones del pedido", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
            Activity.RESULT_CANCELED -> Toast.makeText(this, "Se ha cancelado el pedido", Toast.LENGTH_LONG).show()
        }
    }
}
