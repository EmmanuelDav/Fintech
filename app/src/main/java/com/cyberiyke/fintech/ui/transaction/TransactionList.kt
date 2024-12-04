package com.cyberiyke.fintech.ui.transaction

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.cyberiyke.fintech.R
import com.cyberiyke.fintech.databinding.TransactionListBinding
import com.cyberiyke.fintech.ui.adapter.UniversalRecyclerAdapter
import com.cyberiyke.fintech.ui.home.HomeViewModel

class TransactionList : Fragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    var userDataViewModel: HomeViewModel? = null
    lateinit var v: TransactionListBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        v =  DataBindingUtil.inflate(inflater, R.layout.transaction_list, container, false)

        v.lifecycleOwner = this
        userDataViewModel = activity?.let { ViewModelProvider(it).get(HomeViewModel::class.java) }
        v.viewmodel = userDataViewModel
        v.executePendingBindings()
        v.viewmodel?.fetchUserDetails()
        v.viewmodel?.fetchStatement(v.root)
      return v.root

    }

}


@BindingAdapter(value = ["tools:statement", "tools:layout", "tools:onclick"], requireAll = false)
fun <T> setListAdapter(
    recyclerView: RecyclerView,
    statement: MutableLiveData<List<T>>,
    @LayoutRes layout: Int = R.layout.transaction_item,
    listener: Any
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter =
            UniversalRecyclerAdapter(layout, statement.value ?: ArrayList(), listener)
    } else {
        if (recyclerView.adapter is UniversalRecyclerAdapter<*>) {
            val items = statement.value ?: ArrayList()
            (recyclerView.adapter as UniversalRecyclerAdapter<T>)?.updateData(items)
        }
    }
}