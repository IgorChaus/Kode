package com.example.kode_viewmodel.wrappers

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    // Оборачиваем наши данные в класс 'Success' в случае успешного ответа от api
    class Success<T>(data: T) : Resource<T>(data = data)

    // Оборачиваем наши данные в класс 'Error' для UI в случае ошибки в ответе
    class Error<T>(errorMessage: String) : Resource<T>(message = errorMessage)

    // Оборачиваем наши данные в класс 'Loading' перд вызовом api
    class Loading<T> : Resource<T>()
}