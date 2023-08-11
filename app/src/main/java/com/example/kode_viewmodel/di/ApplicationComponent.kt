package com.example.kode_viewmodel.di

import com.example.kode_viewmodel.view.MainScreen
import dagger.Component

@Component(modules = [DataModule::class])
interface ApplicationComponent {

    fun inject(fragment: MainScreen)

}