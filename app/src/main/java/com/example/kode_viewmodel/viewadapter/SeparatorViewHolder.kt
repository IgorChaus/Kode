package com.example.kode_viewmodel.viewadapter

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kode_viewmodel.databinding.SeparatorBinding

class SeparatorViewHolder(binding: SeparatorBinding) : RecyclerView.ViewHolder(binding.root) {
    val separetor: TextView = binding.year
}