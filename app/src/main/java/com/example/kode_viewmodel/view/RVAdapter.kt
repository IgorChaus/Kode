package com.example.kode_viewmodel.view

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
import com.example.kode_viewmodel.R
import com.example.kode_viewmodel.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class RVAdapter(private val itemClickListener: ItemClickListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(item: Person.Items)
    }

    private var  items: List<IRow> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun refreshUsers(items: List<IRow>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is Birthday -> R.layout.item_birthday
            is ABC -> R.layout.item
            is Separator -> R.layout.separator
            is Skeleton -> R.layout.skeleton_item
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
        R.layout.skeleton_item -> SkeletonHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.skeleton_item, parent, false))
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

    class SkeletonHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

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
        holder.itemView.setOnClickListener {
            if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }
            itemClickListener.onItemClick(item)
        }
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
        holder.itemView.setOnClickListener {
            if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }
            itemClickListener.onItemClick(item)
        }
    }


    private fun bindSeparator(holder: RecyclerView.ViewHolder, separator: Separator) {
        (holder as SeparatorHolder).separetor.text = separator.year
    }

    private fun bindSkeleton(){
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (holder.itemViewType) {
            R.layout.item -> bindItem(holder,items[position] as ABC)
            R.layout.item_birthday -> bindItemBirthday(holder,items[position] as Birthday)
            R.layout.separator -> bindSeparator(holder,items[position] as Separator)
            R.layout.skeleton_item -> bindSkeleton()
            else -> throw IllegalArgumentException()
        }

    override fun getItemCount() = items.count()
}

