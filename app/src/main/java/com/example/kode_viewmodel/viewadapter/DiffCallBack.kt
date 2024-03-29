package com.example.kode_viewmodel.viewadapter

import androidx.recyclerview.widget.DiffUtil
import com.example.kode_viewmodel.model.*

class DiffCallBack: DiffUtil.ItemCallback<AdapterItems>() {
    override fun areItemsTheSame(oldItem: AdapterItems, newItem: AdapterItems): Boolean {
        return when{
            oldItem is Ordinary && newItem is Ordinary -> {
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

    override fun areContentsTheSame(oldItem: AdapterItems, newItem: AdapterItems): Boolean {
        return when{
            oldItem is Ordinary && newItem is Ordinary -> {
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