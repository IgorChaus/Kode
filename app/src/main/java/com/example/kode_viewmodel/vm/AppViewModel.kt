package com.example.kode_viewmodel.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kode_viewmodel.source.RetrofitInstance
import com.example.kode_viewmodel.api.PersonApi
import com.example.kode_viewmodel.model.Person
import com.example.kode_viewmodel.model.Resource
import com.example.kode_viewmodel.source.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppViewModel: ViewModel() {
    val itemsLiveData: MutableLiveData<Resource<Person>> = MutableLiveData()

    private val dataRepository = DataRepository()

    fun fetchPersons(){
        viewModelScope.launch {
            itemsLiveData.postValue(Resource.Loading())
            itemsLiveData.postValue(dataRepository.getPersons())

        }
    }

}