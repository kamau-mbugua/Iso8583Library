package com.rbs.iso8583lib.expose

import android.content.Context
import com.rbs.iso8583lib.storage.db.AppDatabase
import com.rbs.iso8583lib.storage.repository.TransactionRepository

object Iso8583DataProvider {
    lateinit var repository: TransactionRepository
    fun initRepository(context: Context) {
        repository = TransactionRepository(AppDatabase.getDatabase(context).transactionDao())
    }
}
