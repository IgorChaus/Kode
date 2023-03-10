package com.example.kode_viewmodel.view

import android.os.Build
import android.os.Bundle
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
import com.example.kode_viewmodel.view.MainFragment.Companion.departments
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ItemScreen : Fragment() {

    companion object {
        fun getIstance() = ItemScreen()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.screen_item, container, false)

        val photo: ImageView = view.findViewById(R.id.photo)
        val fullName: TextView = view.findViewById(R.id.fullName)
        val tag: TextView = view.findViewById(R.id.tag)
        val department: TextView = view.findViewById(R.id.department)
        val birthday: TextView = view.findViewById(R.id.birthday)
        val telephone: TextView = view.findViewById(R.id.telephone)
        val age: TextView = view.findViewById(R.id.age)

        val path = this.arguments?.getString("path")
        Glide.with(this).load(path).circleCrop().into(photo)
        fullName.text = this.arguments?.getString("personName")
        tag.text = this.arguments?.getString("tag")?.lowercase()
        val depatStr = this.arguments?.getString("department")
        department.text = departments.filterValues { it == depatStr }.keys.first()
        val birthdayStr = this.arguments?.getString("birthday")
        birthday.text = LocalDate.parse(birthdayStr).format(DateTimeFormatter
            .ofPattern("dd MMMM yyyy", Locale("ru")))

        telephone.text = this.arguments?.getString("phone")

        val formatYear: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy")
        val currentYear = LocalDate.now().format(formatYear).toInt()
        val birthdayYear = LocalDate.parse(birthdayStr).format(formatYear).toInt()
        age.text = ageString(currentYear - birthdayYear)

        val backButton: ImageButton = view.findViewById(R.id.bButton)
        backButton.setOnClickListener {
                activity?.supportFragmentManager?.popBackStack()
        }
        return view
    }

    private fun ageString(ageInt: Int): String{
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
}