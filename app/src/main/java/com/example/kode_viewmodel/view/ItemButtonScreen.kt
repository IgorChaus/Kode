package com.example.kode_viewmodel.view

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.kode_viewmodel.R
import com.example.kode_viewmodel.view.MainScreen.Companion.departments
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ItemButtonScreen : ItemScreen() {

    companion object {
        fun getIstance() = ItemButtonScreen()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.screen_item_button, container, false)

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.appColorPrimaryVariant4,
            typedValue, true)
        val color = requireContext().getColor(typedValue.resourceId)
        requireActivity().window.setStatusBarColor(color)

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
            activity?.supportFragmentManager?.popBackStack("ListFragment",
                FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        val phoneButton: Button = view.findViewById(R.id.phoneButton)
        phoneButton.text = this.arguments?.getString("phone")

        val cancelButton: Button = view.findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack("ItemScreen",
                FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        return view
    }


}