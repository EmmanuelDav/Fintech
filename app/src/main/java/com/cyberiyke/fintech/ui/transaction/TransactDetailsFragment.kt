package com.cyberiyke.fintech.ui.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.cyberiyke.fintech.R
import com.cyberiyke.fintech.data.model.Statement
import com.cyberiyke.fintech.databinding.FragmentTransactBinding

class TransactFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: FragmentTransactBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_transact, container, false
        )
        val mBundle: Bundle? = arguments
        val statement: Statement? = mBundle?.getParcelable("statement")
        v.statement = statement
        return v.root
    }
}