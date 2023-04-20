package com.example.kode_viewmodel.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.kode_viewmodel.R
import com.example.kode_viewmodel.viewmodel.AppViewModel

class NoFindFragment : Fragment() {

    companion object {
        fun getIstance() = NoFindFragment()
    }

    val viewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.no_find_screen, container, false)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.itemsLiveData.observe(viewLifecycleOwner) {
            if(it.search == "")
                activity?.supportFragmentManager?.popBackStack()
        }
    }
}