package com.rbs.iso8583lib.iso.pack_iso

// IsoMessageUtils.kt
// IsoMessageUtils.kt

import com.rbs.iso8583lib.model.Iso8583Message
import com.rbs.iso8583lib.transaction.TransactionManager
import com.rbs.iso8583lib.viewmodel.TciViewModel
import timber.log.Timber

object IsoMessageUtils {
    fun sendPackedMessage(transactionManager: TransactionManager, viewModel: TciViewModel, message: Iso8583Message) {
        val packedMessage = viewModel.createISOMessage(message)
        val messageType = packedMessage.getString(0)

        Timber.e("Packed Message: ${packedMessage.pack()}")
        Timber.e("Packed Message Type: $messageType")
        transactionManager.sendTransaction(
            messageType.toString(),
            packedMessage
        ).apply {
            Timber.e("Request Sent")
        }
    }
}