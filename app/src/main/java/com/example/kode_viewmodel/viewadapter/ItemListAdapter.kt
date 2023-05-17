package com.example.kode_viewmodel.viewadapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
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
class ItemListAdapter : ListAdapter<IRow, RecyclerView.ViewHolder>(DiffCallBack()) {

    var itemClickListener: ((Person.Items) -> Unit)? = null

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is ABC -> ITEM_ORDINARY
            is Birthday -> ITEM_BIRHDAY
            is Separator -> ITEM_SEPARATOR
            is Skeleton -> ITEM_SKELETON
            else -> throw RuntimeException("Illegal item type")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {

        ITEM_ORDINARY -> ItemViewHolder(
            ItemBinding.inflate(
                LayoutInflater
                    .from(parent.context), parent, false
            )
        )
        ITEM_BIRHDAY -> ItemBirthdayViewHolder(
            ItemBirthdayBinding.inflate(
                LayoutInflater
                    .from(parent.context), parent, false
            )
        )
        ITEM_SEPARATOR -> SeparatorViewHolder(
            SeparatorBinding.inflate(
                LayoutInflater
                    .from(parent.context), parent, false
            )
        )
        ITEM_SKELETON -> SkeletonViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.skeleton_item, parent, false)
        )
        else -> throw throw RuntimeException("Illegal item type")
    }

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
        (holder as SeparatorViewHolder).separetor.text = separator.year
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (holder.itemViewType) {
            ITEM_ORDINARY -> bindItem(holder, getItem(position) as ABC)
            ITEM_BIRHDAY -> bindItemBirthday(holder, getItem(position) as Birthday)
            ITEM_SEPARATOR -> bindSeparator(holder, getItem(position) as Separator)
            ITEM_SKELETON -> Unit
            else -> throw RuntimeException("Illegal item type")
        }

    companion object {
        private const val ITEM_ORDINARY = 1
        private const val ITEM_BIRHDAY = 2
        private const val ITEM_SEPARATOR = 3
        private const val ITEM_SKELETON = 4
    }
}