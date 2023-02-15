package com.example.kode_viewmodel

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kode_viewmodel.model.*
import com.example.kode_viewmodel.source.DataRepository
import com.example.kode_viewmodel.vm.AppViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity(), RVAdapter.ItemClickListener {

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private var checkedBotton: Int = R.id.radioButton1

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

    private val dataRepository = DataRepository()
    val factory = AppViewModel.Factory(dataRepository)

    private val viewModel by lazy {ViewModelProvider(this,factory)
        .get(AppViewModel::class.java)}

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv: RecyclerView = findViewById(R.id.rv1)

        val llm = LinearLayoutManager(this)
        rv.layoutManager = llm

        val adapter = RVAdapter(this)
        rv.adapter = adapter

        window.setBackgroundDrawable(
            ContextCompat
            .getDrawable(this@MainActivity,R.color.white))

        //--------------------- SEARCH ----------------------
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val editText: EditText = findViewById(R.id.editText)

        val sortButton: ImageButton = findViewById(R.id.imageButton)
        sortButton.setOnClickListener {
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
            editText.setText("")

            //Hide keyboard
            val  imm = editText.context.getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)

            viewModel.filterSearch("")
            sortButton.visibility = View.VISIBLE

            editText.setCompoundDrawablesWithIntrinsicBounds(
                ResourcesCompat.getDrawable(resources, R.drawable.icon_search, null),
                null, null, null)

        }

        editText.addTextChangedListener {
                s ->  viewModel.filterSearch(s.toString())
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
                    viewModel.filterTab(tab?.text.toString())
            }
        })

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
            viewModel.sorting(checkedBotton)
            if (checkedBotton == R.id.radioButton2) {
                sortButton.setImageResource(R.drawable.icon_right_purple)
            } else {
                sortButton.setImageResource(R.drawable.icon_right)
            }
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }



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
                    adapter.refreshUsers(it.data!!)
                    swipeContainer.isRefreshing = false
                    snackbarLoading.dismiss()

                }

                is Resource.Error -> {
                    if(it.message == "IOException"){
                        snackbarError.show()
                        swipeContainer.isRefreshing = false
                    }else{
                        val intent = Intent(this@MainActivity,ErrorActivity::class.java)
                        Log.i("MyTag","Error" + it.message)
                        startActivity(intent)
                    }
                }

                is Resource.Loading -> snackbarLoading.show()

            }
        })

    }

    override fun onItemClick(item: Person.Items){
            val intent = Intent(this, Portfolio::class.java)

            intent.putExtra("path", item.avatarUrl)
            intent.putExtra("personName", item.firstName + " " + item.lastName)
            intent.putExtra("tag", item.userTag)
            intent.putExtra("department", item.department)
            intent.putExtra("birthday", item.birthday)
            intent.putExtra("phone", item.phone)

            startActivity(intent)

    }
}