package com.example.kode_viewmodel.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kode_viewmodel.R
import com.example.kode_viewmodel.databinding.ErrorScreenBinding

class ErrorScreen : Fragment() {

    private var _binding: ErrorScreenBinding? = null
    private val binding: ErrorScreenBinding
        get() = _binding ?: throw RuntimeException("ErrorScreenBinding == null")

    companion object {
        fun getInstance() = ErrorScreen()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        _binding = ErrorScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTryAgain.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container_activity, MainScreen.getInstance())
                ?.commit()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}