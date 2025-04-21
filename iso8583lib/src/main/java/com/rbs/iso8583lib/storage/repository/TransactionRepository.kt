package com.rbs.iso8583lib.storage.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rbs.iso8583lib.storage.dao.TransactionDao
import com.rbs.iso8583lib.storage.entity.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TransactionRepository(private val transactionDao: TransactionDao?) {

    suspend fun saveTransaction(transaction: TransactionEntity) = withContext(Dispatchers.IO) {
        transactionDao?.insertTransaction(transaction)
    }

    suspend fun getTransaction(transactionId: String): TransactionEntity? = withContext(Dispatchers.IO) {
        transactionDao?.getTransactionById(transactionId)
    }

    suspend fun getTransactionByRrn(transactionRrn: String): TransactionEntity? = withContext(Dispatchers.IO) {
        transactionDao?.getPreAuthByRRN(transactionRrn)
    }

    suspend fun clearOldTransactions() = withContext(Dispatchers.IO) {
        transactionDao?.deleteOldTransactions()
    }

    suspend fun getTransactionsForSettlement(): List<TransactionEntity> = withContext(Dispatchers.IO) {
        transactionDao?.getTransactionsForSettlement() ?: emptyList()
    }


    fun markTransactionAsVoided(transactionId: String) {
        transactionDao?.markTransactionAsVoided(transactionId)
    }
    fun getTransactionsPaged(): Flow<PagingData<TransactionEntity>> = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { transactionDao!!.getTransactionsPaged() }
    ).flow
    fun getPreAuthTransactionsPaged(): Flow<PagingData<TransactionEntity>> = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { transactionDao!!.getPreAuthTransactionsPaged() }
    ).flow

    fun searchTransactions(query: String): Flow<PagingData<TransactionEntity>> = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { transactionDao!!.searchTransactions(query) }
    ).flow

    fun filterTransactionsByDate(startDate: Long, endDate: Long): Flow<PagingData<TransactionEntity>> = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { transactionDao!!.filterTransactionsByDate(startDate, endDate) }
    ).flow

    fun filterTransactionsByType(type: String): Flow<PagingData<TransactionEntity>> = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { transactionDao!!.filterTransactionsByType(type) }
    ).flow


}
