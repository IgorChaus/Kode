package com.example.kode

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import java.nio.file.attribute.AclEntry.newBuilder
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    var items = ArrayList<Person.Items>()
    var tabPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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



        val swipeContainer: SwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
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
                                adapter.setMovieList(itemsFilterUpdate, departments[tabPosition].
                                replaceFirstChar {it.uppercase()})
                            } else {
                                adapter.setMovieList(items_update,"Все")
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
                    adapter.setMovieList(itemsFilter, departments[tabPosition].
                        replaceFirstChar {it.uppercase()})
                } else {
                    adapter.setMovieList(items,"Все")
                }

            }

        })

        var apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call: Call<Person> = apiService.getPersons()

        call.enqueue(object : Callback<Person> {
            override fun onResponse(call: Call<Person>, response: Response<Person>) {
                if(response.isSuccessful) {
                    items = response.body()?.items as ArrayList<Person.Items>
                    hideSkeleton(listView)
                    adapter.setMovieList(items, "Все")
                }else{
                    hideSkeleton(listView)
                    Log.d("MyTag","Выводим активити с ошибкой")
                }
            }

            override fun onFailure(call: Call<Person>, t: Throwable) {
                Log.i("MyTag", "Ошибка " + t.toString())

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


}