package com.example.kode_viewmodel

data class Person(val items: List<Items>) {

    data class Items(
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





