package com.example.kode

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RVAdapter(_persons: List<Person.Items>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEARDER = 0
        private const val TYPE_PERSON = 1
    }

    var items = _persons
    var department = "Все"

    fun setMovieList(_persons: List<Person.Items>, _department: String) {
        items = _persons
        department = _department
        notifyDataSetChanged();
    }

    override fun getItemViewType(position: Int): Int{
        if (position == 0)
            return TYPE_HEARDER
        else
            return TYPE_PERSON
    }



    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) =
        when (viewType) {

            TYPE_HEARDER -> HeaderViewHolder(
                LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.header, viewGroup, false))

            TYPE_PERSON -> PersonViewHolder(
                LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.item, viewGroup, false))

            else -> {
                throw IllegalArgumentException()
            }
        }


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) =
        when (viewHolder.itemViewType) {
            TYPE_HEARDER -> {
                val headerViewHolder = viewHolder as HeaderViewHolder
                headerViewHolder.department.text = department
            }
            TYPE_PERSON -> {
                val personViewHolder = viewHolder as PersonViewHolder
                val path: String = items.get(position).avatarUrl
                Glide.with(personViewHolder.itemView.getContext()).load(R.drawable.ig1).circleCrop()
                    .into(personViewHolder.personPhoto)
                personViewHolder.personName.text = items.get(position).firstName + " " +
                        items.get(position).lastName
                personViewHolder.personTag.text = " " + items.get(position).userTag
                personViewHolder.personDepartment.text = items.get(position).department.toString()
            }
            else -> {
                throw IllegalArgumentException()
            }
        }


    override fun getItemCount(): Int {
        return items.size
    }

    class PersonViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val personPhoto: ImageView = itemView.findViewById(R.id.imageView)
        val personName: TextView = itemView.findViewById(R.id.personName)
        val personTag: TextView = itemView.findViewById(R.id.personTag)
        val personDepartment: TextView = itemView.findViewById(R.id.personDepartment)
    }

    class HeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val department: TextView = itemView.findViewById(R.id.department)
    }

}