package com.example.kode_viewmodel.view

import androidx.recyclerview.widget.DiffUtil
import com.example.kode_viewmodel.model.*

class DiffCallBack: DiffUtil.ItemCallback<IRow>() {
    override fun areItemsTheSame(oldItem: IRow, newItem: IRow): Boolean {
        return when{
            oldItem is ABC && newItem is ABC -> {
                oldItem.id == newItem.id
            }
            oldItem is Birthday && newItem is Birthday -> {
                oldItem.id == newItem.id
            }
            oldItem is Skeleton && newItem is Skeleton -> {
                true
            }
            oldItem is Separator && newItem is Separator -> {
                true
            }
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: IRow, newItem: IRow): Boolean {
        return when{
            oldItem is ABC && newItem is ABC -> {
                oldItem.equals(newItem)
            }
            oldItem is Birthday && newItem is Birthday -> {
                oldItem.equals(newItem)
            }
            oldItem is Skeleton && newItem is Skeleton -> {
                true
            }
            oldItem is Separator && newItem is Separator -> {
                true
            }
            else -> false
        }
    }
}