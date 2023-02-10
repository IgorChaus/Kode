package com.example.kode_viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kode_viewmodel.model.Person

class RAdapter: RecyclerView.Adapter<RAdapter.MyViewHolder>() {

    private var item: List<Person.Items> = arrayListOf()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val personPhoto: ImageView = itemView.findViewById(R.id.imageView)
        val personName: TextView = itemView.findViewById(R.id.personName)
        val personTag: TextView = itemView.findViewById(R.id.personTag)
        val personDepartment: TextView = itemView.findViewById(R.id.personDepartment)
        val birthday: TextView = itemView.findViewById(R.id.personBirthday)
    }

    //Эта функция на выходе создает экземпляр класса MyViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder{
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val path: String = item[position].avatarUrl
        Glide.with(holder.itemView.context).load(path).circleCrop()
            .into(holder.personPhoto)
        holder.personName.text = item[position].firstName + " " +
                item[position].lastName
        holder.personTag.text = " " + item[position].userTag.lowercase()
        holder.personDepartment.text = item[position].department
    }

    override fun getItemCount(): Int {
        return item.size
    }

    fun refreshUsers(items: List<Person.Items>) {
        this.item = items
        notifyDataSetChanged()
    }

}