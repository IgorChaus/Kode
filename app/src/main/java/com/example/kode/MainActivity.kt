package com.example.kode

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
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

private const val DRAWABLE_LEFT_INDEX = 0
private const val DRAWABLE_RIGHT_INDEX = 2

class MainActivity : AppCompatActivity() {

    var items = ArrayList<Person.Items>()
    var tabPosition: Int = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        val departments = arrayListOf<String>("Все", "android", "ios", "design", "management",
            "qa", "back_office", "frontend", "hr", "pr", "backend", "support", "analytics")

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)

        val listView = arrayListOf<View>()
        showSkeleton(listView)

        val rv: RecyclerView = findViewById(R.id.rv1)

        val llm: LinearLayoutManager = LinearLayoutManager(this)
        rv.setLayoutManager(llm)

        val adapter: RVAdapter = RVAdapter(items)
        rv.setAdapter(adapter)

        var apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call: Call<Person> = apiService.getPersons()

        call.enqueue(object : Callback<Person> {
            override fun onResponse(call: Call<Person>, response: Response<Person>) {
                if(response.isSuccessful) {
                    items = response.body()?.items as ArrayList<Person.Items>
                    hideSkeleton(listView)
                    adapter.setMovieList(items)
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
            override fun afterTextChanged(s: Editable?) {
                var strSearch = s.toString()
                var searchFilter: ArrayList<Person.Items>
                if (strSearch.length > 1) {
                 val   searchFilter = items.filter {
                        it.firstName.contains(strSearch, ignoreCase = true) ||
                                it.lastName.contains(strSearch, ignoreCase = true) ||
                                it.userTag.contains(strSearch, ignoreCase = true)
                    } as ArrayList<Person.Items>
                    adapter.setMovieList(searchFilter)

                }else{
                    searchFilter = items.filter {
                        it.firstName.contains(strSearch, ignoreCase = true) ||
                                it.lastName.contains(strSearch, ignoreCase = true)
                    } as ArrayList<Person.Items>
                    adapter.setMovieList(searchFilter)
                }

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
                    override fun onResponse(call: Call<Person>, response: Response<Person>) {
                        if(response.isSuccessful){
                            var items_update = response.body()?.items as ArrayList<Person.Items>
                            if (tabPosition > 0 ){
                                val itemsFilterUpdate= items_update.
                                    filter { it.department == departments[tabPosition]}
                                        as ArrayList<Person.Items>
                                adapter.setMovieList(itemsFilterUpdate)
                            } else {
                                adapter.setMovieList(items_update)
                            }
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

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabPosition = tab?.position ?:0
                if (tabPosition > 0 ){
                    val itemsFilter= items.filter { it.department == departments[tabPosition]} as ArrayList<Person.Items>
                    adapter.setMovieList(itemsFilter)
                } else {
                    adapter.setMovieList(items)
                }

            }

        })

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