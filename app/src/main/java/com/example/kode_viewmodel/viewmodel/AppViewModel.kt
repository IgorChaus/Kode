package com.example.kode_viewmodel.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.kode_viewmodel.model.*
import com.example.kode_viewmodel.source.DataRepository
import com.example.kode_viewmodel.view.MainScreen.Companion.departments
import com.example.kode_viewmodel.wrappers.Response
import com.example.kode_viewmodel.wrappers.State
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class AppViewModel @Inject constructor(private val dataRepository: DataRepository) : ViewModel() {

    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State>
        get() = _state

    private val _sortingType: MutableLiveData<String> = MutableLiveData()
    val sortingType: LiveData<String>
        get() = _sortingType

    private var strSearch = EMPTY_STRING

    private var tabName: String = ALL

    private var listPersons: List<Person.Items>? = null

    init {
        _sortingType.value = ALPHABET_SORTING
        val skeletonList = List(8) { Skeleton() }
        _state.value = State.Content(skeletonList)
        fetchPersons()
    }

    private suspend fun getPersonsFromRepository() {
        val responce = dataRepository.getPersons()
        when (responce) {
            is Response.Success -> {
                listPersons = responce.data.items
                setScreenContent()
            }
            is Response.Error -> {
                _state.postValue(State.Error(responce.errorMessage))
            }
        }
    }

    fun fetchPersons() {
        viewModelScope.launch {
            _state.postValue(State.Loading)
            getPersonsFromRepository()
        }
    }

    fun setFilterTab(tabName: String) {
        this.tabName = tabName
        setScreenContent()
    }

    fun setFilterSearch(strSearch: String) {
        this.strSearch = strSearch
        setScreenContent()
    }

    fun changeSortingType(sortingType: String) {
        _sortingType.value = sortingType
        setScreenContent()
    }

    fun setScreenContent(){
        if (setFilter()?.isEmpty() == true && strSearch != "") {
            _state.value = State.NothingFound
        } else {
            setFilter()?.let {
                _state.value = sortPerson(it)
            }
        }
    }


    private fun sortPerson(items: List<Person.Items>): State {
        val result: State

        if (sortingType.value == ALPHABET_SORTING) {

            val listItems = items.map {
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

            result = State.Content(arrayListItems)

        } else {
            val listItems  = items.map {
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

            val separatedListItems: ArrayList<AdapterItems> = arrayListOf()
            separatedListItems.addAll(arrayListItems.filter
            {
                LocalDate.parse(it.birthday).format(formatMMDD) >= currentDate.format(formatMMDD)
            })

            val baseYear = LocalDate.of(currentDate.year, currentDate.month, currentDate.dayOfMonth)
            val nextYear = LocalDate.from(baseYear).plusYears(1)
            val formatYear: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY")

            if (items.isNotEmpty())
                separatedListItems.add(Separator(nextYear.format(formatYear)))

            separatedListItems.addAll(arrayListItems.filter
            {
                LocalDate.parse(it.birthday).format(formatMMDD) < currentDate.format(formatMMDD)
            })

            result = State.Content(separatedListItems)

        }

        return result
    }


    private fun setFilter(): List<Person.Items>? {

        val listFilterTab = if (tabName == ALL) {
            listPersons
        } else {
            listPersons?.filter { it.department == departments[tabName] }
        }

        val listFilterSearch = if (strSearch.length > 1) {
            listFilterTab?.filter {
                it.firstName.contains(strSearch, ignoreCase = true) ||
                        it.lastName.contains(strSearch, ignoreCase = true) ||
                        it.userTag.contains(strSearch, ignoreCase = true)
            }
        } else {
            listFilterTab?.filter {
                it.firstName.contains(strSearch, ignoreCase = true) ||
                        it.lastName.contains(strSearch, ignoreCase = true)
            }
        }

        return listFilterSearch
    }

    companion object {
        const val ALPHABET_SORTING = "alphabet"
        const val BIRTHDAY_SORTING = "birthday"
        const val ALL = "Все"
        const val EMPTY_STRING = ""
    }
}


