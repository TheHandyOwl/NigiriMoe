package com.tho.nigirimoe.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.tho.nigirimoe.R
import com.tho.nigirimoe.fragment.TableListFragment
import com.tho.nigirimoe.model.Table

class TableListActivity : AppCompatActivity(), TableListFragment.OnTableSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_list)

        // Comprobamos el layout
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

    override fun onTableSelected(table: Table, position: Int) {
        Log.v("LOG", "Se ha pulsado la posición: ${position}")
    }

}
