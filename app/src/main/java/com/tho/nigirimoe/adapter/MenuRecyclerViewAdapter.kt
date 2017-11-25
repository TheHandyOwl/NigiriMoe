package com.tho.nigirimoe.adapter

import android.content.res.Resources
import android.preference.PreferenceManager
import android.os.Bundle
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.tho.nigirimoe.R
import com.tho.nigirimoe.model.Course
import com.tho.nigirimoe.model.MenuList
import com.tho.nigirimoe.model.Order
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
            Log.v("TAG","Image: ${menuCourse.image}")
            itemView.course_image.setImageResource(context.resources.getIdentifier(menuCourse.image.substring(0,menuCourse.image.length-4),"drawable","com.tho.nigirimoe"))
            itemView.course_name.text = menuCourse.name
            itemView.course_price.text = menuCourse.price.toString()

            // Listener
            itemView.setOnClickListener { onClickListener?.onMenuSelected(menuCourse)}
        }
    }

    interface OnMenuSelectedListener {
        fun onMenuSelected(course: Course)
    }

}
