package com.example.kode_viewmodel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kode_viewmodel.model.Person
import com.example.kode_viewmodel.model.Resource
import com.example.kode_viewmodel.vm.AppViewModel
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        val swipeContainer: SwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        swipeContainer.setProgressBackgroundColorSchemeColor(ResourcesCompat.getColor(resources,
            R.color.white,null))

        swipeContainer.setOnRefreshListener {
            viewModel.fetchPersons()
        }

        val snackbarLoading: Snackbar = Snackbar.make(rv,"Секундочку, гружусь...", Snackbar
            .LENGTH_INDEFINITE)
        snackbarLoading.setBackgroundTint(ResourcesCompat.getColor(resources,R.color.purple,null))
        snackbarLoading.setTextColor(ResourcesCompat.getColor(resources,R.color.white,null))

        val snackbarError: Snackbar = Snackbar.make(rv,"""Не могу обновить данные.
            |Проверьте соединение с Интернетом.""".trimMargin(), Snackbar.LENGTH_LONG)
        snackbarError.setBackgroundTint(ResourcesCompat.getColor(resources,R.color.red,null))
        snackbarError.setTextColor(ResourcesCompat.getColor(resources,R.color.white,null))

        viewModel.itemsLiveData.observe(this, Observer {
            when(it){
                is Resource.Success -> {
                    adapter.refreshUsers(it.data?.items as ArrayList<Person.Items>)
                    swipeContainer.isRefreshing = false
                    snackbarLoading.dismiss()
                }

                is Resource.Error -> {
                    if(it.message == "IOException"){
                        snackbarError.show()
                        swipeContainer.isRefreshing = false
                    }else{
                        val intent = Intent(this@MainActivity,ErrorActivity::class.java)
                        startActivity(intent)
                    }
                }

                is Resource.Loading -> snackbarLoading.show()

            }
        })

        viewModel.fetchPersons()

    }
}