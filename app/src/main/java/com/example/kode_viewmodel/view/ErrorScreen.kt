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
import com.example.kode_viewmodel.databinding.ErrorScreenBinding
import com.example.kode_viewmodel.databinding.ScreenItemBinding

class ErrorScreen : Fragment() {
    private var binding: ErrorScreenBinding? = null

    companion object {
        fun getIstance() = ErrorScreen()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

  //      val view = inflater.inflate(R.layout.error_screen, container, false)
        binding = ErrorScreenBinding.inflate(inflater, container, false)

 //       val textView: TextView = view.findViewById(R.id.textView3)
        binding?.textView3?.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container_activity, MainScreen.getInstance())
                ?.commit()
        }

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}