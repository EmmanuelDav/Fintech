package com.cyberiyke.fintech.utils

import android.view.View
import androidx.fragment.app.Fragment
import com.cyberiyke.fintech.data.model.Statement
import com.cyberiyke.fintech.data.model.Users


interface UserClickInterface<T> {
    fun onUserClick(user: Users)
}

interface StatementClickInterface<T> {
    fun onStatementClick(statement: Statement)
}