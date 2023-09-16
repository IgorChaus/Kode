package com.example.kode_viewmodel.source

import com.example.kode_viewmodel.api.PersonApi
import com.example.kode_viewmodel.wrappers.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class DataRepository @Inject constructor(val service: PersonApi) {
//    suspend fun getPersons(): Response {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = service.getPersons()
//                if (response.isSuccessful) {
//                    Response.Success(data = response.body()!!)
//                } else {
//                    Response.Error(response.code().toString())
//                }
//            } catch (e: HttpException) {
//                Response.Error(e.message ?: "HttpException")
//            } catch (e: IOException) {
//                Response.Error("IOException")
//            } catch (e: Exception) {
//                Response.Error(e.message ?: "Exception")
//            }
//        }
//    }

    suspend fun getPersons(): Flow<Response> = flow {
        val result =  withContext(Dispatchers.IO) {
            try {
                val response = service.getPersons()
                if (response.isSuccessful) {
                    Response.Success(data = response.body()!!)
                } else {
                    Response.Error(response.code().toString())
                }
            } catch (e: HttpException) {
                Response.Error(e.message ?: "HttpException")
            } catch (e: IOException) {
                Response.Error("IOException")
            } catch (e: Exception) {
                Response.Error(e.message ?: "Exception")
            }
        }
        emit(result)
    }
}