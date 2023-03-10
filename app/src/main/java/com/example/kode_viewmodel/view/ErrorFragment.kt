package com.example.kode_viewmodel.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kode_viewmodel.R

class ErrorFragment : Fragment() {

    companion object {
        fun getIstance() = ErrorFragment()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.screen_error, container, false)

        val textView: TextView = view.findViewById(R.id.textView3)
        textView.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, MainFragment.getIstance())
                ?.commit()
        }
        return view
    }
}