package com.tho.nigirimoe.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.FragmentManager
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.tho.nigirimoe.R
import com.tho.nigirimoe.fragment.OrdersListFragment
import com.tho.nigirimoe.fragment.TableListFragment
import com.tho.nigirimoe.model.*
import com.tho.nigirimoe.utils.toListArray
import kotlinx.android.synthetic.main.activity_table_list.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.json.JSONObject
import java.net.URL
import java.util.*

class TableListActivity : AppCompatActivity(),
        TableListFragment.OnTableSelectedListener,
        OrdersListFragment.OnOrderSelectedListener,
        OrdersListFragment.OnNewOrderButtonListener {

    enum class VIEW_INDEX(val index: Int) {
        LOADING(0),
        TABLES(1)
    }

    companion object {
        val REQ_MENU_AND_ORDER = 33
        val REQ_EDIT_ORDER = 66
    }

    private var tableIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_list)

        setupViewSwitcher()
        setupActionBar()
        loadMenu()

        // Quitamos fragments reemplazados
        //fragmentManager.popBackStack("OrdersReplaceTables")

        try {
            fragmentManager.popBackStack("OrdersReplacesTables", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        } catch (e: Exception) {
            Log.v("TAG", "There was an error with popBackStack: ${e}")
        }

        // Comprobamos el layout table
        if (findViewById<View>(R.id.fragment_table_list) != null) {
            //Comprobamos el fragment
            if (fragmentManager.findFragmentById(R.id.fragment_table_list) == null) {
                val fragment = TableListFragment.newInstance()
                fragmentManager.beginTransaction()
                        .add(R.id.fragment_table_list, fragment)
                        .commit()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cash_register, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        // Orders menu: landscape
        val ordersFragment = fragmentManager.findFragmentById(R.id.fragment_orders_list) as? OrdersListFragment
        // Orders menu: portrait
        val ordersInTableFragment = fragmentManager.findFragmentById(R.id.fragment_table_list) as? OrdersListFragment

        if (ordersFragment != null || ordersInTableFragment != null) {
            val menuCaja = menu?.findItem(R.id.action_cash_register)
            menuCaja?.setEnabled(Tables[tableIndex].orders.size > 0)

            val menuBorrarPedidos = menu?.findItem(R.id.action_delete_orders)
            menuBorrarPedidos?.setEnabled(Tables[tableIndex].orders.size > 0)
        } else {
            val menuCaja = menu?.findItem(R.id.action_cash_register)
            menuCaja?.setEnabled(false)

            val menuBorrarPedidos = menu?.findItem(R.id.action_delete_orders)
            menuBorrarPedidos?.setEnabled(false)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_cash_register -> {
                var totalPrice: Float = 0.toFloat()
                for (order in Tables[tableIndex].orders) {
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
                            // Delete orders: portrait
                            val ordersInTableFragment = fragmentManager.findFragmentById(R.id.fragment_table_list) as? OrdersListFragment
                            ordersInTableFragment?.deleteOrders()
                            // Delete orders: landscape
                            val ordersFragment = fragmentManager.findFragmentById(R.id.fragment_orders_list) as? OrdersListFragment
                            ordersFragment?.deleteOrders()
                            val tableFragment = fragmentManager.findFragmentById(R.id.fragment_table_list) as? TableListFragment
                            tableFragment?.showTableListDataChanged()
                        })
                        .setNegativeButton("Cancelar", { dialog, _ ->
                            dialog.dismiss()
                        })
                        .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViewSwitcher(){
        view_switcher.setInAnimation(this, android.R.anim.fade_in)
        view_switcher.setOutAnimation(this, android.R.anim.fade_out)
    }

    fun setupActionBar() {
        supportActionBar?.title = "Nigiri Moe"
    }

    private fun loadMenu() {
        if (MenuList.courses.size == 0) {
            view_switcher.displayedChild = VIEW_INDEX.LOADING.index

            val activity = this

            async(UI) {
                val menuListDownloader: Deferred<List<Course>?> = bg {
                    downloadMenuList()
                }

                val downloadedMenuList = menuListDownloader.await()
                view_switcher.displayedChild = VIEW_INDEX.TABLES.index

                if (downloadedMenuList != null) {
                    // MenuList.courses.addAll(downloadedMenuList)
                    MenuList.courses += downloadedMenuList
                } else {
                    AlertDialog.Builder(activity)
                            .setTitle("Error en la descarga")
                            .setMessage("No se puede descargar el menú")
                            .setPositiveButton("Reintentar", { dialog, _ ->
                                dialog.dismiss()
                                loadMenu()
                            })
                            .setNegativeButton("Salir", { _, _ -> activity.finish() })
                            .show()
                }
            }

        } else {
            view_switcher.displayedChild = VIEW_INDEX.TABLES.index
        }
    }

    private fun downloadMenuList(): List<Course>? {
        try {

            Thread.sleep(1000)

            // Descarga del menú
            val url = URL("http://www.mocky.io/v2/5a19fe153100006905d91eae")
            val jsonString = Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next()

            // Analizamos los datos descargados
            val jsonRoot = JSONObject(jsonString)
            val myMenu = jsonRoot.getJSONArray("menu")

            // Nos creamos la lista que vamos a ir rellenando
            val menuItems = mutableListOf<Course>()

            //Recorremos el menú del objeto JSON
            for (itemIndex in 0 until myMenu.length()) {
                val item = myMenu.getJSONObject(itemIndex)
                val name = item.getString("name")
                val price = item.getString("price").toFloat()
                val image = item.getString("image")
                var allergens = item.getJSONArray("allergens")

                menuItems.add(Course(name, price, image, allergens.toListArray()))
            }

            return menuItems

        } catch (ex: Exception) {
            ex.printStackTrace() // Revisar permisos de internet???
        }

        return null
    }

    override fun onTableSelected(table: Table, position: Int) {
        tableIndex = position

        val fragment = OrdersListFragment.newInstance(position)

        if (findViewById<View>(R.id.fragment_orders_list) != null) {
            // Landscape
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_orders_list, fragment)
                    .commit()
        } else {
            // Portrait
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_table_list, fragment)
                    .addToBackStack("OrdersReplacesTables")
                    .commit()
        }

        // Title
        supportActionBar?.title = "Nigiri Moe - ${Tables[position].name}"
    }

    override fun onOrderSelected(tableIndex: Int, order: Order, position: Int) {
        startActivityForResult(EditCourseActivity.intentEditOrder(this, tableIndex, position, Tables[tableIndex].orders[position]), REQ_EDIT_ORDER)
    }

    override fun onNewOrderButton(tableIndex: Int) {
        startActivityForResult(MenuActivity.intent(this, tableIndex), REQ_MENU_AND_ORDER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {

            Activity.RESULT_OK -> when (requestCode) {
                REQ_MENU_AND_ORDER -> {
                    if (data != null) {
                        val tableIndex = data.getSerializableExtra(EditCourseActivity.EXTRA_TABLE_ITEM) as? Int
                        val newOrder = data.getSerializableExtra(EditCourseActivity.EXTRA_ORDER_ITEM) as? Order
                        if (newOrder != null && tableIndex != null ) {
                            val olderOrders = Tables[tableIndex].orders.toMutableList()
                            val table = Tables[tableIndex]
                            table.orders.add(newOrder)

                            // Refresh table list: orders in portrait
                            val ordersInTableFragment = fragmentManager.findFragmentById(R.id.fragment_table_list) as? OrdersListFragment
                            ordersInTableFragment?.refreshOrdersList()
                            // Refresh table list: tables and orders in landscape
                            val ordersFragment = fragmentManager.findFragmentById(R.id.fragment_orders_list) as? OrdersListFragment
                            ordersFragment?.refreshOrdersList()
                            val tableFragment = fragmentManager.findFragmentById(R.id.fragment_table_list) as? TableListFragment
                            tableFragment?.showTableListDataChanged()

                            // Wanna undo?
                            val fragmentView = findViewById<View>(R.id.fragment_table_list)
                            Snackbar.make(fragmentView, "Añadimos pedido nuevo", Snackbar.LENGTH_LONG)
                                    .setAction("Deshacer añadir pedido") {
                                        table.orders = olderOrders
                                        ordersInTableFragment?.refreshOrdersList()
                                        ordersFragment?.showOrdersListDataChanged()
                                        tableFragment?.showTableListDataChanged()
                                    }
                                    .show()
                        }
                    }
                }
                REQ_EDIT_ORDER -> {
                    if (data != null) {
                        val order = data.getSerializableExtra(EditCourseActivity.EXTRA_ORDER_ITEM) as? Order
                        val orderIndex = data.getSerializableExtra(EditCourseActivity.EXTRA_ORDER_ITEM_INDEX) as? Int
                        val tableIndex = data.getSerializableExtra(EditCourseActivity.EXTRA_TABLE_ITEM) as? Int
                        if (order != null && orderIndex != null && tableIndex != null) {
                            val table = Tables[tableIndex]
                            if (table.orders[orderIndex].observations != order?.observations) {
                                val olderOrder = table.orders[orderIndex].observations
                                table.orders[orderIndex].observations = order?.observations ?: ""

                                // Refresh table list: orders in portrait
                                val ordersInTableFragment = fragmentManager.findFragmentById(R.id.fragment_table_list) as? OrdersListFragment
                                ordersInTableFragment?.refreshOrdersList()
                                // Refresh table list: tables and orders in landscape
                                val ordersFragment = fragmentManager.findFragmentById(R.id.fragment_orders_list) as? OrdersListFragment
                                ordersFragment?.refreshOrdersList()
                                val tableFragment = fragmentManager.findFragmentById(R.id.fragment_table_list) as? TableListFragment
                                tableFragment?.showTableListDataChanged()

                                // Wanna undo?
                                val fragmentView = findViewById<View>(R.id.fragment_table_list)
                                Snackbar.make(fragmentView, "Modificamos observaciones del pedido", Snackbar.LENGTH_LONG)
                                        .setAction("Deshacer modificar observaciones") {
                                            table.orders[orderIndex].observations = olderOrder
                                            ordersInTableFragment?.refreshOrdersList()
                                            ordersFragment?.refreshOrdersList()
                                            tableFragment?.showTableListDataChanged()
                                        }
                                        .show()
                            }
                        }
                    }
                }
            }
            Activity.RESULT_CANCELED -> when (requestCode) {
                REQ_MENU_AND_ORDER -> {
                    val fragmentView = findViewById<View>(R.id.fragment_table_list)
                    Snackbar.make(fragmentView,
                            "Se ha cancelado el pedido",
                            Snackbar.LENGTH_LONG).show()
                }
                REQ_EDIT_ORDER -> {
                    val fragmentView = findViewById<View>(R.id.fragment_table_list)
                    Snackbar.make(fragmentView,
                            "Se han cancelado las observaciones",
                            Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

}