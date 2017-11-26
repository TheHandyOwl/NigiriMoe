package com.tho.nigirimoe.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.tho.nigirimoe.R
import com.tho.nigirimoe.model.Order
import com.tho.nigirimoe.model.Table
import com.tho.nigirimoe.model.Tables
import kotlinx.android.synthetic.main.activity_edit_course.*

class EditCourseActivity : AppCompatActivity() {

    companion object {
        val EXTRA_TABLE_ITEM = "EXTRA_TABLE_ITEM"
        val EXTRA_ORDER_ITEM_INDEX = "EXTRA_ORDER_ITEM_INDEX"
        val EXTRA_ORDER_ITEM = "EXTRA_ORDER_ITEM"

        fun intentNewOrder(context: Context, table: Int, order: Order) : Intent {
            val intent = Intent(context, EditCourseActivity::class.java)
            intent.putExtra(EXTRA_TABLE_ITEM, table)
            intent.putExtra(EXTRA_ORDER_ITEM, order)
            return intent
        }

        fun intentEditOrder(context: Context, table: Int, orderItem: Int, order: Order) : Intent {
            val intent = Intent(context, EditCourseActivity::class.java)
            intent.putExtra(EXTRA_TABLE_ITEM, table)
            intent.putExtra(EXTRA_ORDER_ITEM_INDEX, orderItem)
            intent.putExtra(EXTRA_ORDER_ITEM, order)
            return intent
        }
    }

    private val orderIndex: Int by lazy { intent.getIntExtra(EXTRA_ORDER_ITEM_INDEX, 0) }
    private val table: Table by lazy { Tables[intent.getIntExtra(EXTRA_TABLE_ITEM, 0)] }
    private val order: Order by lazy { intent.getSerializableExtra(EXTRA_ORDER_ITEM) as Order }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_course)

        // Accedemos al contexto
        val context = this

        edit_course_image.setImageResource(context.resources.getIdentifier(order.course.image.substring(0,order.course.image.length-4),"drawable","com.tho.nigirimoe"))
        edit_course_name.text = order.course.name
        edit_course_observations_text.setText(order.observations)

        for (allergen in order.course.allergens) {
            when (allergen) {
                "a_001_sulfide" -> a_001_sulfide.visibility = View.VISIBLE
                "a_002_egg" -> a_002_egg.visibility = View.VISIBLE
                "a_005_milk" -> a_005_milk.visibility = View.VISIBLE
                "a_006_crustaceans" -> a_006_crustaceans.visibility = View.VISIBLE
                "a_007_fish" -> a_007_fish.visibility = View.VISIBLE
                "a_010_gluten" -> a_010_gluten.visibility = View.VISIBLE
                "a_012_soybean" -> a_012_soybean.visibility = View.VISIBLE
                "a_013_peanut" -> a_013_peanut.visibility = View.VISIBLE
            }
        }

        setupActionBar()
        setupListeners()

    }

    private fun setupActionBar() {
        supportActionBar?.title = "${table.name}: ${order.course.name}"
    }

    private fun setupListeners() {
        btn_edit_course_cancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        btn_edit_course_ok.setOnClickListener {
            val order = Order(order.course, edit_course_observations_text.text.toString())

            val intent = Intent()
            intent.putExtra(EXTRA_ORDER_ITEM, order)
            intent.putExtra(EXTRA_ORDER_ITEM_INDEX, orderIndex)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

}