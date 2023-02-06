package com.example.kode_viewmodel.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kode_viewmodel.source.RetrofitInstance
import com.example.kode_viewmodel.api.PersonApi
import com.example.kode_viewmodel.model.Person
import com.example.kode_viewmodel.model.Resource
import com.example.kode_viewmodel.source.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppViewModel(private val dataRepository: DataRepository): ViewModel() {

    class Factory(private val dataRepository: DataRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AppViewModel(dataRepository) as T
        }
    }

    val itemsLiveData: MutableLiveData<Resource<Person>> = MutableLiveData()

    fun fetchPersons(){
        viewModelScope.launch {
            itemsLiveData.postValue(Resource.Loading())
            itemsLiveData.postValue(dataRepository.getPersons())

        }
    }

}