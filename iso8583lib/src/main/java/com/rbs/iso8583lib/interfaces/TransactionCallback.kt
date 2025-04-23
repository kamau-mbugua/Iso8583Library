package com.rbs.iso8583lib.interfaces

import org.jpos.iso.ISOMsg

interface TransactionCallback {
    fun onMessageSent(request: ISOMsg) {}
    fun onMessageReceived(response: ISOMsg) {}
    fun onError(error: String, request: ISOMsg) {}
    fun onTransactionSuccess(response: ISOMsg) {}
    fun onTransactionFailure(error: String, transactionType: String) {}
}
