package com.example.kode_viewmodel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val viewModel by lazy {ViewModelProvider(this).get(AppViewModel::class.java)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv: RecyclerView = findViewById(R.id.rv1)

        val llm = LinearLayoutManager(this)
        rv.layoutManager = llm

        val adapter = RAdapter()
        rv.adapter = adapter

        viewModel.getListUsers().observe(this, Observer {
            it?.let {
                adapter.refreshUsers(it)
            }
        })



    }
}