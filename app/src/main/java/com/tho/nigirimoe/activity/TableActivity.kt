package com.tho.nigirimoe.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.tho.nigirimoe.R

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)
        setSupportActionBar(toolbar)

        Log.v("TAG", "Pasando item: ${intent.getIntExtra(EXTRA_TABLE_ITEM, 0)}")

        new_order_btn.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

}
