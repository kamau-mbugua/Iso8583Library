package com.rbs.iso8583lib.storage.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.jpos.iso.ISOMsg
import org.json.JSONObject
import timber.log.Timber

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["transactionId"], unique = true),
        Index(value = ["responseCode"]),
        Index(value = ["processingCode"]),
        Index(value = ["timestamp"])
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val transactionId: String,   // Field 11 - STAN
    val transactionType: String, // e.g. Purchase, Reversal, Balance Inquiry
    val messageTypeName: String, // e.g. Purchase, Reversal, Balance Inquiry
    val amount: String?,         // Field 4 - Transaction Amount
    val responseCode: String?,   // Field 39 - Response Code
    val processingCode: String?,   // Field 39 - Response Code
    val retrievalReferenceNumber: String?,   // Field 39 - Response Code
    val timestamp: Long = System.currentTimeMillis(), // Time of transaction

    // Store ISO8583 message bits dynamically
    val isoMessageJson: String,  // Stores full ISO8583 message as JSON
    val status: String,           // "SUCCESS", "FAILED", "PENDING"
    val lastCardOutputData: String,
    val voided: Boolean = false,
    val voidedOn: Long = 0,
    val isPreAuthCompleted: Boolean = false,
    val authCompletedOn: Long = 0
) {
    // Converts ISOMsg to JSON for storage
    companion object {
        fun fromISOMsg(transactionId: String, transactionType: String, isoMsg: ISOMsg, responseCode: String?, processingCode: String?, retrievalReferenceNumber: String?, lastCardOutputData: String, messageTypeName: String): TransactionEntity {
            // Convert ISOMsg to a map for JSON storage
            val isoJson = mutableMapOf<String, String>()
            for (i in 2..128) {
                isoMsg.getString(i)?.let {
                    var safeValue = it.trim() // Remove leading and trailing spaces

                    // Special handling for P-63
                    if (i == 63) {
                        safeValue = safeValue.replace("\"", "\\\"") // Escape quotes
                        safeValue = safeValue.replace("\\s+".toRegex(), " ") // Normalize spaces
                    }

                    isoJson["P-$i"] = safeValue
                } ?: run {
                    isoMsg.getBytes(i)?.let {
                        isoJson["P-$i"] = it.joinToString("") { byte -> "%02x".format(byte) } // Convert binary fields
                    }
                }
            }

            val jsonString = JSONObject(isoJson as Map<String, Any>).toString()


            Timber.e("isoJson $isoJson")
            return TransactionEntity(
                transactionId = transactionId,
                transactionType = transactionType,
                amount = isoMsg.getString(4),
                responseCode = responseCode,
                processingCode = processingCode,
                retrievalReferenceNumber = retrievalReferenceNumber,
                isoMessageJson = jsonString,
                status = if (responseCode == "00") "SUCCESS" else "FAILED",
                lastCardOutputData=lastCardOutputData,
                messageTypeName = messageTypeName
            )
        }
    }
}
