package com.example.kode_viewmodel.vm

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kode_viewmodel.R
import com.example.kode_viewmodel.model.*
import com.example.kode_viewmodel.source.DataRepository
import com.example.kode_viewmodel.v.MainActivity.Companion.departments
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class AppViewModel(private val dataRepository: DataRepository): ViewModel() {

    val itemsLiveData: MutableLiveData<Resource<List<IRow>>> = MutableLiveData()

    private var tabName: String = "Все"
    private var strSearch: String = ""
    private var sorting: Int = R.id.radioButton1
    lateinit var  resourceItems: Resource<Person>

    init{
        val skelList = List(8){ Skeleton() }
        itemsLiveData.postValue(Resource.Success(skelList))
        firstFetchPersons()
    }

    class Factory(private val dataRepository: DataRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AppViewModel(dataRepository) as T
        }
    }


    fun fetchPersons(){
        viewModelScope.launch {
            itemsLiveData.postValue(Resource.Loading())
            val _resourceItems = dataRepository.getPersons()
            if(_resourceItems is Resource.Success) {
                resourceItems = _resourceItems
                itemsLiveData.postValue(sortPerson(setFilter()))
            }else
                itemsLiveData.postValue(Resource
                    .Error(_resourceItems.message ?:"Error json API"))
        }
    }

    fun firstFetchPersons(){
        viewModelScope.launch {
            val _resourceItems = dataRepository.getPersons()
            if(_resourceItems is Resource.Success) {
                resourceItems = _resourceItems
                itemsLiveData.postValue(sortPerson(setFilter()))
            }else
                itemsLiveData.postValue(Resource
                    .Error(_resourceItems.message ?:"Error json API"))
        }
    }

    fun filterTab(tabName: String){
        this.tabName = tabName
        itemsLiveData.postValue(sortPerson(setFilter()))
    }

    fun filterSearch(strSearch: String){
        this.strSearch = strSearch
        itemsLiveData.postValue(sortPerson(setFilter()))
    }

    fun sorting(sorting: Int){
        this.sorting = sorting
        itemsLiveData.postValue(sortPerson(setFilter()))
    }


    fun sortPerson(items:List<Person.Items>): Resource<List<IRow>> {
        val result: Resource<List<IRow>>

        if (sorting == R.id.radioButton1) {

            val listItems: List<ABC> = items.map {
                ABC(
                    it.id, it.avatarUrl, it.firstName,
                    it.lastName, it.userTag, it.department, it.position, it.birthday, it.phone
                )

            }

            val arraylistItems = ArrayList(listItems)

            arraylistItems.sortWith(
                compareBy({ it.firstName }, { it.lastName })
            )

            result = Resource.Success(arraylistItems)

        } else {
            val listItems: List<Birthday> = items.map {
                Birthday(
                    it.id, it.avatarUrl, it.firstName,
                    it.lastName, it.userTag, it.department, it.position, it.birthday, it.phone
                )
            }

            val arraylistItems = ArrayList(listItems)

            val formatMMDD: DateTimeFormatter = DateTimeFormatter.ofPattern("MMdd")
            val currentDate = LocalDate.now()

            arraylistItems.sortWith(
                compareBy { LocalDate.parse(it.birthday).format(formatMMDD) }
            )

            val sepItems: ArrayList<IRow> = arrayListOf()
            sepItems.addAll(arraylistItems.filter
            { LocalDate.parse(it.birthday).format(formatMMDD)  >= currentDate.format(formatMMDD)})

            val baseYear = LocalDate.of(currentDate.year, currentDate.month, currentDate.dayOfMonth)
            val nextYear = LocalDate.from(baseYear).plusYears(1)
            val formatYear: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY")
            sepItems.add(Separator(nextYear.format(formatYear)))

            sepItems.addAll(arraylistItems.filter
            { LocalDate.parse(it.birthday).format(formatMMDD)  < currentDate.format(formatMMDD)})

            result = Resource.Success(sepItems)
        }

        return result
    }


    fun setFilter(): List<Person.Items> {

         val filterTab: List<Person.Items> = if (tabName == "Все") {
             resourceItems.data!!.items
              } else {
                  resourceItems.data!!.items.filter { it.department == departments[tabName] }
              }

        val itemsFilter: List<Person.Items> = if (strSearch.length > 1) {
            filterTab.filter {
                it.firstName.contains(strSearch, ignoreCase = true) ||
                        it.lastName.contains(strSearch, ignoreCase = true) ||
                        it.userTag.contains(strSearch, ignoreCase = true)
            }
        }else{
            filterTab.filter {
                it.firstName.contains(strSearch, ignoreCase = true) ||
                        it.lastName.contains(strSearch, ignoreCase = true)
            }
        }

        return itemsFilter
    }
}


