package com.tho.nigirimoe.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.tho.nigirimoe.R
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

    lateinit var root: View
    private lateinit var adapter: ArrayAdapter<Order>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)

        setupActionBar()
        setupOrdersList()
        setupNewOrderButton()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cash_register, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val menuCaja = menu?.findItem(R.id.action_cash_register)
        menuCaja?.setEnabled( table.orders.size > 0 )

        val menuBorrarPedidos = menu?.findItem(R.id.action_delete_orders)
        menuBorrarPedidos?.setEnabled( table.orders.size > 0 )

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
                return true
            }
            R.id.action_cash_register -> {
                var totalPrice: Float = 0.toFloat()
                for (order in table.orders) {
                    totalPrice += order.course.price
                }
                AlertDialog.Builder(this)
                        .setTitle("Caja registradora")
                        .setMessage("A cobrar ${totalPrice}")
                        .setPositiveButton("Aceptar", { dialog, _ ->
                            dialog.dismiss()
                        })
                        .show()
            }
            R.id.action_delete_orders -> {
                AlertDialog.Builder(this)
                        .setTitle("Borrar pedidos")
                        .setMessage("Todos los pedidos de esta mesa serán eliminados")
                        .setPositiveButton("Aceptar", { dialog, _ ->
                            dialog.dismiss()
                            deleteOrders()
                        })
                        .setNegativeButton("Cancelar", { dialog, _ ->
                            dialog.dismiss()
                        })
                        .show()
            }
        }
        return super.onOptionsItemSelected(item)
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
                        val newOrder = data.getSerializableExtra(EditCourseActivity.EXTRA_ORDER_ITEM) as? Order
                        if (newOrder != null) {
                            table.orders.add(newOrder)
                            adapter.notifyDataSetChanged()
                            Toast.makeText(this, "Añadimos ${newOrder.course.name}", Toast.LENGTH_LONG).show()
                        }
                    }

                    // DESHACER NO DESHACE
                    /*
                    if (data != null) {
                        val newOrder = data.getSerializableExtra(EditCourseActivity.EXTRA_ORDER_ITEM) as? Order
                        if (newOrder != null) {
                            val olderOrders = table.orders
                            table.orders.add(newOrder)
                            adapter.notifyDataSetChanged()
                            Snackbar.make(findViewById(R.id.orders_list), "Añadimos pedido nuevo", Snackbar.LENGTH_LONG)
                                    .setAction("Deshacer") {
                                        table.orders = olderOrders
                                        adapter.notifyDataSetChanged()
                                    }
                                    .show()
                         }
                     }
                    */
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

    fun deleteOrders() {
        table.orders.clear()
        adapter.notifyDataSetInvalidated()
        adapter.notifyDataSetChanged()
    }
}
