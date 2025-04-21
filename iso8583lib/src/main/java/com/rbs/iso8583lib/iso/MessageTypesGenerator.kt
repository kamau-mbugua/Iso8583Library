package com.rbs.iso8583lib.iso

import com.rbs.iso8583lib.model.AccountTypes.UNKNOWN
import com.rbs.iso8583lib.model.CardOutputData
import com.rbs.iso8583lib.model.FileActionAdvice
import com.rbs.iso8583lib.model.Iso8583Message
import com.rbs.iso8583lib.model.IsoDataClass
import com.rbs.iso8583lib.model.TransactionTypes
import com.rbs.iso8583lib.utils.generateRRN
import com.rbs.iso8583lib.utils.generateSTAN
import com.rbs.iso8583lib.utils.getLocalTransactionDate
import com.rbs.iso8583lib.utils.getLocalTransactionTime
import com.rbs.iso8583lib.utils.getProcessingCode
import com.rbs.iso8583lib.utils.getTerminalID
import com.rbs.iso8583lib.utils.transactionTypesIdentifiers.MessageTypeIdentifiers
import com.rbs.iso8583lib.utils.tranzware_errorcodes.ReversalResponseCodes
import org.jpos.iso.ISOMsg
import com.rbs.iso8583lib.model.TransactionTypes.*
import com.rbs.iso8583lib.storage.entity.TransactionEntity
import com.rbs.iso8583lib.utils.cores.extensions.fromJson
import com.rbs.iso8583lib.utils.cores.extensions.removeLeadingZeros
import com.rbs.iso8583lib.utils.generateBatchNumber
import com.rbs.iso8583lib.utils.prefs.originalBatchNumberUsedFor0320

fun generateSignOnRequest():Iso8583Message {
    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.NETWORK_MANAGEMENT_REQUEST,
        p3_processingCode = getProcessingCode(),
        p11_systemTraceAuditNumber = generateSTAN(),
        p12_localTransactionTime = getLocalTransactionTime(),
        p13_localTransactionDate = getLocalTransactionDate(),
        p41_cardAcceptorTerminalID = getTerminalID(),
    )
}

fun createPurchaseRequests(createPayload: CardOutputData): Iso8583Message {

    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST,
        p2_primaryAccountNumber = createPayload.cardNumber,
        p3_processingCode = getProcessingCode(TransactionTypes.PURCHASE.name, UNKNOWN, UNKNOWN),
        p4_transactionAmount = createPayload.amount,
        p11_systemTraceAuditNumber = generateSTAN(),
        p12_localTransactionTime = getLocalTransactionTime(),
        p13_localTransactionDate = getLocalTransactionDate(),
        p22_pointOfServiceEntryMode = "051",
        p23_cardSequenceNumber = createPayload.cardSequenceNumber,
        p25_pointOfServiceConditionCode = "00",
        p35_track2Data = createPayload.track2Data,
        p41_cardAcceptorTerminalID = getTerminalID(),
        p49_transactionCurrencyCode = createPayload.countryID.removeLeadingZeros(),
        p52_pinData = createPayload.pinBlock,
        p55_iccData = createPayload.field55,
    )
}

fun createBalanceInquiryRequest(createPayload: CardOutputData): Iso8583Message {
    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.AUTHORIZATION_REQUEST,  // MTI for Balance Inquiry Request
        p2_primaryAccountNumber = createPayload.cardNumber, // PAN
        p3_processingCode = getProcessingCode(
            TransactionTypes.BALANCE_INQUIRY.name,
            UNKNOWN,
            UNKNOWN
        ), // Processing Code 31
        p11_systemTraceAuditNumber = generateSTAN(), // Unique trace number
        p12_localTransactionTime = getLocalTransactionTime(), // Transaction time
        p13_localTransactionDate = getLocalTransactionDate(), // Transaction date
        p22_pointOfServiceEntryMode = "051", // EMV chip read
        p25_pointOfServiceConditionCode = "00", // Normal condition
        p37_retrievalReferenceNumber = generateRRN(),
        p41_cardAcceptorTerminalID = getTerminalID(), // Terminal ID
        p49_transactionCurrencyCode = createPayload.countryID.removeLeadingZeros() // Currency Code
    )
}

