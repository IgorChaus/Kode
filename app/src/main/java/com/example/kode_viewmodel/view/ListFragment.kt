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
import com.example.kode_viewmodel.R
import com.example.kode_viewmodel.databinding.ListScreenBinding
import com.example.kode_viewmodel.model.Person
import com.example.kode_viewmodel.viewadapter.ItemListAdapter
import com.example.kode_viewmodel.viewmodel.AppViewModel
import com.example.kode_viewmodel.wrappers.State
import com.google.android.material.snackbar.Snackbar

@RequiresApi(Build.VERSION_CODES.O)
class ListFragment: Fragment() {

    private var _binding: ListScreenBinding? = null
    private val binding: ListScreenBinding
        get() = _binding ?: throw RuntimeException("ListScreenBinding == null")

    private lateinit var adapter: ItemListAdapter

    private val viewModel: AppViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ItemListAdapter()
        adapter.itemClickListener = {
            showItem(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        _binding = ListScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rv1.adapter = adapter
        setupSwipeRefreshLayout()
        setupObserver(getSnackBarLoading(), getSnackBarError())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObserver(
        snackBarLoading: Snackbar,
        snackBarError: Snackbar
    ) {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is State.Content -> {
                    adapter.submitList(it.data)
                    binding.swipeRefreshLayout.isRefreshing = false
                    snackBarLoading.dismiss()
                }

                is State.Error -> {
                    if (it.errorMessage == "IOException") {
                        snackBarError.show()
                        binding.swipeRefreshLayout.isRefreshing = false
                    } else {
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.container_buttons, ErrorScreen.getInstance())
                            ?.commit()
                    }
                }

                is State.Loading -> snackBarLoading.show()

                is State.NothingFound -> {
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.container_list, NoFindFragment.getInstance())
                        ?.addToBackStack(null)
                        ?.commit()
                    snackBarLoading.dismiss()
                }
            }
        }
    }

    private fun showItem(item: Person.Items){
        val itemFragment = ItemScreen.getInstance(item)

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container_activity, itemFragment)
            ?.addToBackStack("ListFragment")
            ?.commit()

    }

    private fun getSnackBarLoading(): Snackbar {
        val view: View = requireActivity().findViewById(android.R.id.content)
        val snackBarLoading: Snackbar = Snackbar.make(
            view, getString(R.string.loading), Snackbar
                .LENGTH_INDEFINITE
        )
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.appBackground,
            typedValue, true)
        val colorText = requireContext().getColor(typedValue.resourceId)
        snackBarLoading.setTextColor(colorText)

        requireContext().theme.resolveAttribute(R.attr.appColorSeconary,
            typedValue, true)
        val colorBackGround = requireContext().getColor(typedValue.resourceId)
        snackBarLoading.setBackgroundTint(colorBackGround)

        return snackBarLoading
    }

    private fun getSnackBarError(): Snackbar{
        val view: View = requireActivity().findViewById(android.R.id.content)
        val snackBarError: Snackbar = Snackbar.make(view,
            getString(R.string.cant_update_data),
            Snackbar.LENGTH_LONG
        )
        val typedValue = TypedValue()

        requireContext().theme.resolveAttribute(R.attr.appBackground,
            typedValue, true)
        val colorText = requireContext().getColor(typedValue.resourceId)
        snackBarError.setTextColor(colorText)

        requireContext().theme.resolveAttribute(R.attr.appColorSeconaryVariant,
            typedValue, true)
        val colorBackGround = requireContext().getColor(typedValue.resourceId)
        snackBarError.setBackgroundTint(colorBackGround)

        return snackBarError
    }

    private fun setupSwipeRefreshLayout(){
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.appBackground,
            typedValue, true)
        val colorBackground = requireContext().getColor(typedValue.resourceId)

        binding.swipeRefreshLayout.setProgressBackgroundColorSchemeColor(colorBackground)
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchPersons()
        }
    }

    companion object {
        fun getInstance() = ListFragment()
    }
}

//                    if (it.data?.isEmpty()!! && (it.search != EMPTY_STRING)) {
//                        activity?.supportFragmentManager?.beginTransaction()
//                            ?.replace(R.id.container_list, NoFindFragment.getInstance())
//                            ?.addToBackStack(null)
//                            ?.commit()
//                        snackBarLoading.dismiss()
//                    } else {