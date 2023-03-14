package com.example.kode_viewmodel.view

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.kode_viewmodel.R
import com.example.kode_viewmodel.model.*
import com.example.kode_viewmodel.source.DataRepository
import com.example.kode_viewmodel.source.RetrofitInstance
import com.example.kode_viewmodel.viewmodel.AppViewModel

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity(){

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dataRepository = DataRepository(RetrofitInstance.service)
        val factory = AppViewModel.Factory(dataRepository)
        //Создаем экземпляр viewModel в хранилище Activity
        ViewModelProvider(this, factory).get(AppViewModel::class.java)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_activity, MainScreen.getInstance())
                .commitNow()
        }
    }
}