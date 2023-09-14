package com.example.kode_viewmodel.wrappers

import com.example.kode_viewmodel.model.AdapterItems

sealed class State {

    class Content(val data: List<AdapterItems>): State()

    class Error(val errorMessage: String): State()

    object Loading: State()

    object NothingFound: State()
}