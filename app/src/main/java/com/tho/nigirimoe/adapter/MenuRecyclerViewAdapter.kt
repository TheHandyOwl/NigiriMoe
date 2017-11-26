package com.tho.nigirimoe.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tho.nigirimoe.R
import com.tho.nigirimoe.model.Course
import com.tho.nigirimoe.model.MenuList
import kotlinx.android.synthetic.main.content_menu.view.*

class MenuRecyclerViewAdapter : RecyclerView.Adapter<MenuRecyclerViewAdapter.MenuViewHolder>() {

    var onClickListener: OnMenuSelectedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.content_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder?, position: Int) {
        holder?.bindMenu(MenuList.courses[position])
    }

    override fun getItemCount() = MenuList.courses.count() ?: 0

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindMenu(menuCourse: Course) {

            // Accedemos al contexto
            val context = itemView.context

            // Actualizamos la vista con el modelo
            itemView.course_image.setImageResource(context.resources.getIdentifier(menuCourse.image.substring(0,menuCourse.image.length-4),"drawable","com.tho.nigirimoe"))
            itemView.course_name.text = menuCourse.name
            itemView.course_price.text = menuCourse.price.toString()

            for (allergen in menuCourse.allergens) {
                when (allergen) {
                    "a_001_sulfide" -> itemView.a_001_sulfide.visibility = View.VISIBLE
                    "a_002_egg" -> itemView.a_002_egg.visibility = View.VISIBLE
                    "a_005_milk" -> itemView.a_005_milk.visibility = View.VISIBLE
                    "a_006_crustaceans" -> itemView.a_006_crustaceans.visibility = View.VISIBLE
                    "a_007_fish" -> itemView.a_007_fish.visibility = View.VISIBLE
                    "a_010_gluten" -> itemView.a_010_gluten.visibility = View.VISIBLE
                    "a_012_soybean" -> itemView.a_012_soybean.visibility = View.VISIBLE
                    "a_013_peanut" -> itemView.a_013_peanut.visibility = View.VISIBLE
                }
            }

            // Listener
            itemView.setOnClickListener { onClickListener?.onMenuSelected(menuCourse)}
        }
    }

    interface OnMenuSelectedListener {
        fun onMenuSelected(course: Course)
    }

}
