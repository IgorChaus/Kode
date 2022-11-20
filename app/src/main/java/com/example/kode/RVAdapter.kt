package com.example.kode

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.security.AccessController.getContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class RVAdapter(_persons: ArrayList<Person.Items>): RecyclerView.Adapter<RVAdapter.ViewHolder>() {


    var items = _persons
    var sorting: Int = R.id.radioButton1

    @RequiresApi(Build.VERSION_CODES.O)
    fun setMovieList(_persons: ArrayList<Person.Items>, _sorting: Int) {
        items = _persons
        sorting = _sorting
        if (sorting == R.id.radioButton1){
            items.sortWith(
                compareBy({ it.firstName }, { it.lastName })
            )
        }else{
            items.sortWith(
                compareBy({ it.birthday.substring(5) })
            )
            val currentDate = LocalDate.now().toString()
            while (items.get(0).birthday.substring(5) < currentDate.substring(5)){
                items.add(items.get(0))
                items.removeAt(0)
            }
        }
        notifyDataSetChanged()

    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val personPhoto: ImageView = itemView.findViewById(R.id.imageView)
        val personName: TextView = itemView.findViewById(R.id.personName)
        val personTag: TextView = itemView.findViewById(R.id.personTag)
        val personDepartment: TextView = itemView.findViewById(R.id.personDepartment)
        val birthday: TextView = itemView.findViewById(R.id.personBirthday)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) =

        ViewHolder(LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.item, viewGroup, false))

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(viewHolder: RVAdapter.ViewHolder, position: Int){

                val path: String = items.get(position).avatarUrl
                Glide.with(viewHolder.itemView.getContext()).load(path).circleCrop()
                    .into(viewHolder.personPhoto)
                viewHolder.personName.text = items.get(position).firstName + " " +
                        items.get(position).lastName
                viewHolder.personTag.text = " " + items.get(position).userTag
                viewHolder.personDepartment.text = items.get(position).department.toString()
                if(sorting == R.id.radioButton1)
                    viewHolder.birthday.text = ""
                else {
                    var date = LocalDate.parse(items.get(position).birthday)
                    var formatter: DateTimeFormatter =
                        DateTimeFormatter.ofPattern("dd MMM", Locale("ru"))
                    viewHolder.birthday.text = date.format(formatter)
                }
        }


    override fun getItemCount(): Int {
        return items.size
    }

}