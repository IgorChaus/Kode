package com.example.kode_viewmodel.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kode_viewmodel.source.DataRepository

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("UNCHECKED_CAST")
class AppViewModelFactory(private val dataRepository: DataRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java))
            return AppViewModel(dataRepository) as T
        throw RuntimeException("Unknown ViewModel class $modelClass")
    }
}