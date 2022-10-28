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

class RVAdapter(_persons: List<Person.Items>) : RecyclerView.Adapter<RVAdapter.PersonViewHolder>() {

    var items = _persons

    fun setMovieList(_persons: List<Person.Items>){
        items = _persons
        notifyDataSetChanged();
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PersonViewHolder {
        val v: View = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup,
            false)
        return PersonViewHolder(v)
    }

    override fun onBindViewHolder(personViewHolder: PersonViewHolder, position: Int) {

        val path: String = items.get(position).avatarUrl
        Glide.with(personViewHolder.itemView.getContext()).load(R.drawable.ig1).circleCrop().
           into(personViewHolder.personPhoto)
        personViewHolder.personName.text = items.get(position).firstName + " " +
                items.get(position).lastName
        personViewHolder.personTag.text = " " + items.get(position).userTag
        personViewHolder.personDepartment.text = items.get(position).department.toString()
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

}