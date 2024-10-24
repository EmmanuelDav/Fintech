package com.cyberiyke.fintech.utils

import com.cyberiyke.fintech.data.model.Statement

interface StatementClickInterface<T> {
    fun onStatementClick(statement: Statement)
}