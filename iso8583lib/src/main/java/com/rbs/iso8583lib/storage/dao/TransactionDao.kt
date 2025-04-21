package com.rbs.iso8583lib.storage.dao

    import androidx.paging.PagingSource
    import androidx.room.Dao
    import androidx.room.Insert
    import androidx.room.OnConflictStrategy
    import androidx.room.Query
    import com.rbs.iso8583lib.storage.entity.TransactionEntity

    @Dao
    interface TransactionDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insertTransaction(transaction: TransactionEntity)

        @Query("SELECT * FROM transactions WHERE transactionId = :transactionId LIMIT 1")
        fun getTransactionById(transactionId: String): TransactionEntity?

        @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT :limit")
        fun getRecentTransactions(limit: Int): List<TransactionEntity>

        //Clear all database
        @Query("DELETE FROM transactions")
        fun deleteOldTransactions()

        @Query("UPDATE transactions SET status = 'VOIDED' WHERE transactionId = :transactionId")
        fun markTransactionAsVoided(transactionId: String)

        // **Paging Source for Transactions**
        @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
        fun getTransactionsPaged(): PagingSource<Int, TransactionEntity>

        // **Paging Source for Transactions**
        @Query("SELECT * FROM transactions WHERE messageTypeName = 'PREAUTHORIZATION_PURCHASE' AND isPreAuthCompleted = 0 ORDER BY timestamp DESC")
        fun getPreAuthTransactionsPaged(): PagingSource<Int, TransactionEntity>

        // **Search Transactions**
        @Query("SELECT * FROM transactions WHERE transactionId LIKE '%' || :query || '%' OR transactionType LIKE '%' || :query || '%' OR amount LIKE '%' || :query || '%' ORDER BY timestamp DESC")
        fun searchTransactions(query: String): PagingSource<Int, TransactionEntity>

        // **Filter by Date Range**
        @Query("SELECT * FROM transactions WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
        fun filterTransactionsByDate(startDate: Long, endDate: Long): PagingSource<Int, TransactionEntity>

        // **Filter by Transaction Type**
        @Query("SELECT * FROM transactions WHERE transactionType = :type ORDER BY timestamp DESC")
        fun filterTransactionsByType(type: String): PagingSource<Int, TransactionEntity>

        @Query("SELECT * FROM transactions WHERE retrievalReferenceNumber = :rrn LIMIT 1")
        fun getPreAuthByRRN(rrn: String): TransactionEntity?

        @Query("""
    SELECT * FROM transactions 
    WHERE 
        status = 'SUCCESS' 
        AND messageTypeName IN (
            'PURCHASE', 'PURCHASE_WITH_CASHBACK', 'CASH_ADVANCE',
            'PREAUTHORIZATION_COMPLETION', 'DEPOSIT', 'TRANSFER', 'REFUND', 'BILL_PAYMENT'
        )
        AND voided = 0
    ORDER BY timestamp ASC
""")
        fun getTransactionsForSettlement(): List<TransactionEntity>


    }

