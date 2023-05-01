package com.example.kode_viewmodel.view

import android.content.Context
import android.graphics.Color
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.kode_viewmodel.R
import com.example.kode_viewmodel.databinding.MainScreenBinding
import com.example.kode_viewmodel.viewmodel.AppViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout

@RequiresApi(Build.VERSION_CODES.O)
class MainScreen: Fragment() {

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheet: ConstraintLayout
    private lateinit var radioGroup: RadioGroup

    private var _binding: MainScreenBinding? = null
    private val binding: MainScreenBinding
        get() = _binding ?: throw RuntimeException("MainScreenBinding == null")

    val viewModel: AppViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstFetchPersons()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        _binding = MainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindBottonSheet()
        setStatusBarColor()
        setToolBar()
        setSortButtonListener()
        setButtonCancelListener()
        setEditTextListener()
        setTabListener()
        setBottomSheetListener()
        setRadioGroupListener()
        launchListFragment()
   }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun getColorBackground(): Int{
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.appBackground,
            typedValue, true)
        return requireContext().getColor(typedValue.resourceId)
    }

    fun setStatusBarColor(){
        val mainActivity = activity as AppCompatActivity
        mainActivity.window.setBackgroundDrawable(ColorDrawable(getColorBackground()))
        requireActivity().window.statusBarColor = getColorBackground()
    }

    fun setToolBar() {
        val mainActivity = activity as AppCompatActivity
        mainActivity.setSupportActionBar(binding.toolbar)
    }

    fun setSortButtonListener() {
        binding.sortButton.setOnClickListener {
            when (sheetBehavior.state) {
                BottomSheetBehavior.STATE_COLLAPSED ->
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                BottomSheetBehavior.STATE_EXPANDED ->
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                else -> return@setOnClickListener
            }
        }
    }

    fun setButtonCancelListener(){
        binding.buttonCancel.setOnClickListener {
            binding.etSearch.clearFocus()
            binding.buttonCancel.visibility = View.GONE
            binding.etSearch.setText("")

            // Hide keyboard
            val  imm = binding.etSearch.context.getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)

            viewModel.filterSearch("")
            binding.sortButton.visibility = View.VISIBLE

            binding.etSearch.setCompoundDrawablesWithIntrinsicBounds(
                ResourcesCompat.getDrawable(resources, R.drawable.icon_search, null),
                null, null, null)
        }
    }

    fun setEditTextListener(){
        binding.etSearch.addTextChangedListener {
                s ->  viewModel.filterSearch(s.toString())

        }

        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.buttonCancel.visibility = View.VISIBLE
                binding.sortButton.visibility = View.GONE
                binding.etSearch.setCompoundDrawablesWithIntrinsicBounds(
                    ResourcesCompat.getDrawable(resources, R.drawable.icon_search_black, null),
                    null, null, null)
            } else {
                binding.buttonCancel.visibility = View.GONE
                binding.sortButton.visibility = View.VISIBLE
            }
        }
    }

    fun setTabListener(){
        departments.forEach{
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it.key))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.filterTab(tab?.text.toString())
            }
        })
    }

    fun setBottomSheetListener(){
        val mainActivity = activity as AppCompatActivity
        sheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val checkButton: RadioButton = binding.root.findViewById(viewModel.sorting)
                checkButton.isChecked = true

                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {

                        mainActivity.window.setBackgroundDrawable(ColorDrawable(Color.parseColor("#10000000")))
                        requireActivity().window.statusBarColor = Color.parseColor("#10000000")

                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        mainActivity.window.setBackgroundDrawable(ColorDrawable(getColorBackground()))
                        requireActivity().window.statusBarColor = getColorBackground()
                    }
                    else -> return
                }
            }
        })

    }

    fun setRadioGroupListener(){
        radioGroup.clearCheck()
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.sorting(checkedId)
            if (checkedId == AppViewModel.BIRTHDAY_SORTING) {
                binding.sortButton.setImageResource(R.drawable.icon_right_purple)
            } else {
                binding.sortButton.setImageResource(R.drawable.icon_right)
            }
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    fun bindBottonSheet(){
        bottomSheet = binding.root.findViewById(R.id.bottom_sheet)
        radioGroup = binding.root.findViewById(R.id.radioGroup)
        sheetBehavior = BottomSheetBehavior.from(bottomSheet)
    }

    fun launchListFragment(){
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container_list, ListFragment.getIstance())
            ?.addToBackStack(null)
            ?.commit()
    }

    companion object {
        fun getInstance() = MainScreen()

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
}