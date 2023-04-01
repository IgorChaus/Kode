package com.example.kode_viewmodel.view

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kode_viewmodel.R
import com.example.kode_viewmodel.databinding.ItemBinding
import com.example.kode_viewmodel.databinding.ItemBirthdayBinding
import com.example.kode_viewmodel.databinding.SeparatorBinding
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
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item, parent, false))
        R.layout.item_birthday -> ItemBirthdayViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_birthday, parent, false))
        R.layout.separator -> SeparatorHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.separator, parent, false))
        R.layout.skeleton_item -> SkeletonHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.skeleton_item, parent, false))
        else -> throw IllegalArgumentException()
    }

    class ItemViewHolder(val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: ABC, itemClickListener: ItemClickListener, holder: RecyclerView.ViewHolder) {
            val path: String = item.avatarUrl
            Glide.with(binding.root.context).load(path).circleCrop()
                .into(binding.imageView)
            binding.personName.text = item.firstName + " " + item.lastName
            binding.personTag.text = " " + item.userTag.lowercase()
            binding.personDepartment.text = item.department
            binding.itemLayout.setOnClickListener{
                if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }
                itemClickListener.onItemClick(item)
            }
        }
    }

    class ItemBirthdayViewHolder(val binding: ItemBirthdayBinding)
        : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: Birthday, itemClickListener: ItemClickListener
                 ,holder: RecyclerView.ViewHolder) {
            val path: String = item.avatarUrl
            Glide.with(binding.root.context).load(path).circleCrop()
                .into(binding.imageView)
            binding.personName.text = item.firstName + " " + item.lastName
            binding.personTag.text = " " + item.userTag.lowercase()
            binding.personDepartment.text = item.department

            val date = LocalDate.parse(item.birthday)
            val formatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("dd MMM", Locale("ru"))

            binding.personBirthday.text = date.format(formatter)
            binding.itemBirthdayLayout.setOnClickListener{
                if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }
                itemClickListener.onItemClick(item)
            }
        }
    }

    class SeparatorHolder(val binding: SeparatorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(separator: Separator) {
            binding.year.text = separator.year
        }
    }

    class SkeletonHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (holder.itemViewType) {
            R.layout.item -> (holder as ItemViewHolder)
                .bind((items[position] as ABC),itemClickListener, holder)
            R.layout.item_birthday -> (holder as ItemBirthdayViewHolder)
                .bind((items[position] as Birthday),itemClickListener, holder)
            R.layout.separator -> (holder as SeparatorHolder).bind(items[position] as Separator)
            R.layout.skeleton_item -> Unit
            else -> throw IllegalArgumentException()
        }

    override fun getItemCount() = items.count()
}

