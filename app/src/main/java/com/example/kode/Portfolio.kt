package com.example.kode

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Portfolio: AppCompatActivity() {
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
        tag.text = intent.extras!!.getString("tag")
        val depatStr = intent.extras!!.getString("department")
        department.text = MainActivity.departments.filterValues { it == depatStr }.keys.first()
        val birthdayStr = intent.extras!!.getString("birthday")
        birthday.text = birthdayStr
        telephone.text = intent.extras!!.getString("phone")

        val formatYear: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY")
        val currentYear = LocalDate.now().format(formatYear).toInt()
        val birthdayYear = LocalDate.parse(birthdayStr).format(formatYear).toInt()
        age.text = ageString(currentYear - birthdayYear)

        val backButton: ImageButton = findViewById(R.id.bButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    fun ageString(ageInt: Int): String{
        when (ageInt){
            1               ->  return ageInt.toString() + " год"
            in 2 .. 4 ->  return ageInt.toString() + " года"
            in 5 .. 20 -> return ageInt.toString() + " лет"
        }

        when (ageInt % 10){
            1                 ->    return ageInt.toString() + " год"
            in 2 .. 4   ->    return ageInt.toString() + " года"
            0, in 5 .. 9   ->    return ageInt.toString() + " лет"
        }
        return ""
    }
}