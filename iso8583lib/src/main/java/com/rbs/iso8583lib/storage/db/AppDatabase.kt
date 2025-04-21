package com.rbs.iso8583lib.storage.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rbs.iso8583lib.storage.dao.TransactionDao
import com.rbs.iso8583lib.storage.entity.TransactionEntity
import timber.log.Timber

@Database(
    entities = [TransactionEntity::class], // Include all database entities
    version = 1, // Increment this when changing the schema
    exportSchema = false // Disable schema export for simplicity
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            Timber.e("AppDatabase>>>>> getDatabase")
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "transactions_db"
                )
                    .fallbackToDestructiveMigration() // Deletes DB on schema change (use carefully!)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
