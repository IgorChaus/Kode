package com.example.kode_viewmodel.api

import com.example.kode_viewmodel.model.Person
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface PersonApi {
    @Headers("Prefer: code=200, dynamic=true", "Content-Type: application/jso")
    @GET("users")
    suspend fun getPersons() : Response<Person>
}