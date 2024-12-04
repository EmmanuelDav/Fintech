package com.cyberiyke.fintech.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cyberiyke.fintech.R
import com.cyberiyke.fintech.data.model.Users
import com.cyberiyke.fintech.databinding.FragmentSendMoneyBinding
import com.cyberiyke.fintech.ui.home.HomeViewModel

class SendMoneyFragment : Fragment() {

    lateinit var userDataViewModel: HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v: FragmentSendMoneyBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_send_money, container, false)
        userDataViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        v.exitSend.setOnClickListener { findNavController().popBackStack() }
        v.model = userDataViewModel
        v.executePendingBindings()
        val mBundle: Bundle? = arguments
        val user: Users? = mBundle?.getParcelable("User")
        if (user != null) {
            v.data = user
            val currentUsersList = v.model?.users?.value ?: emptyList() // Get the current list or an empty list
            val updatedUsersList = currentUsersList + user // Create a new list with the added user
            v.model?.users?.value = updatedUsersList
            v.model?.getUsers(user)
        }
        return v.root
    }
}