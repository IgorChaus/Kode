package com.example.kode_viewmodel.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.kode_viewmodel.R

class ErrorScreen : Fragment() {

    companion object {
        fun getIstance() = ErrorScreen()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.error_screen, container, false)

        val textView: TextView = view.findViewById(R.id.textView3)
        textView.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container_activity, MainScreen.getIstance())
                ?.commit()
        }
        return view
    }
}