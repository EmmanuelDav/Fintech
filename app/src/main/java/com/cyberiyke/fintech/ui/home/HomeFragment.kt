package com.cyberiyke.fintech.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.cyberiyke.fintech.R
import com.cyberiyke.fintech.databinding.FragmentHomeBinding
import com.cyberiyke.fintech.ui.adapter.UniversalRecyclerAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = homeViewModel
        binding.executePendingBindings()
        binding.viewmodel?.fetchUserDetails()
        binding.viewmodel?.fetchStatement(binding.root)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@BindingAdapter(value = ["tools:statement", "tools:layout", "tools:onclick"], requireAll = false)
fun <T> setHomeAdapter(
    recyclerView: RecyclerView,
    statement: MutableLiveData<List<T>>,
    @LayoutRes layout: Int = R.layout.item_recent,
    listener: Any
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter =
            UniversalRecyclerAdapter(layout, statement.value ?: ArrayList(), listener)
    } else {
        if (recyclerView.adapter is UniversalRecyclerAdapter<*>) {
            val items = statement.value ?: ArrayList()
            (recyclerView.adapter as UniversalRecyclerAdapter<T>).updateData(items)
        }
    }
}