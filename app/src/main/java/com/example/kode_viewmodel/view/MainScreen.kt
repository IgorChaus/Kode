package com.example.kode_viewmodel.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kode_viewmodel.R
import com.example.kode_viewmodel.model.Person
import com.example.kode_viewmodel.viewmodel.AppViewModel
import com.example.kode_viewmodel.wrappers.Resource
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout

class MainScreen: Fragment(), RVAdapter.ItemClickListener {

    companion object {
        fun getIstance() = MainScreen()

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

    val viewModel: AppViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstFetchPersons()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.main_screen, container, false)
        lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
        var checkedBotton: Int = R.id.radioButton1

        val rv: RecyclerView = view.findViewById(R.id.rv1)

        val llm = LinearLayoutManager(requireContext())
        rv.layoutManager = llm

        val adapter = RVAdapter(this)
        rv.adapter = adapter

        val mainActivity = activity as AppCompatActivity

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.appBackground,
            typedValue, true)
        val colorBackground = requireContext().getColor(typedValue.resourceId)
        mainActivity.window.setBackgroundDrawable(ColorDrawable(colorBackground))
        requireActivity().window.setStatusBarColor(colorBackground)


        // --------------------- SEARCH ----------------------
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        mainActivity.setSupportActionBar(toolbar)

        val editText: EditText = view.findViewById(R.id.editText)

        val sortButton: ImageButton = view.findViewById(R.id.imageButton)
        sortButton.setOnClickListener {
            when(sheetBehavior.state){
                BottomSheetBehavior.STATE_COLLAPSED ->
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                BottomSheetBehavior.STATE_EXPANDED ->
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                else -> return@setOnClickListener
            }
        }

        val buttonCancel: Button = view.findViewById(R.id.button)
        buttonCancel.setOnClickListener {
            editText.clearFocus()
            buttonCancel.visibility = View.GONE
            editText.setText("")

            // Hide keyboard
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


        // ---------------- TABS ---------------------------
        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)
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

        // ----------------- BOTTOM SHEET --------------------------------
        val bottomSheet: ConstraintLayout = view.findViewById(R.id.bottomSheet)
        sheetBehavior = BottomSheetBehavior.from(bottomSheet)

        sheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                val checkBotton: RadioButton = view.findViewById(checkedBotton)
                checkBotton.isChecked = true

                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        requireContext().theme.resolveAttribute(R.attr.appColorPrimaryVariant5,
                            typedValue, true)
                        val color = requireContext().getColor(typedValue.resourceId)
                        mainActivity.window.setBackgroundDrawable(ColorDrawable(color))
                        requireActivity().window.setStatusBarColor(color)
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        mainActivity.window.setBackgroundDrawable(ColorDrawable(colorBackground))
                        requireActivity().window.setStatusBarColor(colorBackground)
                    }
                    else -> return
                }
            }
        })

        val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)
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



        val swipeContainer: SwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        swipeContainer.setProgressBackgroundColorSchemeColor(colorBackground)

        swipeContainer.setOnRefreshListener {
            viewModel.fetchPersons()
        }

        val snackbarLoading: Snackbar = Snackbar.make(rv,"Секундочку, гружусь...", Snackbar
            .LENGTH_INDEFINITE)
        requireContext().theme.resolveAttribute(R.attr.appColorSeconary,
            typedValue, true)
        var color = requireContext().getColor(typedValue.resourceId)
        snackbarLoading.setBackgroundTint(color)
        snackbarLoading.setTextColor(colorBackground)

        val snackbarError: Snackbar = Snackbar.make(rv,"""Не могу обновить данные.
            |Проверьте соединение с Интернетом.""".trimMargin(), Snackbar.LENGTH_LONG)
        requireContext().theme.resolveAttribute(R.attr.appColorSeconaryVariant,
            typedValue, true)
        color = requireContext().getColor(typedValue.resourceId)
        snackbarError.setBackgroundTint(color)
        snackbarError.setTextColor(colorBackground)

        viewModel.itemsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    adapter.refreshUsers(it.data!!)
                    swipeContainer.isRefreshing = false
                    snackbarLoading.dismiss()
                }

                is Resource.Error -> {
                    if (it.message == "IOException") {
                        snackbarError.show()
                        swipeContainer.isRefreshing = false
                    } else {
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.container, ErrorScreen.getIstance())
                            ?.commit()
                    }
                }

                is Resource.Loading -> snackbarLoading.show()

            }
        }

        return view
    }

    override fun onItemClick(item: Person.Items){

        val bundle = Bundle()
        bundle.putString("path", item.avatarUrl)
        bundle.putString("personName", item.firstName + " " + item.lastName)
        bundle.putString("tag", item.userTag)
        bundle.putString("department", item.department)
        bundle.putString("birthday", item.birthday)
        bundle.putString("phone", item.phone)

        val itemFragment = ItemScreen.getIstance()
        itemFragment.setArguments(bundle)

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container, itemFragment)
            ?.addToBackStack(null)
            ?.commit()

    }
}