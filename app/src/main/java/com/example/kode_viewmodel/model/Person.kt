package com.example.kode_viewmodel.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Person(val items: List<Items>) : Parcelable {

    @Parcelize
    open  class Items(
        val id: String,
        val avatarUrl: String,
        val firstName: String,
        val lastName: String,
        val userTag: String,
        val department: String,
        val position: String,
        val birthday: String,
        val phone: String
    ) : Parcelable


}





