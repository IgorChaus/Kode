package com.example.kode_viewmodel.view

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ListAdapter
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

@RequiresApi(Build.VERSION_CODES.O)
class RVAdapter : ListAdapter<IRow, RecyclerView.ViewHolder>(DiffCallBack()) {

    var itemClickListener: ((Person.Items) -> Unit)? = null

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Birthday -> R.layout.item_birthday
            is ABC -> R.layout.item
            is Separator -> R.layout.separator
            is Skeleton -> R.layout.skeleton_item
            else -> throw RuntimeException("Illegal item type")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {

        R.layout.item -> ItemViewHolder(
            ItemBinding.inflate(LayoutInflater
                .from(parent.context), parent, false))
      R.layout.item_birthday -> ItemBirthdayViewHolder(
          ItemBirthdayBinding.inflate(LayoutInflater
              .from(parent.context), parent, false))
      R.layout.separator -> SeparatorHolder(
          SeparatorBinding.inflate(LayoutInflater
              .from(parent.context), parent, false))
      R.layout.skeleton_item -> SkeletonHolder(
          LayoutInflater.from(parent.context)
              .inflate(R.layout.skeleton_item, parent, false))
      else -> throw IllegalArgumentException()
    }

    class ItemViewHolder(binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val personPhoto: ImageView = binding.imageView
        val personName: TextView = binding.personName
        val personTag: TextView = binding.personTag
        val personDepartment: TextView = binding.personDepartment
    }

    class ItemBirthdayViewHolder(binding: ItemBirthdayBinding) : RecyclerView.ViewHolder(binding.root) {
        val personPhoto: ImageView = binding.imageView
        val personName: TextView = binding.personName
        val personTag: TextView = binding.personTag
        val personDepartment: TextView = binding.personDepartment
        val birthday: TextView = binding.personBirthday
    }

    class SeparatorHolder(binding: SeparatorBinding) : RecyclerView.ViewHolder(binding.root) {
        val separetor: TextView = binding.year
    }

    class SkeletonHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    @SuppressLint("SetTextI18n")
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
            itemClickListener?.invoke(item)
        }
    }

    @SuppressLint("SetTextI18n")
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
            itemClickListener?.invoke(item)
        }
    }


    private fun bindSeparator(holder: RecyclerView.ViewHolder, separator: Separator) {
        (holder as SeparatorHolder).separetor.text = separator.year
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (holder.itemViewType) {
            R.layout.item -> bindItem(holder,getItem(position) as ABC)
            R.layout.item_birthday -> bindItemBirthday(holder,getItem(position) as Birthday)
            R.layout.separator -> bindSeparator(holder,getItem(position) as Separator)
            R.layout.skeleton_item -> Unit
            else -> throw RuntimeException("Illegal item type")
        }
}

