package com.example.kode_viewmodel.view

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kode_viewmodel.R

class ButtonsFragment: Fragment() {
    companion object {
        fun getIstance() = ButtonsFragment()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.buttons, container, false)

        /*val mainActivity = activity as AppCompatActivity
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.appBackground,
            typedValue, true)
        val colorBackground = requireContext().getColor(typedValue.resourceId)
        mainActivity.window.setBackgroundDrawable(ColorDrawable(colorBackground))
        requireActivity().window.setStatusBarColor(colorBackground)*/

        val buttonCancel: Button = view.findViewById(R.id.buttonCencel)
        buttonCancel.setOnClickListener {
            parentFragmentManager?.popBackStack()
        }

        return view
    }
}