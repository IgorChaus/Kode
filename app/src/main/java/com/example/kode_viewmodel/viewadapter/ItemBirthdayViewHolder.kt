package com.example.kode_viewmodel.viewadapter

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kode_viewmodel.databinding.ItemBirthdayBinding

class ItemBirthdayViewHolder(binding: ItemBirthdayBinding) : RecyclerView.ViewHolder(binding.root) {
    val personPhoto: ImageView = binding.imageView
    val personName: TextView = binding.personName
    val personTag: TextView = binding.personTag
    val personDepartment: TextView = binding.personDepartment
    val birthday: TextView = binding.personBirthday
}