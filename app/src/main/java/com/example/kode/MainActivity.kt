package com.example.kode

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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
import com.google.android.material.bottomsheet.BottomSheetBehavior

private const val DRAWABLE_LEFT_INDEX = 0
private const val DRAWABLE_RIGHT_INDEX = 2

class MainActivity : AppCompatActivity() {

    var items = ArrayList<Person.Items>()
    var tabName: String = "Все"
    var strSearch: String =""

    var checkedBotton: Int = R.id.radioButton1

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    val departments = mapOf("Все" to "All",
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
        "Аналитика" to "analytics")

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        val editText: EditText = findViewById(R.id.editText)

        editText.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when {
                    rightDrawableClicked(event, v as EditText) -> {
                        editText.setText("")
                        return true
                    }
                    leftDrawableClicked(event,v as EditText) -> {

                        //Hide keyboard
                        val  imm = v.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0)

                        return true

                    }else -> return false
                }
            }
        })


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)

        for(i in departments) {
            tabLayout.addTab(tabLayout.newTab().setText(i.key))
        }

        val listView = arrayListOf<View>()
        showSkeleton(listView)

        val rv: RecyclerView = findViewById(R.id.rv1)

        val llm: LinearLayoutManager = LinearLayoutManager(this)
        rv.setLayoutManager(llm)

        val adapter: RAdapter = RAdapter(items)
        rv.setAdapter(adapter)

        var apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call: Call<Person> = apiService.getPersons()

        call.enqueue(object : Callback<Person> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<Person>, response: Response<Person>) {
                if(response.isSuccessful) {
                    items = response.body()?.items as ArrayList<Person.Items>
                    hideSkeleton(listView)
                    adapter.setMovieList(items, checkedBotton)
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

        editText.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun afterTextChanged(s: Editable?) {
                strSearch = s.toString()
                adapter.setMovieList(setFilter(), checkedBotton)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val swipeContainer: SwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        swipeContainer.setProgressBackgroundColorSchemeColor(Color.parseColor("#FFFFFF"))
        swipeContainer.setOnRefreshListener {
                var apiService = ApiClient.getClient().create(ApiInterface::class.java)
                val call: Call<Person> = apiService.getPersons()
                call.enqueue(object : Callback<Person> {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onResponse(call: Call<Person>, response: Response<Person>) {
                        if(response.isSuccessful){
                            items.clear()
                            items = response.body()?.items as ArrayList<Person.Items>
                            adapter.setMovieList(setFilter(), checkedBotton)
                        }
                        swipeContainer.setRefreshing(false)
                    }

                    override fun onFailure(call: Call<Person>, t: Throwable) {
                        Log.i("MyTag", "Response = " + t);
                        swipeContainer.setRefreshing(false)
                    }
                })

            }



        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabName = tab?.text.toString()
                adapter.setMovieList(setFilter(), checkedBotton)
            }

        })


        val radioGroup: RadioGroup = findViewById(R.id.radioGroup)
        radioGroup.clearCheck()
        radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onCheckedChanged(group: RadioGroup, checkedId: Int){
                checkedBotton = checkedId
                adapter.setMovieList(setFilter(), checkedBotton)
            }
        })

    }

    fun setFilter(): ArrayList<Person.Items>{

        val filterTab: ArrayList<Person.Items>

        if (tabName == "Все") {
            filterTab = items
        }else {
            filterTab = items.filter { it.department == departments[tabName] }
                    as ArrayList<Person.Items>
        }

        if (strSearch.length > 1) {
            return filterTab.filter {
                it.firstName.contains(strSearch, ignoreCase = true) ||
                        it.lastName.contains(strSearch, ignoreCase = true) ||
                        it.userTag.contains(strSearch, ignoreCase = true)
            } as ArrayList<Person.Items>

        }else{
            return filterTab.filter {
                it.firstName.contains(strSearch, ignoreCase = true) ||
                        it.lastName.contains(strSearch, ignoreCase = true)
            } as ArrayList<Person.Items>
        }
    }

    //Get hight of screen
    fun getSkeletonRowCount(context: Context): Int{
        val metrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        val heightDpi = ((metrics.heightPixels / metrics.density).toInt()).toDouble()
        val amount = (Math.ceil(heightDpi / 102)).toInt() //Get hight of item
        return amount
    }

    fun showSkeleton(listView: ArrayList<View>){
        //Skeleton
        var view :View
        val ltInflater: LayoutInflater = getLayoutInflater();
        val linLayout: LinearLayout = findViewById(R.id.linLayout)
        var counter = getSkeletonRowCount(this)

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

    fun leftDrawableClicked(event: MotionEvent, view: EditText): Boolean {

        val leftDrawable = view.compoundDrawables[DRAWABLE_LEFT_INDEX]

        return if (leftDrawable == null) {
            false
        } else {
            val startOfDrawable = view.paddingLeft
            val endOfDrawable = startOfDrawable + leftDrawable.bounds.width()
            startOfDrawable <= event.x && event.x <= endOfDrawable
        }
    }

    fun rightDrawableClicked(event: MotionEvent, view: EditText): Boolean {

        val rightDrawable = view.compoundDrawables[DRAWABLE_RIGHT_INDEX]

        return if (rightDrawable == null) {
            false
        } else {
            val startOfDrawable = view.width - rightDrawable.bounds.width() - view.paddingRight
            val endOfDrawable = startOfDrawable + rightDrawable.bounds.width()
            startOfDrawable <= event.x && event.x <= endOfDrawable
        }
    }


}