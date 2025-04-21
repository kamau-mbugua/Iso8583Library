package com.rbs.iso8583lib.utils.tranzware_errorcodes

/**
 * Tranzware Error Codes Constants (Field 39 - Response Code)
 * These constants represent response codes returned in ISO8583 messages.
 * Each response code has an associated meaning as per TranzWare TITP documentation.
 */
object TranzwareErrorCodesConstants {

    /** ✅ Transaction Approved */
    const val APPROVED = "00"

    /** ⛔ Contact card issuer */
    const val CONTACT_ISSUER = "01"

    /** 🔄 Format error - Invalid message format */
    const val FORMAT_ERROR = "03"

    /** ❌ External decline - Do not honor */
    const val EXTERNAL_DECLINE = "05"

    /** ✅ Partial approval - Transaction partially approved */
    const val PARTIAL_APPROVAL = "10"

    /** 🚫 Invalid transaction - Transaction type not allowed */
    const val INVALID_TRANSACTION = "12"

    /** 🚫 Merchant limit exceeded - Transaction amount exceeds merchant's limit */
    const val MERCHANT_LIMIT_EXCEEDED = "13"

    /** 🚫 Invalid track 2 - Incorrect Track 2 data (Field 35) */
    const val INVALID_TRACK_2 = "14"

    /** 🔑 Weak PIN - User entered a weak PIN */
    const val WEAK_PIN = "25"

    /** ❌ Invalid format - Processing format incorrect */
    const val INVALID_FORMAT = "30"

    /** 🔒 Lost card - Card has been reported lost */
    const val LOST_CARD = "41"

    /** 🚨 Stolen card - Card has been reported stolen */
    const val STOLEN_CARD = "43"

    /** 💰 Insufficient funds - Not enough balance in the account */
    const val INSUFFICIENT_FUNDS = "51"

    /** 🏦 Expired card - Card is past its expiry date */
    const val EXPIRED_CARD = "54"

    /** 🔑 Invalid PIN - Incorrect PIN entered, PIN tries exceeded */
    const val INVALID_PIN = "55"

    /** ❌ Invalid processing code - Processing code not allowed */
    const val INVALID_PROCESSING_CODE = "58"

    /** ❌ Invalid MAC - Message Authentication Code mismatch */
    const val INVALID_MAC = "62"

    /** 🔄 Original request not found - No matching original transaction found */
    const val ORIGINAL_REQUEST_NOT_FOUND = "78"

    /** ❌ Wrong format of customer information field */
    const val WRONG_CUSTOMER_INFO_FORMAT = "81"

    /** ❌ Prepaid Code not found - The provided prepaid card/code does not exist */
    const val PREPAID_CODE_NOT_FOUND = "82"

    /** 🚨 Invalid terminal ID - Terminal ID not recognized */
    const val INVALID_TERMINAL_ID = "89"

    /** 🚫 Destination not available - Acquirer or issuer is offline */
    const val DESTINATION_NOT_AVAILABLE = "91"

    /** 🔄 Duplicate transmission - Transaction was already processed */
    const val DUPLICATE_TRANSMISSION = "94"

    /** ⚠️ Reconcile error - Settlement mismatch, requires Batch Upload */
    const val RECONCILE_ERROR = "95"

    /** 🔥 System error - General system malfunction */
    const val SYSTEM_ERROR = "96"
}
