package com.example.kode_viewmodel.di

import com.example.kode_viewmodel.api.PersonApi
import com.example.kode_viewmodel.source.RetrofitInstance
import dagger.Module
import dagger.Provides

@Module
class DataModule {

    @Provides
    fun providePersonApi(): PersonApi {
        return RetrofitInstance.service
    }
}