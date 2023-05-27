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
import androidx.lifecycle.ViewModelProvider
import com.example.kode_viewmodel.R
import com.example.kode_viewmodel.databinding.MainScreenBinding
import com.example.kode_viewmodel.source.DataRepository
import com.example.kode_viewmodel.source.RetrofitInstance
import com.example.kode_viewmodel.viewmodel.AppViewModel
import com.example.kode_viewmodel.viewmodel.AppViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout

@RequiresApi(Build.VERSION_CODES.O)
class MainScreen: Fragment() {

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheet: ConstraintLayout
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButtonAlphabet: RadioButton
    private lateinit var radioButtonBirthday: RadioButton

    private var _binding: MainScreenBinding? = null
    private val binding: MainScreenBinding
        get() = _binding ?: throw RuntimeException("MainScreenBinding == null")

    private val dataRepository = DataRepository(RetrofitInstance.service)
    private val factory = AppViewModelFactory(dataRepository)
    private val viewModel: AppViewModel by lazy {
        ViewModelProvider(requireActivity(), factory)[AppViewModel::class.java]
    }


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
        bindBottomSheet()
        initRadioButtons()
        setStatusBarColor()
        setToolBar()
        setSortButtonListener()
        setObserverForSortButton()
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

    private fun setStatusBarColor(){
        val mainActivity = activity as AppCompatActivity
        mainActivity.window.setBackgroundDrawable(ColorDrawable(getColorBackground()))
        requireActivity().window.statusBarColor = getColorBackground()
    }

    private fun setToolBar() {
        val mainActivity = activity as AppCompatActivity
        mainActivity.setSupportActionBar(binding.toolbar)
    }

    private fun setSortButtonListener() {
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

    private fun setObserverForSortButton() {
        viewModel.sortingType.observe(viewLifecycleOwner) {
            when (it) {
                AppViewModel.ALPHABET_SORTING -> {
                    binding.sortButton.setImageResource(R.drawable.icon_right)
                }
                AppViewModel.BIRTHDAY_SORTING ->
                    binding.sortButton.setImageResource(R.drawable.icon_right_purple)
                else -> throw RuntimeException("Illegal sortingType")
            }
        }
    }

    private fun setButtonCancelListener(){
        binding.buttonCancel.setOnClickListener {
            binding.etSearch.clearFocus()
            binding.buttonCancel.visibility = View.GONE
            binding.etSearch.setText("")

            // Hide keyboard
            val  imm = binding.etSearch.context.getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)

            viewModel.setFilterSearch("")
            binding.sortButton.visibility = View.VISIBLE

            binding.etSearch.setCompoundDrawablesWithIntrinsicBounds(
                ResourcesCompat.getDrawable(resources, R.drawable.icon_search, null),
                null, null, null)
        }
    }

    private fun setEditTextListener(){
        binding.etSearch.addTextChangedListener {
                s ->  viewModel.setFilterSearch(s.toString())

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

    private fun setTabListener(){
        departments.forEach{
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it.key))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.setFilterTab(tab?.text.toString())
            }
        })

    }

    private fun setBottomSheetListener(){
        val mainActivity = activity as AppCompatActivity
        sheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {

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

    private fun setRadioGroupListener(){
        radioGroup.setOnCheckedChangeListener { _, checkedId ->

            when(checkedId){
                R.id.alphabetSorting ->
                    viewModel.changeSortingType(AppViewModel.ALPHABET_SORTING)

                R.id.birthdaySorting ->
                    viewModel.changeSortingType(AppViewModel.BIRTHDAY_SORTING)

                else -> throw RuntimeException("Illegal checkedId in Radio Buttons")
            }
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun bindBottomSheet(){
        bottomSheet = binding.root.findViewById(R.id.bottom_sheet)
        radioGroup = binding.root.findViewById(R.id.radioGroup)
        radioButtonAlphabet = binding.root.findViewById(R.id.alphabetSorting)
        radioButtonBirthday = binding.root.findViewById(R.id.birthdaySorting)
        sheetBehavior = BottomSheetBehavior.from(bottomSheet)
    }

    private fun initRadioButtons(){
        when(viewModel.sortingType.value){
            AppViewModel.BIRTHDAY_SORTING -> radioButtonBirthday.isChecked = true
            AppViewModel.ALPHABET_SORTING ->  radioButtonAlphabet.isChecked = true
            else -> throw RuntimeException("Illegal viewModel.sortingType")
        }
    }



    private fun launchListFragment(){
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container_list, ListFragment.getInstance())
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