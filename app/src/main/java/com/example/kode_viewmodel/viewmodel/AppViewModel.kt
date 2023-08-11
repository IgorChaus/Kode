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
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class AppViewModel @Inject constructor(private val dataRepository: DataRepository): ViewModel() {

    private val _itemList: MutableLiveData<Resource<List<IRow>>> = MutableLiveData()
    val itemList: LiveData<Resource<List<IRow>>>
        get() = _itemList

    private val _sortingType: MutableLiveData<String> = MutableLiveData()
    val sortingType: LiveData<String>
        get() = _sortingType

    private var tabName: String = ALL
    private var strSearch: String = EMPTY_STRING
    private var resourceItems: Resource<Person>? = null

    init{
        _sortingType.value = ALPHABET_SORTING
        val skeletonList = List(8){ Skeleton() }
        _itemList.value = Resource.Success(skeletonList,strSearch)
        fetchPersons()
    }

    suspend fun getPersonsFromRepository(){
        val _resourceItems = dataRepository.getPersons()
        if(_resourceItems is Resource.Success) {
            resourceItems = _resourceItems
            setFilter()?.let{
                _itemList.value = sortPerson(it)
            }
        }else
            _itemList.postValue(
                Resource
                    .Error(_resourceItems.message ?:"Error json API"))
    }

    fun fetchPersons(){
        viewModelScope.launch {
            _itemList.postValue(Resource.Loading())
            getPersonsFromRepository()
        }
    }

    fun setFilterTab(tabName: String){
        this.tabName = tabName
        setFilter()?.let{
            _itemList.value = sortPerson(it)
        }
    }

    fun setFilterSearch(strSearch: String){
        this.strSearch = strSearch
        setFilter()?.let{
            _itemList.value = sortPerson(it)
        }
    }

    fun changeSortingType(sortingType: String){
        _sortingType.value = sortingType
        setFilter()?.let{
            _itemList.value = sortPerson(it)
        }
    }


    private fun sortPerson(items:List<Person.Items>): Resource<List<IRow>> {
        val result: Resource<List<IRow>>

        if (sortingType.value == ALPHABET_SORTING) {

            val listItems: List<Ordinary> = items.map {
                Ordinary(
                    id = it.id,
                    avatarUrl = it.avatarUrl,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    userTag = it.userTag,
                    department = it.department,
                    position = it.position,
                    birthday = it.birthday,
                    phone = it.phone
                )
            }

            val arrayListItems = ArrayList(listItems)

            arrayListItems.sortWith(
                compareBy({ it.firstName }, { it.lastName })
            )

            result = Resource.Success(arrayListItems, strSearch)

        } else {
            val listItems: List<Birthday> = items.map {
                Birthday(
                    id = it.id,
                    avatarUrl = it.avatarUrl,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    userTag = it.userTag,
                    department = it.department,
                    position = it.position,
                    birthday = it.birthday,
                    phone = it.phone
                )
            }

            val arrayListItems = ArrayList(listItems)

            val formatMMDD: DateTimeFormatter = DateTimeFormatter.ofPattern("MMdd")
            val currentDate = LocalDate.now()

            arrayListItems.sortWith(
                compareBy { LocalDate.parse(it.birthday).format(formatMMDD) }
            )

            val separatedListItems: ArrayList<IRow> = arrayListOf()
            separatedListItems.addAll(arrayListItems.filter
            { LocalDate.parse(it.birthday).format(formatMMDD)  >= currentDate.format(formatMMDD)})

            val baseYear = LocalDate.of(currentDate.year, currentDate.month, currentDate.dayOfMonth)
            val nextYear = LocalDate.from(baseYear).plusYears(1)
            val formatYear: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY")

            if(items.isNotEmpty())
                separatedListItems.add(Separator(nextYear.format(formatYear)))

            separatedListItems.addAll(arrayListItems.filter
            { LocalDate.parse(it.birthday).format(formatMMDD)  < currentDate.format(formatMMDD)})

            result = Resource.Success(separatedListItems, strSearch)

        }

        return result
    }


    private fun setFilter(): List<Person.Items>? {

         val listFilterTab = if (tabName == ALL) {
             resourceItems?.data?.items
         } else {
             resourceItems?.data?.items?.filter { it.department == departments[tabName] }
         }

        val listFilterSearch = if (strSearch.length > 1) {
            listFilterTab?.filter {
                it.firstName.contains(strSearch, ignoreCase = true) ||
                        it.lastName.contains(strSearch, ignoreCase = true) ||
                        it.userTag.contains(strSearch, ignoreCase = true)
            }
        }else{
            listFilterTab?.filter {
                it.firstName.contains(strSearch, ignoreCase = true) ||
                        it.lastName.contains(strSearch, ignoreCase = true)
            }
        }

        return listFilterSearch
    }

    companion object{
        const val ALPHABET_SORTING = "alphabet"
        const val BIRTHDAY_SORTING = "birthday"
        const val ALL = "Все"
        const val EMPTY_STRING = ""
    }
}


