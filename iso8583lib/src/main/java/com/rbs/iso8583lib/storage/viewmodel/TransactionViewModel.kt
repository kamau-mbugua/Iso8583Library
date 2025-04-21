package com.rbs.iso8583lib.storage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rbs.iso8583lib.storage.entity.TransactionEntity
import com.rbs.iso8583lib.storage.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    fun getTransactionsPaged(): Flow<PagingData<TransactionEntity>> =
        repository.getTransactionsPaged().cachedIn(viewModelScope)
    fun getPreAuthTransactionsPaged(): Flow<PagingData<TransactionEntity>> =
        repository.getPreAuthTransactionsPaged().cachedIn(viewModelScope)

    fun searchTransactions(query: String): Flow<PagingData<TransactionEntity>> =
        repository.searchTransactions(query).cachedIn(viewModelScope)

    fun filterTransactionsByDate(startDate: Long, endDate: Long): Flow<PagingData<TransactionEntity>> =
        repository.filterTransactionsByDate(startDate, endDate).cachedIn(viewModelScope)

    fun filterTransactionsByType(type: String): Flow<PagingData<TransactionEntity>> =
        repository.filterTransactionsByType(type).cachedIn(viewModelScope)
}
