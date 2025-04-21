package com.rbs.iso8583lib.model

import org.jpos.tlv.TLVList

data class CardOutputData(
    val cardNumber: String,
    val cardLabel: String,
    val track2Data: String,
    val pinBlock: String,
    val serialNumber: String,
    val field55: String,
    val pinType: String,
    val entryMode: String,
    val amount: String,
    val amountCashback: String,
    val countryID: String,
    val cardSequenceNumber: String,
    val expirationDate: String,
    val field55TlvList: TLVList,
    val issuerResponseCode: String,
    val aid: String,
    val tvr: String,
    val cid: String,
    val transactionId: String,
    val originalRRN: String,
    val cardHolderName: String
)