package com.example.kode_viewmodel

import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.kode_viewmodel.model.*
import com.example.kode_viewmodel.source.DataRepository
import com.example.kode_viewmodel.source.RetrofitInstance
import com.example.kode_viewmodel.viewmodel.AppViewModel
import com.example.kode_viewmodel.viewmodel.AppViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity(){

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataRepository = DataRepository(RetrofitInstance.service)
        val factory = AppViewModelFactory(dataRepository)
        //Создаем экземпляр viewModel в хранилище Activity
        ViewModelProvider(this, factory).get(AppViewModel::class.java)

        setContentView(R.layout.activity_main)

    }
}