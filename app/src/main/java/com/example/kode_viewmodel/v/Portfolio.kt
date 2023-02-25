package com.example.kode_viewmodel.v

import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kode_viewmodel.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class Portfolio : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.portfolio)
        val photo: ImageView = findViewById(R.id.photo)
        val fullName: TextView = findViewById(R.id.fullName)
        val tag: TextView = findViewById(R.id.tag)
        val department: TextView = findViewById(R.id.department)
        val birthday: TextView = findViewById(R.id.birthday)
        val telephone: TextView = findViewById(R.id.telephone)
        val age: TextView = findViewById(R.id.age)


        val path = intent.extras!!.getString("path")
        Glide.with(this).load(path).circleCrop().into(photo)
        fullName.text = intent.extras!!.getString("personName")
        tag.text = intent.extras!!.getString("tag")?.lowercase()
        val depatStr = intent.extras!!.getString("department")
        department.text = MainActivity.departments.filterValues { it == depatStr }.keys.first()
        val birthdayStr = intent.extras!!.getString("birthday")
        birthday.text = LocalDate.parse(birthdayStr).format(DateTimeFormatter
            .ofPattern("dd MMMM yyyy", Locale("ru")))

        telephone.text = intent.extras!!.getString("phone")

        val formatYear: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy")
        val currentYear = LocalDate.now().format(formatYear).toInt()
        val birthdayYear = LocalDate.parse(birthdayStr).format(formatYear).toInt()
        age.text = ageString(currentYear - birthdayYear)

        val backButton: ImageButton = findViewById(R.id.bButton)
        backButton.setOnClickListener {
            finish()
        }
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