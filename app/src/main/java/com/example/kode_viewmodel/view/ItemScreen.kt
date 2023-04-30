package com.example.kode_viewmodel.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.kode_viewmodel.R
import com.example.kode_viewmodel.databinding.ScreenItemBinding
import com.example.kode_viewmodel.model.Person
import com.example.kode_viewmodel.view.MainScreen.Companion.departments
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class ItemScreen : Fragment() {

    private lateinit var item: Person.Items

    private var _binding: ScreenItemBinding? = null
    private val binding: ScreenItemBinding
        get() = _binding ?: throw RuntimeException("ScreenItemBinding == null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pursArgs()
    }

    fun pursArgs(){
        requireArguments().getParcelable<Person.Items>(KEY_ITEM)?.let {
            item = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        _binding = ScreenItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setStatusBarColor(getColorForStatusBar())
        bindViews()
    }

    @SuppressLint("SetTextI18n")
    fun bindViews(){
        Glide.with(this).load(item.avatarUrl).circleCrop().into(binding.photo)
        with(binding) {
            fullName.text = item.firstName + " " + item.lastName
            tag.text = item.userTag.lowercase()
            department.text = departments.filterValues { it == item.department }.keys.first()
            birthday.text = LocalDate.parse(item.birthday).format(DateTimeFormatter
                .ofPattern("dd MMMM yyyy", Locale("ru")))
            telephone.text = item.phone
            age.text = getAge(item.birthday)
            backButton.setOnClickListener {
                activity?.supportFragmentManager?.popBackStack()
            }
        }
    }

    fun getColorForStatusBar(): Int{
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.appColorPrimaryVariant4,
            typedValue, true)
        return requireContext().getColor(typedValue.resourceId)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getAge(birthday: String): String{
        val formatYear: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy")
        val currentYear = LocalDate.now().format(formatYear).toInt()
        val birthdayYear = LocalDate.parse(birthday).format(formatYear).toInt()
        val age: Int = currentYear - birthdayYear

        when (age){
            1             ->  return age.toString() + " ${getString(R.string.god)}"
            in 2..4 ->  return age.toString() + " ${getString(R.string.goda)}"
            in 5..20 -> return age.toString() + " ${getString(R.string.let)}"
        }

        when (age % 10){
            1               ->    return age.toString() + " ${getString(R.string.god)}"
            in 2..4   ->    return age.toString() + " ${getString(R.string.goda)}"
            0, in 5..9 ->    return age.toString() + " ${getString(R.string.let)}"
        }
        return ""
    }

    companion object {
        fun getIstance(item: Person.Items): Fragment{
            return ItemScreen().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_ITEM,item)
                }
            }
        }

        private const val KEY_ITEM = "item"
    }

}