fun createDepositRequest(createPayload: CardOutputData): Iso8583Message {
    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST,  // MTI = "0200"
        p2_primaryAccountNumber = createPayload.cardNumber,                   // PAN
        p3_processingCode = getProcessingCode(
            TransactionTypes.DEPOSIT.name,
            UNKNOWN,
            UNKNOWN
        ),     // Should return a code starting with "21"
        p4_transactionAmount = createPayload.amount,                           // Deposit amount (in minor currency units)
        p11_systemTraceAuditNumber = generateSTAN(),                           // New unique STAN
        p12_localTransactionTime = getLocalTransactionTime(),                  // Terminal transaction time
        p13_localTransactionDate = getLocalTransactionDate(),                  // Terminal transaction date
        p22_pointOfServiceEntryMode = "051",                                   // e.g. EMV chip read
        p25_pointOfServiceConditionCode = "00",                                // Normal condition
        p35_track2Data = createPayload.track2Data,                             // Track 2 data (if available)
        p41_cardAcceptorTerminalID = getTerminalID(),                          // Terminal ID
        p49_transactionCurrencyCode = createPayload.countryID.removeLeadingZeros(), // Currency code
        p52_pinData = createPayload.pinBlock,                                  // PIN data (if required)
        p55_iccData = createPayload.field55                                    // ICC/EMV data (if available)
    )
}



fun createPurchaseWithCashbackRequest(createPayload: CardOutputData): Iso8583Message {
    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST,
        p2_primaryAccountNumber = createPayload.cardNumber,
        p3_processingCode = getProcessingCode(
            TransactionTypes.PURCHASE_WITH_CASHBACK.name,
            UNKNOWN,
            UNKNOWN
        ),
        p4_transactionAmount = createPayload.amount,  // Total amount (purchase + cashback)
        p11_systemTraceAuditNumber = generateSTAN(),
        p12_localTransactionTime = getLocalTransactionTime(),
        p13_localTransactionDate = getLocalTransactionDate(),
        p22_pointOfServiceEntryMode = "051", // EMV chip read
        p25_pointOfServiceConditionCode = "00", // Normal condition
        p35_track2Data = createPayload.track2Data,
        p41_cardAcceptorTerminalID = getTerminalID(),
        p49_transactionCurrencyCode = createPayload.countryID.removeLeadingZeros(),
        p52_pinData = createPayload.pinBlock,  // Required for cashback transactions
        p55_iccData = createPayload.field55,  // Required for EMV transactions
        p60_privateUse = createPayload.amountCashback // Cashback amount
    )
}

fun createCashAdvanceRequest(createPayload: CardOutputData): Iso8583Message {
    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST,  // MTI = 0200
        p2_primaryAccountNumber = createPayload.cardNumber,  // PAN (Required unless cardless)
        p3_processingCode = getProcessingCode(
            TransactionTypes.CASH_ADVANCE.name,
            UNKNOWN,
            UNKNOWN
        ), // Processing Code 01
        p4_transactionAmount = createPayload.amount,  // Withdrawal Amount
        p11_systemTraceAuditNumber = generateSTAN(),  // Unique STAN
        p12_localTransactionTime = getLocalTransactionTime(),  // Terminal Transaction Time
        p13_localTransactionDate = getLocalTransactionDate(),  // Terminal Transaction Date
        p22_pointOfServiceEntryMode = "051",  // EMV Chip Read
        p25_pointOfServiceConditionCode = "00",  // Normal Condition
        p35_track2Data = createPayload.track2Data,  // Track 2 Data (Mandatory for Card Transactions)
        p41_cardAcceptorTerminalID = getTerminalID(),  // Terminal ID
        p49_transactionCurrencyCode = createPayload.countryID.removeLeadingZeros(),  // Currency Code
        p52_pinData = createPayload.pinBlock,  // PIN Block (Required for Cash Advance)
        p55_iccData = createPayload.field55  // ICC Data for EMV
    )
}


fun createBillPaymentRequest(createPayload: CardOutputData): Iso8583Message {
    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST, // 0200
        p2_primaryAccountNumber = createPayload.cardNumber, // PAN
        p3_processingCode = getProcessingCode(
            TransactionTypes.BILL_PAYMENT.name,
            UNKNOWN,
            UNKNOWN
        ), // Processing Code 50
        p4_transactionAmount = createPayload.amount, // Transaction Amount
        p11_systemTraceAuditNumber = generateSTAN(), // STAN
        p12_localTransactionTime = getLocalTransactionTime(), // Terminal Transaction Time
        p13_localTransactionDate = getLocalTransactionDate(), // Terminal Transaction Date
        p22_pointOfServiceEntryMode = "051", // EMV Chip Read
        p25_pointOfServiceConditionCode = "00", // Normal Condition
        p41_cardAcceptorTerminalID = getTerminalID(), // Terminal ID
        p49_transactionCurrencyCode = createPayload.countryID.removeLeadingZeros(), // Currency Code
        p57_additionalData = null /*createPayload.billerInfo*/ // Additional Bill Payment Details
    )
}


