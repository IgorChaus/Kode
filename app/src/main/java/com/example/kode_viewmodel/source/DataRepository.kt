package com.example.kode_viewmodel.source

import com.example.kode_viewmodel.api.PersonApi
import com.example.kode_viewmodel.model.Person
import com.example.kode_viewmodel.model.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class DataRepository(val service: PersonApi) {
    suspend fun getPersons(): Resource<Person>{
        return withContext(Dispatchers.IO) {
            try {
                val response = service.getPersons()
                if (response.isSuccessful) {
                    Resource.Success(data = response.body()!!)
                } else {
                    Resource.Error(response.code().toString())
                }
            } catch (e: HttpException) {
                Resource.Error(e.message ?: "HttpException")
            } catch (e: IOException) {
                Resource.Error("IOException")
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Exception")
            }
        }
    }
}