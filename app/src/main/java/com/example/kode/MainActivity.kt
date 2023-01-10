package com.example.kode

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private var items = ArrayList<Person.Items>()
    private var tabName: String = "Все"
    private var strSearch: String =""

    private var checkedBotton: Int = R.id.radioButton1

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>


    companion object {
        val departments = mapOf(
            "Все" to "All",
            "Android" to "android",
            "iOS" to "ios",
            "Дизайн" to "design",
            "Менеджмент" to "management",
            "QA" to "qa",
            "Бэк-офис" to "back_office",
            "Frontend" to "frontend",
            "HR" to "hr",
            "PR" to "pr",
            "Backend" to "backend",
            "Техподдержка" to "support",
            "Аналитика" to "analytics"
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val skelAdapter = SkeletonAdapter()

        val rv: RecyclerView = findViewById(R.id.rv1)

        val llm = LinearLayoutManager(this)
        rv.layoutManager = llm

        val adapter = RAdapter(items)
        rv.adapter = skelAdapter


        window.setBackgroundDrawable(ContextCompat
            .getDrawable(this@MainActivity,R.color.white))
        //--------------------- SEARCH ----------------------
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val editText: EditText = findViewById(R.id.editText)

        val sortButton: ImageButton = findViewById(R.id.imageButton)
        sortButton.setOnClickListener {
            Log.i("MyTag",sheetBehavior.state.toString())
            when(sheetBehavior.state){
                BottomSheetBehavior.STATE_COLLAPSED ->
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                BottomSheetBehavior.STATE_EXPANDED ->
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                else -> TODO()
            }
        }

        val buttonCancel: Button = findViewById(R.id.button)
        buttonCancel.setOnClickListener {
            editText.clearFocus()
            buttonCancel.visibility = View.GONE
            strSearch = ""
            editText.setText(strSearch)

            //Hide keyboard
            val  imm = editText.context.getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)

            setFilter(rv,adapter)
            sortButton.visibility = View.VISIBLE

            editText.setCompoundDrawablesWithIntrinsicBounds(
                ResourcesCompat.getDrawable(resources, R.drawable.icon_search, null),
                null, null, null)

        }

        editText.addTextChangedListener {
                s ->  strSearch = s.toString()
                setFilter(rv,adapter)
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                buttonCancel.visibility = View.VISIBLE
                sortButton.visibility = View.GONE
                editText.setCompoundDrawablesWithIntrinsicBounds(
                    ResourcesCompat.getDrawable(resources, R.drawable.icon_search_black, null),
                    null, null, null)
            } else {
                buttonCancel.visibility = View.GONE
                sortButton.visibility = View.VISIBLE
            }
        }


        //---------------- TABS ---------------------------
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        departments.forEach{
            tabLayout.addTab(tabLayout.newTab().setText(it.key))
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabName = tab?.text.toString()
                setFilter(rv,adapter)
            }

        })

        //---------------- FIRST FILLING DATA ----------------------
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call: Call<Person> = apiService.getPersons()

        call.enqueue(object : Callback<Person> {
            override fun onResponse(call: Call<Person>, response: Response<Person>) {
                if(response.isSuccessful) {
                    items = response.body()?.items as ArrayList<Person.Items>
                    rv.adapter = adapter
                    adapter.setDataList(items, checkedBotton)
                }else{
                    val intent = Intent(this@MainActivity,ErrorActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<Person>, t: Throwable) {
                val intent = Intent(this@MainActivity,ErrorActivity::class.java)
                startActivity(intent)
            }
        })

        // --------------- UPDATE DATA -------------------------------------------
        val snackbarLoading: Snackbar = Snackbar.make(rv,"Секундочку, гружусь...",Snackbar
            .LENGTH_INDEFINITE)
        snackbarLoading.setBackgroundTint(ResourcesCompat.getColor(resources,R.color.purple,null))
        snackbarLoading.setTextColor(ResourcesCompat.getColor(resources,R.color.white,null))

        val snackbarError: Snackbar = Snackbar.make(rv,"""Не могу обновить данные.
            |Проверьте соединение с Интернетом.""".trimMargin(),Snackbar.LENGTH_LONG)
        snackbarError.setBackgroundTint(ResourcesCompat.getColor(resources,R.color.purple,null))
        snackbarError.setTextColor(ResourcesCompat.getColor(resources,R.color.white,null))

        val swipeContainer: SwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        swipeContainer.setProgressBackgroundColorSchemeColor(ResourcesCompat.getColor(resources,
            R.color.white,null))

        swipeContainer.setOnRefreshListener {
                snackbarLoading.show()
                val apiGetClients = ApiClient.getClient().create(ApiInterface::class.java)
                val callResult: Call<Person> = apiGetClients.getPersons()
                callResult.enqueue(object : Callback<Person> {
                    override fun onResponse(call: Call<Person>, response: Response<Person>) {
                        if(response.isSuccessful){
                            items.clear()
                            items = response.body()?.items as ArrayList<Person.Items>
                            setFilter(rv,adapter)
                            snackbarLoading.dismiss()
                        }else{
                            snackbarLoading.dismiss()
                            snackbarError.show()
                        }
                        swipeContainer.isRefreshing = false
                    }

                    override fun onFailure(call: Call<Person>, t: Throwable) {
                        snackbarLoading.dismiss()
                        snackbarError.show()
                        swipeContainer.isRefreshing = false
                    }
                })
            }

        //----------------- BOTTOM SHEET --------------------------------
        val bottomSheet: ConstraintLayout = findViewById(R.id.bottomSheet)
        sheetBehavior = BottomSheetBehavior.from(bottomSheet)

        sheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                val checkBotton: RadioButton = findViewById(checkedBotton)
                checkBotton.isChecked = true

                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        window.setBackgroundDrawable(ContextCompat
                            .getDrawable(this@MainActivity,R.color.grey_transpar))
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        window.setBackgroundDrawable(ContextCompat
                            .getDrawable(this@MainActivity,R.color.white))
                    }
                    else -> return
                }
            }
        })

        val radioGroup: RadioGroup = findViewById(R.id.radioGroup)
        radioGroup.clearCheck()
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            checkedBotton = checkedId
            setFilter(rv, adapter)
            if (checkedBotton == R.id.radioButton2) {
                sortButton.setImageResource(R.drawable.icon_right_purple)
            } else {
                sortButton.setImageResource(R.drawable.icon_right)
            }
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setFilter(rv: RecyclerView, adapter: RAdapter){

        val filterTab: ArrayList<Person.Items> = if (tabName == "Все") {
            items
        }else {
            items.filter { it.department == departments[tabName] }
                    as ArrayList<Person.Items>
        }

        val itemsFilter: ArrayList<Person.Items> = if (strSearch.length > 1) {
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
        }

        val emptyView: ConstraintLayout = findViewById(R.id.empty_view)
        if (itemsFilter.isEmpty()) {
            rv.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
        else {
            rv.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            adapter.setDataList(itemsFilter, checkedBotton)
        }
    }

}