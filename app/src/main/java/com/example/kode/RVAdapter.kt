package com.example.kode

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.security.AccessController.getContext
import kotlin.system.exitProcess

class RVAdapter(_persons: ArrayList<Person.Items>): RecyclerView.Adapter<RVAdapter.ViewHolder>() {


    var items = _persons

    fun setMovieList(_persons: ArrayList<Person.Items>) {
        items = _persons
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val personPhoto: ImageView = itemView.findViewById(R.id.imageView)
        val personName: TextView = itemView.findViewById(R.id.personName)
        val personTag: TextView = itemView.findViewById(R.id.personTag)
        val personDepartment: TextView = itemView.findViewById(R.id.personDepartment)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) =

        ViewHolder(LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.item, viewGroup, false))

    override fun onBindViewHolder(viewHolder: RVAdapter.ViewHolder, position: Int){

                val path: String = items.get(position).avatarUrl
                Glide.with(viewHolder.itemView.getContext()).load(path).circleCrop()
                    .into(viewHolder.personPhoto)
                viewHolder.personName.text = items.get(position).firstName + " " +
                        items.get(position).lastName
                viewHolder.personTag.text = " " + items.get(position).userTag
                viewHolder.personDepartment.text = items.get(position).department.toString()
        }


    override fun getItemCount(): Int {
        return items.size
    }

}