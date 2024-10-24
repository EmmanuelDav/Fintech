package com.cyberiyke.fintech.utils

import com.cyberiyke.fintech.data.model.Statement
import com.cyberiyke.fintech.data.model.Users


interface UserClickInterface<T> {
    fun onUserClick(user: Users)
}

