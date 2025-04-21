package com.rbs.iso8583lib.utils.transactionTypesIdentifiers

/**
 * Message Type Identifiers (MTI - Field 0)
 * MTI is a 4-digit code that defines the category and flow of an ISO8583 transaction.
 *
 * Structure of MTI:
 *  - First Digit: Version (0 = ISO8583:1987, 1 = ISO8583:1993, etc.)
 *  - Second Digit: Message Class (Authorization, Financial, Reversal, etc.)
 *  - Third Digit: Message Function (Request, Response, Advice, etc.)
 *  - Fourth Digit: Message Origin (Acquirer, Issuer, etc.)
 */
object MessageTypeIdentifiers {

    // -------------------- Network Management Messages --------------------

    /** 🔧 0800 - Network Management Request (Logon, Echo Test) */
    const val NETWORK_MANAGEMENT_REQUEST = "0800"

    /** 🔧 0810 - Network Management Response */
    const val NETWORK_MANAGEMENT_RESPONSE = "0810"

    /** 🔧 0820 - Network Management Advice (Heartbeat, Key Exchange) */
    const val NETWORK_MANAGEMENT_ADVICE = "0820"

    /** 🔧 0830 - Network Management Advice Response */
    const val NETWORK_MANAGEMENT_ADVICE_RESPONSE = "0830"


    // -------------------- Authorization Messages --------------------

    /** 🔄 0100 - Authorization Request (Card Validation, Pre-Authorization) */
    const val AUTHORIZATION_REQUEST = "0100"

    /** 🔄 0110 - Authorization Response */
    const val AUTHORIZATION_RESPONSE = "0110"

    /** 🔄 0120 - Authorization Advice (Offline Transactions, PIN Verification) */
    const val AUTHORIZATION_ADVICE = "0120"

    /** 🔄 0130 - Authorization Advice Response */
    const val AUTHORIZATION_ADVICE_RESPONSE = "0130"


    // -------------------- Financial Transactions --------------------

    /** 💰 0200 - Financial Transaction Request (Purchase, Cash Withdrawal) */
    const val FINANCIAL_TRANSACTION_REQUEST = "0200"

    /** 💰 0210 - Financial Transaction Response */
    const val FINANCIAL_TRANSACTION_RESPONSE = "0210"

    /** 💰 0220 - Financial Transaction Advice (Installment Payment, Refund) */
    const val FINANCIAL_TRANSACTION_ADVICE = "0220"

    /** 💰 0230 - Financial Transaction Advice Response */
    const val FINANCIAL_TRANSACTION_ADVICE_RESPONSE = "0230"


    // -------------------- File Actions (Batch Settlement, Reconciliation) --------------------

    /** 📁 0300 - File Action Request (Batch Upload, Settlement) */
    const val FILE_ACTION_REQUEST = "0300"

    /** 📁 0310 - File Action Response */
    const val FILE_ACTION_RESPONSE = "0310"

    /** 📁 0320 - File Action Advice */
    const val FILE_ACTION_ADVICE = "0320"

    /** 📁 0330 - File Action Advice Response */
    const val FILE_ACTION_ADVICE_RESPONSE = "0330"


    // -------------------- Reversals and Chargebacks --------------------

    /** 🔁 0400 - Reversal Request (Transaction Rollback) */
    const val REVERSAL_REQUEST = "0400"

    /** 🔁 0410 - Reversal Response */
    const val REVERSAL_RESPONSE = "0410"

    /** 🔁 0420 - Reversal Advice */
    const val REVERSAL_ADVICE = "0420"

    /** 🔁 0430 - Reversal Advice Response */
    const val REVERSAL_ADVICE_RESPONSE = "0430"


    // -------------------- Chargeback / Adjustment Messages --------------------

    /** 🔄 0500 - Chargeback Request (Dispute, Refund) */
    const val CHARGEBACK_REQUEST = "0500"

    /** 🔄 0510 - Chargeback Response */
    const val CHARGEBACK_RESPONSE = "0510"

    /** 🔄 0520 - Chargeback Advice */
    const val CHARGEBACK_ADVICE = "0520"

    /** 🔄 0530 - Chargeback Advice Response */
    const val CHARGEBACK_ADVICE_RESPONSE = "0530"


    // -------------------- Administrative Messages --------------------

    /** ⚙️ 0600 - Administrative Request (Fee Processing, Statement Request) */
    const val ADMINISTRATIVE_REQUEST = "0600"

    /** ⚙️ 0610 - Administrative Response */
    const val ADMINISTRATIVE_RESPONSE = "0610"

    /** ⚙️ 0620 - Administrative Advice */
    const val ADMINISTRATIVE_ADVICE = "0620"

    /** ⚙️ 0630 - Administrative Advice Response */
    const val ADMINISTRATIVE_ADVICE_RESPONSE = "0630"


    // -------------------- Fee Collection and Reconciliation --------------------

    /** 💲 0700 - Fee Collection Request (Service Charge, Convenience Fee) */
    const val FEE_COLLECTION_REQUEST = "0700"

    /** 💲 0710 - Fee Collection Response */
    const val FEE_COLLECTION_RESPONSE = "0710"

    /** 💲 0720 - Fee Collection Advice */
    const val FEE_COLLECTION_ADVICE = "0720"

    /** 💲 0730 - Fee Collection Advice Response */
    const val FEE_COLLECTION_ADVICE_RESPONSE = "0730"
}
