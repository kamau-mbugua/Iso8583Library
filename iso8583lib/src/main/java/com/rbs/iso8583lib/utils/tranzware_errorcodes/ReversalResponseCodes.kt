package com.rbs.iso8583lib.utils.tranzware_errorcodes

/**
 * Object containing reversal response codes as per POS_TITP documentation.
 */
object ReversalResponseCodes {

    /** Timeout - The terminal generates this reversal if it does not receive a response to the previous request. */
    const val TIMEOUT_REVERSAL = "01"

    /** Reversal at the request of the customer. */
    const val CUSTOMER_REQUEST_REVERSAL = "08"

    /** Reversal due to terminal technical failure. */
    const val TERMINAL_FAILURE_REVERSAL = "10"

    /** Reversal due to high risk of fraud. */
    const val FRAUD_SUSPECT_REVERSAL = "20"

    /**
     * Returns a description for the given response code.
     * @param code The response code.
     * @return A human-readable description of the reversal reason.
     */
    fun getDescription(code: String): String {
        return when (code) {
            TIMEOUT_REVERSAL -> "Reversal due to transaction timeout."
            CUSTOMER_REQUEST_REVERSAL -> "Reversal requested by customer."
            TERMINAL_FAILURE_REVERSAL -> "Reversal due to terminal failure."
            FRAUD_SUSPECT_REVERSAL -> "Reversal due to suspected fraud."
            else -> "Unknown reversal reason."
        }
    }
}
