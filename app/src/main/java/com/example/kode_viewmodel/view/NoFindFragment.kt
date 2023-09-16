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
import com.example.kode_viewmodel.wrappers.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NoFindFragment : Fragment() {

    companion object {
        fun getInstance() = NoFindFragment()
    }

    private val viewModel: AppViewModel by activityViewModels()

    private var job: Job? = null

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
        job = CoroutineScope(Dispatchers.Main).launch {
            viewModel.state.collect(){
                if(it is State.Content && !it.data.isEmpty())
                    activity?.supportFragmentManager?.popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
    }

}