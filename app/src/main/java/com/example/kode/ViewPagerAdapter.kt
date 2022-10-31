package com.example.kode

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class ViewPagerAdapter(val departments: ArrayList<String>): RecyclerView.Adapter<ViewPagerAdapter.PagerVH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(LayoutInflater.from(parent.context).inflate(R.layout.item_name, parent, false))

    override fun onBindViewHolder(holder: PagerVH, position: Int) {
        holder.department.text = departments[position]
    }


    override fun getItemCount(): Int  = departments.size

    class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView){
        val department: TextView = itemView.findViewById(R.id.department)
    }

}