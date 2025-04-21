package com.rbs.iso8583lib.model

data class Iso8583Message(
    var messageType: String? = null, // Message Type Indicator
    var p0: String? = null,  // Primary Bitmap
    var p1: String? = null,  // Secondary Bitmap (if needed)
    var p2: String? = null,  // Primary Account Number (PAN)
    var p3: String? = null,  // Processing Code
    var p4: String? = null,  // Transaction Amount
    var p11: String? = null, // System Trace Audit Number
    var p12: String? = null, // Local Transaction Time
    var p13: String? = null, // Local Transaction Date
    var p14: String? = null, // Expiration Date
    var p22: String? = null, // POS Entry Mode
    var p23: String? = null, // Card Sequence Number
    var p25: String? = null, // POS Condition Code
    var p35: String? = null, // Track 2 Data
    var p37: String? = null, // Retrieval Reference Number
    var p38: String? = null, // Authorization Identification Response
    var p39: String? = null, // Response Code
    var p41: String? = null, // Card Acceptor Terminal ID
    var p42: String? = null, // Card Acceptor Identification Code
    var p45: String? = null, // Track 1 Data
    var p48: String? = null, // Working Keys
    var p49: String? = null, // Transaction Currency Code
    var p52: String? = null, // Personal Identification Number (PIN)
    var p54: String? = null, // Additional Amounts
    var p55: String? = null, // ICC System Related Data (EMV data)
    var p57: String? = null, // Additional Data
    var p58: String? = null, // Detail Addenda Extended
    var p59: String? = null, // Detail Addenda
    var p60: String? = null, // Private Use (Batch, Original Amount)
    var p62: String? = null, // Private Use (Invoice Number, etc.)
    var p63: String? = null, // Private Use (CVV2, Additional Data)
    var p64: String? = null  // Message Authentication Code (MAC)
)
