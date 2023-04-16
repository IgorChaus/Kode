package com.example.kode_viewmodel.view

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kode_viewmodel.R
import com.example.kode_viewmodel.databinding.ListScreenBinding
import com.example.kode_viewmodel.model.Person
import com.example.kode_viewmodel.viewmodel.AppViewModel
import com.example.kode_viewmodel.wrappers.Resource
import com.google.android.material.snackbar.Snackbar

class ListFragment: Fragment(), RVAdapter.ItemClickListener {

    private var binding: ListScreenBinding? = null

    companion object {
        fun getIstance() = ListFragment()
    }

    val viewModel: AppViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = ListScreenBinding.inflate(inflater, container, false)

        val llm = LinearLayoutManager(requireContext())
        binding?.rv1?.layoutManager = llm

        val adapter = RVAdapter(this)
        binding?.rv1?.adapter = adapter

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.appBackground,
            typedValue, true)
        val colorBackground = requireContext().getColor(typedValue.resourceId)

        binding?.swipeRefreshLayout?.setProgressBackgroundColorSchemeColor(colorBackground)
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            viewModel.fetchPersons()
        }

        val lview: View = requireActivity().findViewById(android.R.id.content)

        val snackbarLoading: Snackbar = Snackbar.make(lview,getString(R.string.loading), Snackbar
            .LENGTH_INDEFINITE)
        requireContext().theme.resolveAttribute(R.attr.appColorSeconary,
            typedValue, true)
        var color = requireContext().getColor(typedValue.resourceId)
        snackbarLoading.setBackgroundTint(color)
        snackbarLoading.setTextColor(colorBackground)

        val snackbarError: Snackbar = Snackbar.make(lview,getString(R.string.cant_update_data),
            Snackbar.LENGTH_LONG)
        requireContext().theme.resolveAttribute(R.attr.appColorSeconaryVariant,
            typedValue, true)
        color = requireContext().getColor(typedValue.resourceId)
        snackbarError.setBackgroundTint(color)
        snackbarError.setTextColor(colorBackground)

        viewModel.itemsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if(it.data?.isEmpty()!! && (it.search != "")) {
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.container_list, NoFindFragment.getIstance())
                            ?.addToBackStack(null)
                            ?.commit()
                        snackbarLoading.dismiss()
                    }else {
                        adapter.submitList(it.data)
                        binding?.swipeRefreshLayout?.isRefreshing = false
                        snackbarLoading.dismiss()
                    }
                }

                is Resource.Error -> {
                    if (it.message == "IOException") {
                        snackbarError.show()
                        binding?.swipeRefreshLayout?.isRefreshing = false
                    } else {
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.container_buttons, ErrorScreen.getInstance())
                            ?.commit()
                    }
                }

                is Resource.Loading -> snackbarLoading.show()

                else -> return@observe
            }
        }

        return binding?.root
    }

    override fun onItemClick(item: Person.Items){

        val bundle = Bundle()
        bundle.putString("path", item.avatarUrl)
        bundle.putString("personName", item.firstName + " " + item.lastName)
        bundle.putString("tag", item.userTag)
        bundle.putString("department", item.department)
        bundle.putString("birthday", item.birthday)
        bundle.putString("phone", item.phone)

        val itemFragment = ItemScreen.getIstance()
        itemFragment.setArguments(bundle)

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container_activity, itemFragment)
            ?.addToBackStack("ListFragment")
            ?.commit()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}