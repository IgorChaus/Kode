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
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kode_viewmodel.R
import com.example.kode_viewmodel.model.Person
import com.example.kode_viewmodel.viewmodel.AppViewModel
import com.example.kode_viewmodel.wrappers.Resource
import com.google.android.material.snackbar.Snackbar

class ListFragment: Fragment(), RVAdapter.ItemClickListener {
    companion object {
        fun getIstance() = ListFragment()
    }

    val viewModel: AppViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.list_screen, container, false)

        val rv: RecyclerView = view.findViewById(R.id.rv1)
        val llm = LinearLayoutManager(requireContext())
        rv.layoutManager = llm

        val adapter = RVAdapter(this)
        rv.adapter = adapter

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.appBackground,
            typedValue, true)
        val colorBackground = requireContext().getColor(typedValue.resourceId)

        val swipeContainer: SwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        swipeContainer.setProgressBackgroundColorSchemeColor(colorBackground)
        swipeContainer.setOnRefreshListener {
            viewModel.fetchPersons()
        }

        val lview: View = requireActivity().findViewById(android.R.id.content)

        val snackbarLoading: Snackbar = Snackbar.make(lview,"Секундочку, гружусь...", Snackbar
            .LENGTH_INDEFINITE)
        requireContext().theme.resolveAttribute(R.attr.appColorSeconary,
            typedValue, true)
        var color = requireContext().getColor(typedValue.resourceId)
        snackbarLoading.setBackgroundTint(color)
        snackbarLoading.setTextColor(colorBackground)

        val snackbarError: Snackbar = Snackbar.make(lview,"""Не могу обновить данные.
            |Проверьте соединение с Интернетом.""".trimMargin(), Snackbar.LENGTH_LONG)
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
                        adapter.refreshUsers(it.data)
                        swipeContainer.isRefreshing = false
                        snackbarLoading.dismiss()
                    }
                }

                is Resource.Error -> {
                    if (it.message == "IOException") {
                        snackbarError.show()
                        swipeContainer.isRefreshing = false
                    } else {
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.container_buttons, ErrorScreen.getIstance())
                            ?.commit()
                    }
                }

                is Resource.Loading -> snackbarLoading.show()

                else -> return@observe
            }
        }

        return view
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
            ?.addToBackStack(null)
            ?.commit()

    }
}