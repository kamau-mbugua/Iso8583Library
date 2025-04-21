package com.rbs.iso8583lib.storage.access

import androidx.paging.PagingData
import com.rbs.iso8583lib.expose.Iso8583DataProvider
import com.rbs.iso8583lib.storage.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

object TransactionAccess {

    private val repo get() = Iso8583DataProvider.repository

    fun getAllTransactions(): Flow<PagingData<TransactionEntity>> =
        repo.getTransactionsPaged()

    fun getPreAuthTransactions(): Flow<PagingData<TransactionEntity>> =
        repo.getPreAuthTransactionsPaged()

    fun search(query: String): Flow<PagingData<TransactionEntity>> =
        repo.searchTransactions(query)

    fun filterByDate(startDate: Long, endDate: Long): Flow<PagingData<TransactionEntity>> =
        repo.filterTransactionsByDate(startDate, endDate)

    fun filterByType(type: String): Flow<PagingData<TransactionEntity>> =
        repo.filterTransactionsByType(type)

    suspend fun getTransactionById(id: String) =
        repo.getTransaction(id)

    suspend fun getTransactionByRrn(rrn: String) =
        repo.getTransactionByRrn(rrn)
}
