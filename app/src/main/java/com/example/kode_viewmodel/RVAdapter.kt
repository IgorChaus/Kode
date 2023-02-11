package com.example.kode_viewmodel

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kode_viewmodel.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class RVAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var  items: List<IRow> = listOf()


    /* @SuppressLint("NotifyDataSetChanged")
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
                 compareBy { LocalDate.parse(it.birthday).format(formatMMDD) }
             )

             its.addAll(items.filter
             { LocalDate.parse(it.birthday).format(formatMMDD)  >= currentDate.format(formatMMDD)})

             val formatYear: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY")
             its.add(currentDate.format(formatYear))

             its.addAll(items.filter
             { LocalDate.parse(it.birthday).format(formatMMDD)  < currentDate.format(formatMMDD)})

         }

         notifyDataSetChanged()

     }*/

    fun refreshUsers(items: List<IRow>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is ABC -> R.layout.item
            is Birthday -> R.layout.item_birthday
            is Separator -> R.layout.separator
            else -> throw IllegalArgumentException()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item -> ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item, parent, false))
        R.layout.item_birthday -> ItemBirthdayViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_birthday, parent, false))
        R.layout.separator -> SeparatorHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.separator, parent, false))
        else -> throw IllegalArgumentException()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val personPhoto: ImageView = itemView.findViewById(R.id.imageView)
        val personName: TextView = itemView.findViewById(R.id.personName)
        val personTag: TextView = itemView.findViewById(R.id.personTag)
        val personDepartment: TextView = itemView.findViewById(R.id.personDepartment)

    }

    class ItemBirthdayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val personPhoto: ImageView = itemView.findViewById(R.id.imageView)
        val personName: TextView = itemView.findViewById(R.id.personName)
        val personTag: TextView = itemView.findViewById(R.id.personTag)
        val personDepartment: TextView = itemView.findViewById(R.id.personDepartment)
        val birthday: TextView = itemView.findViewById(R.id.personBirthday)

    }

    class SeparatorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val separetor: TextView = itemView.findViewById(R.id.year)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindItem(holder: RecyclerView.ViewHolder, item: ABC) {
        val itemViewHolder = holder as ItemViewHolder
        val path: String = item.avatarUrl
        Glide.with(itemViewHolder.itemView.context).load(path).circleCrop()
            .into(itemViewHolder.personPhoto)
        itemViewHolder.personName.text = item.firstName + " " +
                item.lastName
        itemViewHolder.personTag.text = " " + item.userTag.lowercase()
        itemViewHolder.personDepartment.text = item.department
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindItemBirthday(holder: RecyclerView.ViewHolder, item: Birthday) {
        val itemViewHolder = holder as ItemBirthdayViewHolder
        val path: String = item.avatarUrl
        Glide.with(itemViewHolder.itemView.context).load(path).circleCrop()
            .into(itemViewHolder.personPhoto)
        itemViewHolder.personName.text = item.firstName + " " +
                item.lastName
        itemViewHolder.personTag.text = " " + item.userTag.lowercase()
        itemViewHolder.personDepartment.text = item.department

        val date = LocalDate.parse(item.birthday)
        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd MMM", Locale("ru"))
        itemViewHolder.birthday.text = date.format(formatter)

    }


    private fun bindSeparator(holder: RecyclerView.ViewHolder, separator: String) {
        (holder as SeparatorHolder).separetor.text = separator
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (holder.itemViewType) {
            R.layout.item -> bindItem(holder,items[position] as ABC)
            R.layout.item_birthday -> bindItemBirthday(holder,items[position] as Birthday)
            R.layout.separator -> bindSeparator(holder,items[position] as String)
            else -> throw IllegalArgumentException()
        }


    override fun getItemCount() = items.count()
}

