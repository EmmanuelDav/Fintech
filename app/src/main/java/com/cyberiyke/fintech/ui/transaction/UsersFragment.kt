package com.iyke.onlinebanking.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cyberiyke.fintech.R
import com.cyberiyke.fintech.databinding.FragmentUsersBinding
import com.cyberiyke.fintech.ui.adapter.UniversalRecyclerAdapter
import com.cyberiyke.fintech.ui.home.HomeViewModel

class UsersFragment : Fragment() {

    var userDataViewModel: HomeViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: FragmentUsersBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_users, container, false)
        v.lifecycleOwner = this
        userDataViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        v.exitSend.setOnClickListener { findNavController().popBackStack() }
        v.model = userDataViewModel
        v.executePendingBindings()
        v.model?.fetchUsers(v.root)
        return v.root
    }
}

@BindingAdapter(value = ["tools:data","tools:itemList", "tools:itemListener"], requireAll = false)
fun <T> setUsersAdapter(recyclerView: RecyclerView, data : MutableLiveData<List<T>>, @LayoutRes listItem : Int = R.layout.item_recent_contacts, itemListener: Any){
    if (recyclerView.adapter == null){
        recyclerView.adapter =  UniversalRecyclerAdapter(listItem, data.value ?: ArrayList(), itemListener)
    }else{
        if (recyclerView.adapter is UniversalRecyclerAdapter<*>) {
            val items = data.value ?: ArrayList()
            (recyclerView.adapter as UniversalRecyclerAdapter<T>).updateData(items)
        }
    }
}