fun createRefundRequest(createPayload: CardOutputData): Iso8583Message {
    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST, // 0200 MTI
        p2_primaryAccountNumber = createPayload.cardNumber, // PAN
        p3_processingCode = getProcessingCode(TransactionTypes.REFUND.name, UNKNOWN, UNKNOWN), // Processing Code 02
        p4_transactionAmount = createPayload.amount, // Refund Amount
        p11_systemTraceAuditNumber = generateSTAN(), // Unique Transaction Reference
        p12_localTransactionTime = getLocalTransactionTime(), // Terminal Transaction Time
        p13_localTransactionDate = getLocalTransactionDate(), // Terminal Transaction Date
        p22_pointOfServiceEntryMode = "051", // EMV Read
        p25_pointOfServiceConditionCode = "00", // Normal condition
        p35_track2Data = createPayload.track2Data, // Track 2 Data (if available)
        p41_cardAcceptorTerminalID = getTerminalID(), // Terminal ID
        p49_transactionCurrencyCode = createPayload.countryID.removeLeadingZeros(), // Currency Code
        p52_pinData = createPayload.pinBlock, // PIN Data (if applicable)
        p55_iccData = createPayload.field55 // EMV ICC Data (if available)
    )
}


fun createVoidCardPresentRequest(createPayload: CardOutputData): Iso8583Message {
    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST, // 0200
        p2_primaryAccountNumber = createPayload.cardNumber, // PAN
        p3_processingCode = getProcessingCode(
            TransactionTypes.VOID.name,
            UNKNOWN,
            UNKNOWN
        ), // "02" if P4=0
        p4_transactionAmount = "0", // Original transaction amount
        p11_systemTraceAuditNumber = generateSTAN(), // Unique trace number
        p12_localTransactionTime = getLocalTransactionTime(), // Terminal transaction time
        p13_localTransactionDate = getLocalTransactionDate(), // Terminal transaction date
        p22_pointOfServiceEntryMode = "051", // EMV chip read
        p25_pointOfServiceConditionCode = "00", // Normal condition
        p35_track2Data = createPayload.track2Data, // Track 2 Data
        p37_retrievalReferenceNumber = createPayload.originalRRN,
        p41_cardAcceptorTerminalID = getTerminalID(), // Terminal ID
        p49_transactionCurrencyCode = createPayload.countryID.removeLeadingZeros(), // Currency Code
        p55_iccData = createPayload.field55, // EMV Data
        p60_privateUse = createPayload.amount // Original transaction amount (mandatory for voids)
    )
}

fun createVoidCardAbsentRequest(createPayload: CardOutputData): Iso8583Message {
    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST, // 0200
//        p2_primaryAccountNumber = createPayload.cardNumber, // PAN
        p3_processingCode = getProcessingCode(
            TransactionTypes.VOID.name,
            UNKNOWN,
            UNKNOWN
        ), // "02" if P4=0
        p4_transactionAmount = "0", // Original transaction amount
        p11_systemTraceAuditNumber = createPayload.transactionId, // Unique trace number
        p12_localTransactionTime = getLocalTransactionTime(), // Terminal transaction time
        p13_localTransactionDate = getLocalTransactionDate(), // Terminal transaction date
//        p22_pointOfServiceEntryMode = "051", // EMV chip read
        p25_pointOfServiceConditionCode = "00", // Normal condition
//        p35_track2Data = createPayload.track2Data, // Track 2 Data
        p37_retrievalReferenceNumber = createPayload.originalRRN,
        p41_cardAcceptorTerminalID = getTerminalID(), // Terminal ID
        p49_transactionCurrencyCode = createPayload.countryID.removeLeadingZeros(), // Currency Code
//        p55_iccData = createPayload.field55, // EMV Data
//        p60_privateUse = createPayload.amount // Original transaction amount (mandatory for voids)
    )
}




