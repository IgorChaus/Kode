package com.example.kode_viewmodel.model

data class Person(val items: List<Items>) {

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
    )
}





