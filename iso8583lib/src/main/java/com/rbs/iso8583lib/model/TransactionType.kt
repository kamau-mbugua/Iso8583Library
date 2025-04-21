package com.rbs.iso8583lib.model

import com.rbs.iso8583lib.utils.transactionTypesIdentifiers.MessageTypeIdentifiers
import timber.log.Timber

enum class TransactionType {
    NETWORK_MANAGEMENT,
    AUTHORIZATION_REQUEST,
    BALANCE_INQUIRY,
    FINANCIAL_TRANSACTION,
    REVERSAL,
    PURCHASE
}


enum class TransactionTypes(
    val key: String,
    val displayName: String,
    val messageTypeIdentifiers: String,
    val messageTypeIdentifiersResponse: String
) {
    PURCHASE(
        "PURCHASE",
        "Purchase",
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST,
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE
    ),
    PURCHASE_WITH_CASHBACK(
        "PURCHASE_WITH_CASHBACK",
        "Purchase with Cashback",
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST,
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE,
    ),
    CASH_ADVANCE(
        "CASH_ADVANCE",
        "Cash Advance",
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST,
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE,
    ),
    PREAUTHORIZATION_PURCHASE(
        "PREAUTHORIZATION_PURCHASE",
        "Pre-Auth",
        MessageTypeIdentifiers.AUTHORIZATION_REQUEST,
        MessageTypeIdentifiers.AUTHORIZATION_RESPONSE,
    ),
    PREAUTHORIZATION_COMPLETION(
        "PREAUTHORIZATION_COMPLETION",
        "Pre-Auth Completion",
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST,
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE,
    ),
    BILL_PAYMENT(
        "BILL_PAYMENT",
        "Bill Payment",
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST,
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE,
    ),
    REFUND(
        "REFUND",
        "Refund",
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST,
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE,
    ),
    VOID(
        "VOID",
        "Void",
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST,
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE,
    ),
    BALANCE_INQUIRY(
        "BALANCE_INQUIRY",
        "Balance Inquiry",
        MessageTypeIdentifiers.AUTHORIZATION_REQUEST,
        MessageTypeIdentifiers.AUTHORIZATION_RESPONSE,
    ),
    CARD_VERIFICATION(
        "CARD_VERIFICATION",
        "Card Verification",
        MessageTypeIdentifiers.AUTHORIZATION_REQUEST,
        MessageTypeIdentifiers.AUTHORIZATION_RESPONSE,
    ),
    DEPOSIT(
        "DEPOSIT",
        "Deposit",
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST,
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE,
    ),
    WITHDRAWAL(
        "WITHDRAWAL",
        "Withdrawal",
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST,
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE,
    ),
    TRANSFER(
        "TRANSFER",
        "Transfer",
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST,
        MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE,
    ),
    REVERSAL(
        "REVERSAL",
        "Reversal",
        MessageTypeIdentifiers.REVERSAL_REQUEST,
        MessageTypeIdentifiers.REVERSAL_RESPONSE,
    ),
    NETWORK_MANAGEMENT(
        "NETWORK_MANAGEMENT",
        "Network Management",
        MessageTypeIdentifiers.NETWORK_MANAGEMENT_REQUEST,
        MessageTypeIdentifiers.NETWORK_MANAGEMENT_RESPONSE,
    ),
    MERCHANT_BATCH_UPLOAD(
        "MERCHANT_BATCH_UPLOAD",
        "Merchant Batch Upload",
        MessageTypeIdentifiers.FILE_ACTION_ADVICE,
        MessageTypeIdentifiers.FILE_ACTION_ADVICE_RESPONSE,
    ),
    REPORTS(
        "REPORTS",
        "Reports",
        "",
        "",
    );

//    val processingCode: String by lazy {
//        if (messageTypeIdentifiers == MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST)
//            getTransactionCodeForTransactionTypes(this)
//        else ""
//    }

    companion object {
        fun getTransactionTypeKey(value: String): String? =
            values().find { it.displayName == value }?.key

        fun getTransactionTypeByKey(key: String): TransactionTypes? =
            values().find { it.key == key }

        fun fromDisplayName(displayName: String): TransactionTypes? =
            values().find { it.displayName == displayName }


        fun getDisplayNameFromKey(key: String): String? =
            values().find { it.key == key }?.displayName

        fun fromKey(key: String): TransactionTypes =
            values().find { it.key == key } ?: PURCHASE

        //get displayname from messageTypeIdentifiersResponse
        fun getDisplayNameFromResponseCode(responseCode: String): String? =
            values().find { it.messageTypeIdentifiersResponse == responseCode }?.displayName

        fun getKeyFromResponseCode(responseCode: String): String? =
            values().find { it.messageTypeIdentifiersResponse == responseCode }?.key

        fun getTransactionDisplayName(mti: String, processingCode: String,retrievalReferenceNumber: String): String {
            val transactionType = processingCode.substring(0, 2)

            Timber

            return when (transactionType) {
                "00" -> if (mti == "0110") "Pre-Authorization Purchase"
                else if (mti == "0210" && retrievalReferenceNumber.isNullOrEmpty()) "Purchase"
                else "Purchase"
//                else "Pre-Authorization Completion"
                "01" -> "Cash Advance"
                "02" -> "Refund / Void"
                "09" -> "Purchase with Cashback"
                "31" -> "Balance Inquiry"
                "38" -> "Card Verification"
                "40" -> "Transfer"
                "50" -> "Bill Payment"
                "20" -> "Refund"
                "21" -> "Deposit"
                else -> "Unknown Transaction Type"
            }
        }

    }
}



object AccountTypes {
    const val UNKNOWN = "Unknown"
    const val CHECKING = "Checking"
    const val SAVINGS = "Savings"
    const val CREDIT = "Credit"
    const val BONUS = "Bonus"
}

object AuthorizationField70 {
    const val DEFAULT = "001"
    const val REVERSAL = "002"
    const val ADVICE = "003"
    const val REVERSAL_ADVICE = "004"

}

//TODO: Default values for TerminalInfo , to be replaced with actual values
object TerminalInfo {
    const val TERMINAL_OWNER = "RBS Ltd."
    const val TERMINAL_CITY = "Nairobi"
    const val TERMINAL_STATE = "254"
    const val TERMINAL_COUNTRY_NAME = "Kenya"
    const val TERMINAL_COUNTRY = "404"
    const val TERMINAL_ADDRESS = "JABAVU ROAD"
    const val TERMINAL_BRANCH = "Main Branch"
    const val TERMINAL_REGION = "Nairobi"
    const val TERMINAL_CLASS = "002"
    const val TERMINAL_DATE = "20240115"
    const val TERMINAL_PS_NAME = "RBS"
    const val TERMINAL_FI_NAME = "BANK"
    const val TERMINAL_RETAILER_NAME = "SuperMart tEST Ltd."
    const val TERMINAL_COUNTY = "047"
    const val TERMINAL_ZIP = "00100"
    const val TERMINAL_TIME_OFFSET = "+0300"
}



