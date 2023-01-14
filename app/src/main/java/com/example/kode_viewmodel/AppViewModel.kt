package com.example.kode_viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppViewModel: ViewModel() {
    var items: MutableLiveData<ArrayList<Person.Items>> = MutableLiveData()
    //инициализируем список и заполняем его данными пользователей
    init {
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call: Call<Person> = apiService.getPersons()

        call.enqueue(object : Callback<Person> {
            override fun onResponse(call: Call<Person>, response: Response<Person>) {
                if(response.isSuccessful) {
                    items.value = response.body()?.items as ArrayList<Person.Items>
                }else{
                   Log.i("MyTag","Error load data")
                }
            }

            override fun onFailure(call: Call<Person>, t: Throwable) {
                Log.i("MyTag","Error load data")
            }
        })
    }

    fun getListUsers() = items

}