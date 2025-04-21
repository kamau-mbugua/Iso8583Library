package com.rbs.iso8583lib.utils.tranzware_errorcodes

import kotlin.collections.get

object TranzwareErrorCodes {
    val errorCodeMap = mapOf(
        "00" to "Approved",
        "01" to "Contact card issuer",
        "03" to "Format error",
        "05" to "External decline",
        "10" to "Partial approval",
        "12" to "Invalid transaction",
        "13" to "Merchant limit exceeded",
        "14" to "Invalid track 2",
        "25" to "Weak PIN",
        "30" to "Invalid format",
        "41" to "Lost card",
        "43" to "Stolen card",
        "51" to "Insufficient funds",
        "54" to "Expired card",
        "55" to "Invalid PIN, PIN tries exceeded",
        "58" to "Invalid processing code",
        "62" to "Invalid MAC",
        "78" to "Original request not found",
        "81" to "Wrong format of customer information field",
        "82" to "Prepaid Code not found",
        "89" to "Invalid terminal ID",
        "91" to "Destination not available",
        "94" to "Duplicate transmission",
        "95" to "Reconcile error, should start Batch Upload",
        "96" to "System error"
    )

    /**
     * Returns the error message corresponding to the response code.
     * @param responseCode The ISO8583 response code.
     * @return The corresponding error message or "Unknown Error" if not found.
     */
    fun getErrorMessage(responseCode: String?): String {
        return errorCodeMap[responseCode] ?: "Unknown Error"
    }
}