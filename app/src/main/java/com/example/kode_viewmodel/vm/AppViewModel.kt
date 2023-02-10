package com.example.kode_viewmodel.vm

import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.kode_viewmodel.MainActivity.Companion.departments
import com.example.kode_viewmodel.source.RetrofitInstance
import com.example.kode_viewmodel.api.PersonApi
import com.example.kode_viewmodel.model.Person
import com.example.kode_viewmodel.model.Resource
import com.example.kode_viewmodel.source.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale.filter

class AppViewModel(private val dataRepository: DataRepository): ViewModel() {

    private var tabName: String = "Все"
    private var strSearch: String =""
    lateinit var  resourceItems: Resource<Person>

    class Factory(private val dataRepository: DataRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AppViewModel(dataRepository) as T
        }
    }

    val itemsLiveData: MutableLiveData<Resource<Person>> = MutableLiveData()

    fun fetchPersons(){
        viewModelScope.launch {
            itemsLiveData.postValue(Resource.Loading())
            resourceItems = dataRepository.getPersons()
            itemsLiveData.postValue(resourceItems)
        }
    }

    fun filterTab(tabName: String){
        this.tabName = tabName
        itemsLiveData.postValue(setFilter())
    }

    fun filterSearch(strSearch: String){
        this.strSearch = strSearch
        itemsLiveData.postValue(setFilter())
    }


    fun setFilter(): Resource<Person> {

        val filterTab: Resource<Person> = if (tabName == "Все") {
            resourceItems
        } else {
            Resource.Success(data =Person(resourceItems.data!!.items
                .filter { it.department == departments[tabName] }))

        }

        return filterTab








      /*  val itemsFilter: ArrayList<Person.Items> = if (strSearch.length > 1) {
            filterTab.filter {
                it.firstName.contains(strSearch, ignoreCase = true) ||
                        it.lastName.contains(strSearch, ignoreCase = true) ||
                        it.userTag.contains(strSearch, ignoreCase = true)
            } as ArrayList<Person.Items>
        }else{
            filterTab.filter {
                it.firstName.contains(strSearch, ignoreCase = true) ||
                        it.lastName.contains(strSearch, ignoreCase = true)
            } as ArrayList<Person.Items>
        }*/

        /*val emptyView: ConstraintLayout = findViewById(R.id.empty_view)
        if (itemsFilter.isEmpty()) {
            rv.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
        else {
            rv.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            adapter.setDataList(itemsFilter, checkedBotton)
        }*/

    }

}