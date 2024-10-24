package com.cyberiyke.fintech.ui.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cyberiyke.fintech.R
import com.cyberiyke.fintech.databinding.FragmentAddMoneyBinding
import com.cyberiyke.fintech.ui.home.HomeViewModel

class AddMoneyFragment : Fragment() {

    var userDataViewModel: HomeViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: FragmentAddMoneyBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_money, container, false)
        userDataViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        v.exitAddM.setOnClickListener { findNavController().popBackStack() }
        v.model = userDataViewModel
        v.executePendingBindings()
        return v.root
    }
}