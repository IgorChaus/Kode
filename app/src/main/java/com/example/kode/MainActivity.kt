package com.example.kode

import android.annotation.SuppressLint
import android.app.PendingIntent.getActivity
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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
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
import java.security.AccessController.getContext

private const val DRAWABLE_LEFT_INDEX = 0
private const val DRAWABLE_RIGHT_INDEX = 2

class MainActivity : AppCompatActivity() {

    var items = ArrayList<Person.Items>()
    var tabName: String = "Все"
    var strSearch: String =""

    var checkedBotton: Int = R.id.radioButton1

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
        val imageButton: ImageButton = findViewById(R.id.imageButton)
        imageButton.setOnClickListener {
            when(sheetBehavior.state){
                BottomSheetBehavior.STATE_COLLAPSED ->
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                BottomSheetBehavior.STATE_EXPANDED ->
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)

        departments.forEach{
            tabLayout.addTab(tabLayout.newTab().setText(it.key))
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

        val button: Button = findViewById(R.id.button)
        button.setOnClickListener {
            editText.clearFocus()
            button.visibility = View.GONE
            strSearch = ""
            editText.setText(strSearch)

            //Hide keyboard
            val  imm = editText.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0)

            setFilter(rv,adapter)
            imageButton.visibility = View.VISIBLE

            editText.setCompoundDrawablesWithIntrinsicBounds(
                ResourcesCompat.getDrawable(getResources(), R.drawable.icon_search, null),
                    null, null, null)

        }

        call.enqueue(object : Callback<Person> {
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
            override fun afterTextChanged(s: Editable?) {
                strSearch = s.toString()
                setFilter(rv,adapter)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        editText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                button.visibility = View.VISIBLE
                imageButton.visibility = View.GONE
                editText.setCompoundDrawablesWithIntrinsicBounds(
                    ResourcesCompat.getDrawable(getResources(), R.drawable.icon_search_black, null),
                    null, null, null)
            } else {
                button.visibility = View.GONE
                imageButton.visibility = View.VISIBLE
            }
        }

        val snackbarLoading: Snackbar = Snackbar.make(rv,"Секундочку, гружусь...",Snackbar
            .LENGTH_INDEFINITE)
        snackbarLoading.setBackgroundTint(Color.parseColor("#6534FF"))
        snackbarLoading.setTextColor(Color.WHITE)

        val snackbarError: Snackbar = Snackbar.make(rv,"Не могу обновить данные.\n" +
                "Проверьте соединение с Интернетом",Snackbar.LENGTH_LONG)
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



        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabName = tab?.text.toString()
                setFilter(rv,adapter)
            }

        })


        val radioGroup: RadioGroup = findViewById(R.id.radioGroup)
        radioGroup.clearCheck()
        radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup, checkedId: Int){
                checkedBotton = checkedId
                setFilter(rv,adapter)
                if (checkedBotton == R.id.radioButton2){
                    imageButton.setImageResource(R.drawable.icon_right_purple)
                }else {
                    imageButton.setImageResource(R.drawable.icon_right)
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
            adapter.setMovieList(itemsFilter, checkedBotton)
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


}