package com.example.kode

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    var items = ArrayList<Person.Items>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val departments = arrayListOf<String>("Все", "android", "ios", "design", "management",
            "qa", "back_office", "frontend", "hr", "pr", "backend", "support", "analytics")

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)



        viewPager.adapter = ViewPagerAdapter(departments)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = departments[position].toString()
        }.attach()

        //Skeleton
        var view :View
        val ltInflater: LayoutInflater = getLayoutInflater();
        val linLayout: LinearLayout = findViewById(R.id.linLayout)
        var counter = getSkeletonRowCount(this)

        while (counter >= 1){
            view = ltInflater.inflate(R.layout.skeleton_item, null, false)
            linLayout.addView(view)
            counter--
        }

        val rv: RecyclerView = findViewById(R.id.rv1)

        val llm: LinearLayoutManager = LinearLayoutManager(this)
        rv.setLayoutManager(llm)

        val adapter: RVAdapter = RVAdapter(items)
        rv.setAdapter(adapter)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tabLayout.selectedTabPosition
                var tabPosition: Int = tab?.position ?:0
                if (tabPosition > 0 ){
                    val itemsFilter = items.filter { it.department == departments[tabPosition]}
                    adapter.setMovieList(itemsFilter)
                } else {
                    adapter.setMovieList(items)
                }

            }

        })

        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call: Call<Person> = apiService.getPersons()

        call.enqueue(object : Callback<Person> {
            override fun onResponse(call: Call<Person>, response: Response<Person>) {
                items = response.body()?.items as ArrayList<Person.Items>
                adapter.setMovieList(items)
            }

            override fun onFailure(call: Call<Person>, t: Throwable) {
                Log.d("MyTag","Response = "+ t);
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

}