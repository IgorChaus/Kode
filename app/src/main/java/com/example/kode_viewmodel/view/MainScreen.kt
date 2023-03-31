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
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.kode_viewmodel.R
import com.example.kode_viewmodel.databinding.MainScreenBinding
import com.example.kode_viewmodel.databinding.ScreenItemBinding
import com.example.kode_viewmodel.viewmodel.AppViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout

class MainScreen: Fragment() {

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

        val binding: MainScreenBinding =
            DataBindingUtil.inflate(inflater,R.layout.main_screen, container, false)
        lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
        var checkedButton: Int = R.id.radioButton1

        val mainActivity = activity as AppCompatActivity

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.appBackground,
            typedValue, true)
        val colorBackground = requireContext().getColor(typedValue.resourceId)
        mainActivity.window.setBackgroundDrawable(ColorDrawable(colorBackground))
        requireActivity().window.statusBarColor = colorBackground


        // --------------------- SEARCH ----------------------
        mainActivity.setSupportActionBar(binding.toolbar)

        binding.sortButton.setOnClickListener {
            when(sheetBehavior.state){
                BottomSheetBehavior.STATE_COLLAPSED ->
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                BottomSheetBehavior.STATE_EXPANDED ->
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                else -> return@setOnClickListener
            }
        }

        binding.buttonCancel.setOnClickListener {
            binding.editText.clearFocus()
            binding.buttonCancel.visibility = View.GONE
            binding.editText.setText("")

            // Hide keyboard
            val  imm = binding.editText.context.getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editText.windowToken, 0)

            viewModel.filterSearch("")
            binding.sortButton.visibility = View.VISIBLE

            binding.editText.setCompoundDrawablesWithIntrinsicBounds(
                ResourcesCompat.getDrawable(resources, R.drawable.icon_search, null),
                null, null, null)

        }

        binding.editText.addTextChangedListener {
                s ->  viewModel.filterSearch(s.toString())

        }

        binding.editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.buttonCancel.visibility = View.VISIBLE
                binding.sortButton.visibility = View.GONE
                binding.editText.setCompoundDrawablesWithIntrinsicBounds(
                    ResourcesCompat.getDrawable(resources, R.drawable.icon_search_black, null),
                    null, null, null)
            } else {
                binding.buttonCancel.visibility = View.GONE
                binding.sortButton.visibility = View.VISIBLE
            }
        }


        // ---------------- TABS ---------------------------
  //      val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)
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

        // ----------------- BOTTOM SHEET --------------------------------
        val view = binding.root
        val bottomSheet: ConstraintLayout = view.findViewById(R.id.bottomSheet)
        sheetBehavior = BottomSheetBehavior.from(bottomSheet)

        sheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                val checkButton: RadioButton = view.findViewById(checkedButton)
                checkButton.isChecked = true

                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {

                        mainActivity.window.setBackgroundDrawable(ColorDrawable(Color.parseColor("#10000000")))
                        requireActivity().window.statusBarColor = Color.parseColor("#10000000")

                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        mainActivity.window.setBackgroundDrawable(ColorDrawable(colorBackground))
                        requireActivity().window.statusBarColor = colorBackground
                    }
                    else -> return
                }
            }
        })

        val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)
        radioGroup.clearCheck()
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            checkedButton = checkedId
            viewModel.sorting(checkedButton)
            if (checkedButton == R.id.radioButton2) {
                binding.sortButton.setImageResource(R.drawable.icon_right_purple)
            } else {
                binding.sortButton.setImageResource(R.drawable.icon_right)
            }
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container_list, ListFragment.getIstance())
            ?.addToBackStack(null)
            ?.commit()

        return view
    }


}