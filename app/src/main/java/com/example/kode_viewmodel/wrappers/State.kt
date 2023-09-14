package com.example.kode_viewmodel.wrappers

import com.example.kode_viewmodel.model.IRow

sealed class State {

    class Content(val data: List<IRow>): State()

    class Error(val errorMessage: String): State()

    object Loading: State()

    object NothingFound: State()
}