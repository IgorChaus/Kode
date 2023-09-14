package com.example.kode_viewmodel.wrappers

import com.example.kode_viewmodel.model.Person

sealed class Response {
    class Success(val data: Person) : Response()

    class Error(val errorMessage: String) : Response()
}