fun createMerchantBatchUploadRequest(fileActionAdvice:FileActionAdvice): Iso8583Message {
    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.FILE_ACTION_ADVICE, // 0320
        p3_processingCode = getProcessingCode(), // Processing Code for batch settlement
        p4_transactionAmount = fileActionAdvice.batchAmount, // Total batch amount
        p11_systemTraceAuditNumber = generateSTAN(), // Unique trace number
        p12_localTransactionTime = getLocalTransactionTime(), // Terminal transaction time
        p13_localTransactionDate = getLocalTransactionDate(), // Terminal transaction date
        p41_cardAcceptorTerminalID = getTerminalID(), // Terminal ID
        p49_transactionCurrencyCode = "404", // Currency Code
        p60_privateUse = fileActionAdvice.batchNumber, // Batch Number
        p63_settlementData = "Settlement Data Placeholder" // Settlement data (depends on acquirer)
    )
}


fun createReversalRequest(createPayload: ISOMsg, reason: String? = ReversalResponseCodes.TIMEOUT_REVERSAL): Iso8583Message {
    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.REVERSAL_REQUEST, // Reversal Advice
        p2_primaryAccountNumber = createPayload.getString(2),
        p3_processingCode = getProcessingCode(
            TransactionTypes.REVERSAL.name,
            UNKNOWN,
            UNKNOWN
        ), // Should return "40xxxx"
        p4_transactionAmount = createPayload.getString(4), // Transfer amount
        p11_systemTraceAuditNumber = generateSTAN(), // New STAN
        p12_localTransactionTime = getLocalTransactionTime(), // Time
        p13_localTransactionDate = getLocalTransactionDate(),
        p41_cardAcceptorTerminalID = getTerminalID(), //
        p49_transactionCurrencyCode = createPayload.getString(49), // Currency
        p60_privateUse = createPayload.getString(4), // Original Amount
        p39_responseCode = reason// Timeout reversal response code
    )
}

fun createTransferRequest(createPayload: CardOutputData): Iso8583Message {
    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST, // MTI = 0200
        p2_primaryAccountNumber = createPayload.cardNumber, // PAN
        p3_processingCode = getProcessingCode(
            TransactionTypes.TRANSFER.name,
            UNKNOWN,
            UNKNOWN
        ), // Should return "40xxxx"
        p4_transactionAmount = createPayload.amount, // Transfer amount
        p11_systemTraceAuditNumber = generateSTAN(), // New STAN
        p12_localTransactionTime = getLocalTransactionTime(), // Time
        p13_localTransactionDate = getLocalTransactionDate(), // Date
        p22_pointOfServiceEntryMode = "051", // EMV chip
        p25_pointOfServiceConditionCode = "00", // Normal condition
        p35_track2Data = createPayload.track2Data, // Track 2 (if available)
        p41_cardAcceptorTerminalID = getTerminalID(), // Terminal ID
        p49_transactionCurrencyCode = createPayload.countryID.removeLeadingZeros(), // Currency
        p52_pinData = createPayload.pinBlock, // Required
        p55_iccData = createPayload.field55 // ICC Data
    )
}


fun createCardVerificationRequest(createPayload: CardOutputData): Iso8583Message {
    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.AUTHORIZATION_REQUEST, // 0100
        p2_primaryAccountNumber = createPayload.cardNumber,
        p3_processingCode = getProcessingCode(
            TransactionTypes.CARD_VERIFICATION.name,
            UNKNOWN,
            UNKNOWN
        ), // should return "380000"
        p4_transactionAmount = "0", // always zero
        p11_systemTraceAuditNumber = generateSTAN(),
        p12_localTransactionTime = getLocalTransactionTime(),
        p13_localTransactionDate = getLocalTransactionDate(),
        p22_pointOfServiceEntryMode = "051", // or 07 if contactless
        p25_pointOfServiceConditionCode = "00",
        p35_track2Data = createPayload.track2Data,
        p41_cardAcceptorTerminalID = getTerminalID(),
        p49_transactionCurrencyCode = createPayload.countryID.removeLeadingZeros(),
        p52_pinData = createPayload.pinBlock, // optional
        p55_iccData = createPayload.field55 // if EMV
    )
}


