package com.tho.nigirimoe.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import com.tho.nigirimoe.R
import com.tho.nigirimoe.adapter.MenuRecyclerViewAdapter
import com.tho.nigirimoe.model.Course
import com.tho.nigirimoe.model.Order
import com.tho.nigirimoe.model.Table
import com.tho.nigirimoe.model.Tables

class MenuActivity : AppCompatActivity() {

    companion object {

        val EXTRA_TABLE_ITEM = "EXTRA_TABLE_ITEM"

        fun intent(context: Context, tableIndex: Int): Intent {
            val intent = Intent(context, MenuActivity::class.java)
            intent.putExtra(EXTRA_TABLE_ITEM, tableIndex)
            return intent
        }
    }

    private val tableIndex: Int by lazy { intent.getIntExtra(TableActivity.EXTRA_TABLE_ITEM, 0) }
    private val table: Table by lazy { Tables[intent.getIntExtra(TableActivity.EXTRA_TABLE_ITEM, 0)] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        setupActionBar()
        setupMenuRecycleView()

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

    fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Elegir platos para mesa ${Tables[tableIndex]}"
    }

    fun setupMenuRecycleView() {
        val list = findViewById<RecyclerView>(R.id.menu_list)
        list.layoutManager = LinearLayoutManager(this)
        list.itemAnimator = DefaultItemAnimator()

        val adapter = MenuRecyclerViewAdapter()
        adapter.onClickListener = object : MenuRecyclerViewAdapter.OnMenuSelectedListener {
            override fun onMenuSelected(course: Course) {
                sendCourseToEditCourseActivity(course)
            }
        }
        list.adapter = adapter
    }

    private fun sendCourseToEditCourseActivity(course: Course) {
        val intent = Intent(this, EditCourseActivity::class.java)
        intent.putExtra(EditCourseActivity.EXTRA_TABLE_ITEM, tableIndex)
        intent.putExtra(EditCourseActivity.EXTRA_ORDER_ITEM, Order(course, ""))
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT) // Nos saltaremos el menú a la vuelta
        startActivity(intent)
        finish()
    }

}
