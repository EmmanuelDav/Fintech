package com.cyberiyke.fintech.utils

import android.view.View
import androidx.fragment.app.Fragment
import com.cyberiyke.fintech.data.model.Statement

interface Interface<T> {
    fun onItemClick(statement: Statement)
}