package com.rbs.iso8583lib.iso

import com.rbs.iso8583lib.model.Iso8583Message


fun createIso8583Message(
    messageType: String,
    fields: Map<String, String>
): Iso8583Message {
    return Iso8583Message(
        messageType = messageType,
        p0 = fields["P-0"],  // Primary Bitmap
        p1 = fields["P-1"],  // Secondary Bitmap (if present)
        p2 = fields["P-2"],  // Primary Account Number (PAN)
        p3 = fields["P-3"],  // Processing Code
        p4 = fields["P-4"],  // Transaction Amount
        p11 = fields["P-11"], // System Trace Audit Number
        p12 = fields["P-12"], // Local Transaction Time
        p13 = fields["P-13"], // Local Transaction Date
        p14 = fields["P-14"], // Expiration Date
        p22 = fields["P-22"], // POS Entry Mode
        p23 = fields["P-23"], // Card Sequence Number
        p25 = fields["P-25"], // POS Condition Code
        p35 = fields["P-35"], // Track 2 Data
        p37 = fields["P-37"], // Retrieval Reference Number
        p38 = fields["P-38"], // Authorization Identification Response
        p39 = fields["P-39"], // Response Code
        p41 = fields["P-41"], // Card Acceptor Terminal ID
        p42 = fields["P-42"], // Card Acceptor Identification Code
        p45 = fields["P-45"], // Track 1 Data
        p48 = fields["P-48"], // Working Keys
        p49 = fields["P-49"], // Transaction Currency Code
        p52 = fields["P-52"], // Personal Identification Number (PIN)
        p54 = fields["P-54"], // Additional Amounts
        p55 = fields["P-55"], // ICC System Related Data (EMV data)
        p57 = fields["P-57"], // Additional Data
        p58 = fields["P-58"], // Detail Addenda Extended
        p59 = fields["P-59"], // Detail Addenda
        p60 = fields["P-60"], // Private Use (Batch, Original Amount)
        p62 = fields["P-62"], // Private Use (Invoice Number, etc.)
        p63 = fields["P-63"], // Private Use (CVV2, Additional Data)
        p64 = fields["P-64"]  // Message Authentication Code (MAC)
    )
}


fun createTransactionRequest(
    messageType: String,
    p2_primaryAccountNumber: String? = null,
    p3_processingCode: String? = null,
    p4_transactionAmount: String? = null,
    p11_systemTraceAuditNumber: String? = null,
    p12_localTransactionTime: String? = null,
    p13_localTransactionDate: String? = null,
    p14_expirationDate: String? = null,
    p22_pointOfServiceEntryMode: String? = null,
    p23_cardSequenceNumber: String? = null,
    p25_pointOfServiceConditionCode: String? = null,
    p35_track2Data: String? = null,
    p37_retrievalReferenceNumber: String? = null,
    p38_authorizationIDResponse: String? = null,
    p39_responseCode: String? = null,
    p41_cardAcceptorTerminalID: String? = null,
    p42_cardAcceptorIdentificationCode: String? = null,
    p45_track1Data: String? = null,
    p48_workingKeys: String? = null,
    p49_transactionCurrencyCode: String? = null,
    p52_pinData: String? = null,
    p54_additionalAmount: String? = null,
    p55_iccData: String? = null,
    p57_additionalData: String? = null,
    p58_detailAddendaExternal: String? = null,
    p60_privateUse: String? = null,
    p63_settlementData: String? = null,
): Iso8583Message {
    val fields = mutableMapOf<String, String>()

    // Add only non-null values to fields
    p3_processingCode?.let { fields["P-3"] = it }
    p11_systemTraceAuditNumber?.let { fields["P-11"] = it }
    p2_primaryAccountNumber?.let { fields["P-2"] = it }
    p4_transactionAmount?.let { fields["P-4"] = it }
    p12_localTransactionTime?.let { fields["P-12"] = it }
    p13_localTransactionDate?.let { fields["P-13"] = it }
    p14_expirationDate?.let { fields["P-14"] = it }
    p22_pointOfServiceEntryMode?.let { fields["P-22"] = it }
    p23_cardSequenceNumber?.let { fields["P-23"] = it }
    p25_pointOfServiceConditionCode?.let { fields["P-25"] = it }
    p37_retrievalReferenceNumber?.let { fields["P-37"] = it }
    p38_authorizationIDResponse?.let { fields["P-38"] = it }
    p39_responseCode?.let { fields["P-39"] = it }
    p41_cardAcceptorTerminalID?.let { fields["P-41"] = it }
    p42_cardAcceptorIdentificationCode?.let { fields["P-42"] = it }
    p49_transactionCurrencyCode?.let { fields["P-49"] = it }
    p52_pinData?.let { fields["P-52"] = it }
    p45_track1Data?.let { fields["P-45"] = it }
    p35_track2Data?.let { fields["P-35"] = it }
    p55_iccData?.let { fields["P-55"] = it }
    p57_additionalData?.let { fields["P-57"] = it }
    p60_privateUse?.let { fields["P-60"] = it }
    p63_settlementData?.let { fields["P-63"] = it }
    p48_workingKeys?.let { fields["P-48"] = it }
    p54_additionalAmount?.let { fields["P-54"] = it }
    p58_detailAddendaExternal?.let { fields["P-58"] = it }

    return createIso8583Message(messageType, fields)
}


