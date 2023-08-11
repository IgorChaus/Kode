package com.example.kode_viewmodel

import android.app.Application
import com.example.kode_viewmodel.di.DaggerApplicationComponent

class KodeApp: Application() {
    val component by lazy {
        DaggerApplicationComponent.create()
    }
}