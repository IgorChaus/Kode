package com.example.kode

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
    ) {
        /*enum class Department{
            android, ios, design, management, qa, back_office, frontend, hr, pr, backend, support,
            analytics
        }*/

    }
}