fun createAuthroizationRequest(createPayload: CardOutputData): Iso8583Message {
    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.AUTHORIZATION_REQUEST, // MTI = 0100
        p2_primaryAccountNumber = createPayload.cardNumber,
        p3_processingCode = getProcessingCode(
            TransactionTypes.PREAUTHORIZATION_PURCHASE.name,
            UNKNOWN,
            UNKNOWN
        ), // should return 000000
        p4_transactionAmount = createPayload.amount,
        p11_systemTraceAuditNumber = generateSTAN(),
        p12_localTransactionTime = getLocalTransactionTime(),
        p13_localTransactionDate = getLocalTransactionDate(),
        p22_pointOfServiceEntryMode = "051",
        p23_cardSequenceNumber = createPayload.cardSequenceNumber,
        p25_pointOfServiceConditionCode = "00",
        p35_track2Data = createPayload.track2Data,
        p41_cardAcceptorTerminalID = getTerminalID(),
        p49_transactionCurrencyCode = createPayload.countryID.removeLeadingZeros(),
        p52_pinData = createPayload.pinBlock,
        p55_iccData = createPayload.field55,
    )
}

fun createPreAuthCompletionRequest(createPayload: TransactionEntity): Iso8583Message {
    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST, // MTI = 0200
        p3_processingCode = createPayload.processingCode, // "000000"
        p4_transactionAmount = createPayload.amount, // Completion amount
        p11_systemTraceAuditNumber = generateSTAN(),
        p12_localTransactionTime = getLocalTransactionTime(),
        p13_localTransactionDate = getLocalTransactionDate(),
        p25_pointOfServiceConditionCode = "00",
        p37_retrievalReferenceNumber = createPayload.retrievalReferenceNumber, // From original Pre-Auth
        p41_cardAcceptorTerminalID = getTerminalID(),
        p49_transactionCurrencyCode = createPayload.isoMessageJson?.fromJson<IsoDataClass>()?.p49.toString()
    )
}


fun createSettlementRequest(totalAmount: String, settlementList: List<TransactionEntity>): Iso8583Message {

    val batchNumber = generateBatchNumber()
    originalBatchNumberUsedFor0320 = batchNumber
    val settlementData = buildString {
        append("BATCH#=$batchNumber;")
        append("CNT=${settlementList.size};")
        append("AMT=$totalAmount;")
        append("TS=${System.currentTimeMillis()};")
    }


    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.CHARGEBACK_REQUEST, // MTI = 0500
        p3_processingCode = "920000",
        p4_transactionAmount = totalAmount, // Total amount from batch
        p11_systemTraceAuditNumber = generateSTAN(),
        p12_localTransactionTime = getLocalTransactionTime(),
        p13_localTransactionDate = getLocalTransactionDate(),
        p41_cardAcceptorTerminalID = getTerminalID(),
        p49_transactionCurrencyCode = "404", // or dynamic if applicable
        p60_privateUse = batchNumber, // Usually batch number
        p63_settlementData = settlementData // 👈 This is required

    )
}


fun createFinalSettlementAfterBatchUpload(batchNumber: String): Iso8583Message {
    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.CHARGEBACK_REQUEST, // MTI = 0500
        p3_processingCode = "960000", // Final after batch upload
        p4_transactionAmount = "0",
        p11_systemTraceAuditNumber = generateSTAN(),
        p12_localTransactionTime = getLocalTransactionTime(),
        p13_localTransactionDate = getLocalTransactionDate(),
        p41_cardAcceptorTerminalID = getTerminalID(),
        p49_transactionCurrencyCode = "404",
        p60_privateUse = batchNumber
    )
}


fun createBatchUploadRequest(transactions: List<TransactionEntity>, batchNumber: String): Iso8583Message {

    val totalAmount = transactions.sumOf { it.amount?.toLongOrNull() ?: 0L }.toString()

    val settlementData = buildString {
        append("BATCH#=$batchNumber;")
        append("CNT=${transactions.size};")
        append("AMT=$totalAmount;")
        append("TS=${System.currentTimeMillis()};")
    }

    return createTransactionRequest(
        messageType = MessageTypeIdentifiers.FILE_ACTION_ADVICE, // MTI = 0320
        p3_processingCode = "960000", // Example processing code for batch
        p4_transactionAmount = transactions.sumOf { it.amount?.toLongOrNull() ?: 0L }.toString(),
        p11_systemTraceAuditNumber = generateSTAN(),
        p12_localTransactionTime = getLocalTransactionTime(),
        p13_localTransactionDate = getLocalTransactionDate(),
        p41_cardAcceptorTerminalID = getTerminalID(),
        p49_transactionCurrencyCode = "404",
        p60_privateUse = batchNumber,
        p63_settlementData = settlementData
    )
}


