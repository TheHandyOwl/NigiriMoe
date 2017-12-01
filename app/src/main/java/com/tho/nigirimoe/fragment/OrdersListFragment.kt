package com.tho.nigirimoe.fragment

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.tho.nigirimoe.R
import com.tho.nigirimoe.model.Order
import com.tho.nigirimoe.model.Table
import com.tho.nigirimoe.model.Tables

class OrdersListFragment : Fragment() {

    companion object {
        val ARG_TABLE_ITEM = "ARG_TABLE_ITEM"

        fun newInstance(tableIndex: Int) : OrdersListFragment {
            val fragment = OrdersListFragment()

            val arguments = Bundle()
            arguments.putInt(ARG_TABLE_ITEM, tableIndex)
            fragment.arguments = arguments

            return fragment
        }
    }

    private val tableIndex: Int by lazy { arguments.getInt(ARG_TABLE_ITEM, 0) }
    private val table: Table by lazy { Tables[arguments.getInt(ARG_TABLE_ITEM, 0)] }

    lateinit var root: View
    private var onOrderSelectedListener: OnOrderSelectedListener? = null
    private var onNewOrderButtonListener: OnNewOrderButtonListener? = null
    private val list by lazy { root.findViewById<ListView>(R.id.orders_list) }
    private lateinit var adapter: ArrayAdapter<Order>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (inflater != null) {

            root = inflater.inflate(R.layout.fragment_orders_list, container, false)
            val list = root.findViewById<ListView>(R.id.orders_list)
            refreshOrdersList()

            //Pulsación del item de la lista
            list.setOnItemClickListener { _, _, position, _ ->
                onOrderSelectedListener?.onOrderSelected(tableIndex, table.orders[position], position)
            }
        }

        setupNewOrderButton()

        return root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        commonAttach(context)
    }

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        commonAttach(activity)
    }

    fun commonAttach(listener: Any?) {
        if (listener is OnOrderSelectedListener) {
            onOrderSelectedListener = listener
        }

        if (listener is OnNewOrderButtonListener) {
            onNewOrderButtonListener = listener
        }
    }

    override fun onDetach() {
        super.onDetach()
        onOrderSelectedListener = null
    }

    fun deleteOrders() {
        table.orders.clear()
        refreshOrdersList()
    }

    fun showOrdersListDataChanged() {
        // Aquí se refrescan los cambios en el TableList
        refreshOrdersList()
        adapter.notifyDataSetInvalidated()
    }

    fun refreshOrdersList() {
        //adapter.notifyDataSetInvalidated()
        //adapter.notifyDataSetChanged()
        adapter = ArrayAdapter<Order>(root.context,android.R.layout.simple_list_item_1, table.orders)
        list.adapter = adapter
    }

    private fun setupNewOrderButton() {
        //new_order_btn.setOnClickListener { _ ->
        root.findViewById<FloatingActionButton>(R.id.new_order_btn).setOnClickListener { _ ->
            onNewOrderButtonListener?.onNewOrderButton(tableIndex)
            //startActivityForResult(MenuActivity.intent(root.context, tableIndex), REQ_MENU_AND_ORDER)
        }
    }

    interface OnOrderSelectedListener {
        fun onOrderSelected(tableIndex: Int, order: Order, cellPosition: Int)
    }

    interface OnNewOrderButtonListener {
        fun onNewOrderButton(tableIndex: Int)
    }

}
