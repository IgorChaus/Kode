package com.example.kode_viewmodel.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.kode_viewmodel.model.*
import com.example.kode_viewmodel.source.DataRepository
import com.example.kode_viewmodel.view.MainScreen.Companion.departments
import com.example.kode_viewmodel.wrappers.Resource
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class AppViewModel(private val dataRepository: DataRepository): ViewModel() {

    // Object must be observable but with a private setter, so we separate LiveData's objects
    private val itemsDataEmitter: MutableLiveData<Resource<List<IRow>>> = MutableLiveData()
    val itemsLiveData: LiveData<Resource<List<IRow>>> = itemsDataEmitter

    private val _sortingType: MutableLiveData<String> = MutableLiveData()
    val sortingType: LiveData<String> = _sortingType

    private var tabName: String = ALL
    private var strSearch: String = EMPTY_STRING
    lateinit var  resourceItems: Resource<Person>

    init{
        _sortingType.value = ALPHABET_SORTING
        val skelList = List(8){ Skeleton() }
        itemsDataEmitter.postValue(Resource.Success(skelList,strSearch))
    }

    suspend fun getPersonsFromRepository(){
        val _resourceItems = dataRepository.getPersons()
        if(_resourceItems is Resource.Success) {
            resourceItems = _resourceItems
            itemsDataEmitter.postValue(sortPerson(setFilter()))
        }else
            itemsDataEmitter.postValue(
                Resource
                    .Error(_resourceItems.message ?:"Error json API"))
    }

    fun fetchPersons(){
        viewModelScope.launch {
            itemsDataEmitter.postValue(Resource.Loading())
            getPersonsFromRepository()
        }
    }

    fun firstFetchPersons(){
        if (!this::resourceItems.isInitialized) {
            viewModelScope.launch {
                getPersonsFromRepository()
            }
        }
    }

    fun filterTab(tabName: String){
        this.tabName = tabName
        itemsDataEmitter.postValue(sortPerson(setFilter()))
    }

    fun filterSearch(strSearch: String){
        this.strSearch = strSearch
        itemsDataEmitter.postValue(sortPerson(setFilter()))
    }

    fun changeSortingType(sortingType: String){
        _sortingType.value = sortingType
        itemsDataEmitter.postValue(sortPerson(setFilter()))
    }


    fun sortPerson(items:List<Person.Items>): Resource<List<IRow>> {
        val result: Resource<List<IRow>>

        if (sortingType.value == ALPHABET_SORTING) {

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

            result = Resource.Success(arraylistItems, strSearch)

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

            if(items.isNotEmpty())
                sepItems.add(Separator(nextYear.format(formatYear)))

            sepItems.addAll(arraylistItems.filter
            { LocalDate.parse(it.birthday).format(formatMMDD)  < currentDate.format(formatMMDD)})

            result = Resource.Success(sepItems, strSearch)

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

    companion object{
        const val ALPHABET_SORTING = "alphabet"
        const val BIRTHDAY_SORTING = "birthday"
        const val ALL = "Все"
        const val EMPTY_STRING = ""
    }
}


