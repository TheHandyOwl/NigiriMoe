package com.tho.nigirimoe.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
// Not this Fragment
//import android.support.v4.app.Fragment
import  android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView

import com.tho.nigirimoe.R
import com.tho.nigirimoe.model.Table
import com.tho.nigirimoe.model.Tables

class TableListFragment : Fragment() {

    companion object {
        fun newInstance() = TableListFragment()
    }

    lateinit var root: View
    private var onTableSelectedListener: OnTableSelectedListener? = null
    private var _data = ArrayList<HashMap<String, Any>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (inflater != null) {
            root = inflater.inflate(R.layout.fragment_table_list, container, false)
            val list = root.findViewById<ListView>(R.id.table_list)
            val adapter = ArrayAdapter<Table>(activity, android.R.layout.simple_list_item_1, Tables.toArray())
            list.adapter = adapter

            // Pulsación del item de la lista
            list.setOnItemClickListener { parent, view, position, id ->
                onTableSelectedListener?.onTableSelected(Tables.get(position), position)
            }

        }

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
        if (listener is OnTableSelectedListener) {
            onTableSelectedListener = listener
        }
    }

    override fun onDetach() {
        super.onDetach()
        onTableSelectedListener = null
    }

    interface OnTableSelectedListener {
        fun onTableSelected(table: Table, position: Int)
    }

}
