package com.example.kode

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayout
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
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

        val listView = arrayListOf<View>()
        showSkeleton(listView)

        val rv: RecyclerView = findViewById(R.id.rv1)

        val llm = LinearLayoutManager(this)
        rv.setLayoutManager(llm)

        val adapter = RAdapter(items)
        rv.setAdapter(adapter)

        //--------------------- SEARCH ----------------------
        val editText: EditText = findViewById(R.id.editText)

        val sortButton: ImageButton = findViewById(R.id.imageButton)
        sortButton.setOnClickListener {
            when(sheetBehavior.state){
                BottomSheetBehavior.STATE_COLLAPSED ->
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                BottomSheetBehavior.STATE_EXPANDED ->
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }

        val buttonCancel: Button = findViewById(R.id.button)
        buttonCancel.setOnClickListener {
            editText.clearFocus()
            buttonCancel.visibility = View.GONE
            strSearch = ""
            editText.setText(strSearch)

            //Hide keyboard
            val  imm = editText.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0)

            setFilter(rv,adapter)
            sortButton.visibility = View.VISIBLE

            editText.setCompoundDrawablesWithIntrinsicBounds(
                ResourcesCompat.getDrawable(getResources(), R.drawable.icon_search, null),
                null, null, null)

        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                strSearch = s.toString()
                setFilter(rv,adapter)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        editText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                buttonCancel.visibility = View.VISIBLE
                sortButton.visibility = View.GONE
                editText.setCompoundDrawablesWithIntrinsicBounds(
                    ResourcesCompat.getDrawable(getResources(), R.drawable.icon_search_black, null),
                    null, null, null)
            } else {
                buttonCancel.visibility = View.GONE
                sortButton.visibility = View.VISIBLE
            }
        }


        //---------------- TABS ---------------------------
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

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
        var apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call: Call<Person> = apiService.getPersons()

        call.enqueue(object : Callback<Person> {
            override fun onResponse(call: Call<Person>, response: Response<Person>) {
                if(response.isSuccessful) {
                    items = response.body()?.items as ArrayList<Person.Items>
                    hideSkeleton(listView)
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
        snackbarLoading.setBackgroundTint(Color.parseColor("#6534FF"))
        snackbarLoading.setTextColor(Color.WHITE)

        val snackbarError: Snackbar = Snackbar.make(rv,"""Не могу обновить данные.
            |Проверьте соединение с Интернетом.""".trimMargin(),Snackbar.LENGTH_LONG)
        snackbarError.setBackgroundTint(Color.parseColor("#F44336"))
        snackbarError.setTextColor(Color.WHITE)

        val swipeContainer: SwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        swipeContainer.setProgressBackgroundColorSchemeColor(Color.parseColor("#FFFFFF"))

        swipeContainer.setOnRefreshListener {
                snackbarLoading.show()
                var apiService = ApiClient.getClient().create(ApiInterface::class.java)
                val call: Call<Person> = apiService.getPersons()
                call.enqueue(object : Callback<Person> {
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
                        swipeContainer.setRefreshing(false)
                    }

                    override fun onFailure(call: Call<Person>, t: Throwable) {
                        Log.i("MyTag", "Response = " + t);
                        snackbarLoading.dismiss()
                        snackbarError.show()
                        swipeContainer.setRefreshing(false)
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

                var checkBotton: RadioButton = findViewById(checkedBotton)
                checkBotton.isChecked = true

                var linLayout: LinearLayout = findViewById(R.id.linLayout)
                linLayout.background = ColorDrawable(Color.parseColor("#29050510"))
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED ->
                        linLayout.background = ColorDrawable(Color.parseColor("#29050510"))
                    BottomSheetBehavior.STATE_COLLAPSED ->
                        linLayout.background = ColorDrawable(Color.parseColor("#FFFFFF"))
                    else -> linLayout.background = ColorDrawable(Color.parseColor("#FFFFFF"))
                }
            }
        })

        val radioGroup: RadioGroup = findViewById(R.id.radioGroup)
        radioGroup.clearCheck()
        radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup, checkedId: Int){
                checkedBotton = checkedId
                setFilter(rv,adapter)
                if (checkedBotton == R.id.radioButton2){
                    sortButton.setImageResource(R.drawable.icon_right_purple)
                }else {
                    sortButton.setImageResource(R.drawable.icon_right)
                }
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)

            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setFilter(rv: RecyclerView, adapter: RAdapter){

        val filterTab: ArrayList<Person.Items>

        if (tabName == "Все") {
            filterTab = items
        }else {
            filterTab = items.filter { it.department == departments[tabName] }
                    as ArrayList<Person.Items>
        }

        val itemsFilter: ArrayList<Person.Items>
        if (strSearch.length > 1) {
            itemsFilter = filterTab.filter {
                it.firstName.contains(strSearch, ignoreCase = true) ||
                        it.lastName.contains(strSearch, ignoreCase = true) ||
                        it.userTag.contains(strSearch, ignoreCase = true)
            } as ArrayList<Person.Items>
        }else{
            itemsFilter = filterTab.filter {
                it.firstName.contains(strSearch, ignoreCase = true) ||
                        it.lastName.contains(strSearch, ignoreCase = true)
            } as ArrayList<Person.Items>
        }

        val emptyView: ConstraintLayout = findViewById(R.id.empty_view)
        if (itemsFilter.isEmpty()) {
            rv.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            rv.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            adapter.setDataList(itemsFilter, checkedBotton)
        }
    }

    fun showSkeleton(listView: ArrayList<View>){
        var view :View
        val ltInflater: LayoutInflater = getLayoutInflater();
        val linLayout: LinearLayout = findViewById(R.id.linLayout)

        val metrics: DisplayMetrics = this.getResources().getDisplayMetrics()
        val heightDpi = ((metrics.heightPixels / metrics.density).toInt()).toDouble()
        var counter = (Math.ceil(heightDpi / 102)).toInt() //Get hight of item

        while (counter >= 1){
            view = ltInflater.inflate(R.layout.skeleton_item, null, false)
            linLayout.addView(view)
            listView.add(view)
            counter--
        }
    }

    fun hideSkeleton(listView: ArrayList<View>){
        for(i in listView){
            i.setVisibility(View.GONE)
        }
    }


}