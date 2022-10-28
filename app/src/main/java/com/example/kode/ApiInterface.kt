package com.example.kode

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiInterface {
    @Headers("Prefer: code=200, example=success", "Content-Type: application/jso")
    @GET("https://stoplight.io/mocks/kode-education/trainee-test/25143926/users")
    fun getPersons() : Call<Person>
}