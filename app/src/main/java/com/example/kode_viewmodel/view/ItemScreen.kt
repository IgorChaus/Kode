package com.example.kode_viewmodel.view

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.kode_viewmodel.R
import com.example.kode_viewmodel.databinding.ScreenItemBinding
import com.example.kode_viewmodel.view.MainScreen.Companion.departments
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ItemScreen : Fragment() {
    private var binding: ScreenItemBinding? = null

    companion object {
        fun getIstance() = ItemScreen()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = ScreenItemBinding.inflate(inflater, container, false)

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.appColorPrimaryVariant4,
            typedValue, true)
        val color = requireContext().getColor(typedValue.resourceId)
        requireActivity().window.setStatusBarColor(color)

        val path = this.arguments?.getString("path")
        Glide.with(this).load(path).circleCrop().into(binding!!.photo)
        binding?.fullName?.text = this.arguments?.getString("personName")
        binding?.tag?.text = this.arguments?.getString("tag")?.lowercase()
        val depatStr = this.arguments?.getString("department")
        binding?.department?.text = departments.filterValues { it == depatStr }.keys.first()
        val birthdayStr = this.arguments?.getString("birthday")
        binding?.birthday?.text = LocalDate.parse(birthdayStr).format(DateTimeFormatter
            .ofPattern("dd MMMM yyyy", Locale("ru")))

        binding?.telephone?.text = this.arguments?.getString("phone")

        val formatYear: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy")
        val currentYear = LocalDate.now().format(formatYear).toInt()
        val birthdayYear = LocalDate.parse(birthdayStr).format(formatYear).toInt()
        binding?.age?.text = ageString(currentYear - birthdayYear)

        binding?.bButton?.setOnClickListener {
                activity?.supportFragmentManager?.popBackStack()
        }
        return binding?.root
    }

    fun ageString(ageInt: Int): String{
        when (ageInt){
            1             ->  return ageInt.toString() + " ${getString(R.string.god)}"
            in 2..4 ->  return ageInt.toString() + " ${getString(R.string.goda)}"
            in 5..20 -> return ageInt.toString() + " ${getString(R.string.let)}"
        }

        when (ageInt % 10){
            1               ->    return ageInt.toString() + " ${getString(R.string.god)}"
            in 2..4   ->    return ageInt.toString() + " ${getString(R.string.goda)}"
            0, in 5..9 ->    return ageInt.toString() + " ${getString(R.string.let)}"
        }
        return ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}