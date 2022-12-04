package com.example.kode

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class RAdapter(_persons: ArrayList<Person.Items>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = _persons
    private var sorting: Int = R.id.radioButton1
    private var  its: ArrayList<Any> = arrayListOf()


    @RequiresApi(Build.VERSION_CODES.O)
    fun setDataList(_persons: ArrayList<Person.Items>, _sorting: Int) {
        items = _persons
        sorting = _sorting
        its.clear()

        if (sorting == R.id.radioButton1){
            items.sortWith(
                compareBy({ it.firstName }, { it.lastName })
            )
            its.addAll(items)
        }else{
            val formatMMDD: DateTimeFormatter = DateTimeFormatter.ofPattern("MMdd")
            val currentDate = LocalDate.now()

            items.sortWith(
                compareBy({ LocalDate.parse(it.birthday).format(formatMMDD) })
            )

            its.addAll(items.filter
                {LocalDate.parse(it.birthday).format(formatMMDD)  >= currentDate.format(formatMMDD)})

            val formatYear: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY")
            its.add(currentDate.format(formatYear))

            its.addAll(items.filter
                {LocalDate.parse(it.birthday).format(formatMMDD)  < currentDate.format(formatMMDD)})

        }

        notifyDataSetChanged()

    }

    override fun getItemViewType(position: Int): Int =
        when (its[position]) {
            is Person.Items -> R.layout.item
            is String -> R.layout.separator
            else -> throw IllegalArgumentException()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item -> ItemViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false))
        R.layout.separator -> SeparatorHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.separator, parent, false))
        else -> throw IllegalArgumentException()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val personPhoto: ImageView = itemView.findViewById(R.id.imageView)
        val personName: TextView = itemView.findViewById(R.id.personName)
        val personTag: TextView = itemView.findViewById(R.id.personTag)
        val personDepartment: TextView = itemView.findViewById(R.id.personDepartment)
        val birthday: TextView = itemView.findViewById(R.id.personBirthday)

    }

    class SeparatorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val separetor: TextView = itemView.findViewById(R.id.year)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindItem(holder: RecyclerView.ViewHolder, item: Person.Items) {
        val itemViewHolder = holder as ItemViewHolder
        val path: String = item.avatarUrl
        Glide.with(itemViewHolder.itemView.getContext()).load(path).circleCrop()
            .into(itemViewHolder.personPhoto)
        itemViewHolder.personName.text = item.firstName + " " +
                item.lastName
        itemViewHolder.personTag.text = " " + item.userTag.lowercase()
        itemViewHolder.personDepartment.text = item.department
        if(sorting == R.id.radioButton1)
            itemViewHolder.birthday.text = ""
        else {
            var date = LocalDate.parse(item.birthday)
            var formatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("dd MMM", Locale("ru"))
            itemViewHolder.birthday.text = date.format(formatter)
        }
        holder.itemView.setOnClickListener {
          /*  if (adapterPosition == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }*/
            val intent = Intent(itemViewHolder.itemView.context,Portfolio::class.java)
            intent.putExtra("path",path)
            intent.putExtra("personName",item.firstName + " " +
                    item.lastName)
            intent.putExtra("tag",item.userTag)
            intent.putExtra("department",item.department)
            intent.putExtra("birthday",item.birthday)


            intent.putExtra("age",item.birthday)
            intent.putExtra("phone",item.phone)

            itemViewHolder.itemView.context.startActivity(intent)
        }
    }
    private fun bindSeparator(holder: RecyclerView.ViewHolder, separator: String) {
        (holder as SeparatorHolder).separetor.text = separator
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (holder.itemViewType) {
            R.layout.item -> bindItem(holder,its[position] as Person.Items)
            R.layout.separator -> bindSeparator(holder,its[position] as String)
            else -> throw IllegalArgumentException()
        }



    override fun getItemCount() = its.count()
}

