package com.rbs.iso8583lib.transaction

import android.util.Log
import com.rbs.iso8583lib.communication.JPOSTCPHandler
import com.rbs.iso8583lib.iso.createReversalRequest
import com.rbs.iso8583lib.model.TransactionTypes
import com.rbs.iso8583lib.expose.Iso8583DataProvider
import com.rbs.iso8583lib.utils.generateBatchNumber
import com.rbs.iso8583lib.utils.transactionTypesIdentifiers.MessageTypeIdentifiers
import com.rbs.iso8583lib.utils.tranzware_errorcodes.ReversalResponseCodes
import com.rbs.iso8583lib.utils.tranzware_errorcodes.TranzwareErrorCodesConstants
import com.rbs.iso8583lib.viewmodel.TciViewModel
import com.rbs.iso8583lib.iso.pack_iso.IsoMessageUtils
import com.rbs.iso8583lib.model.CardOutputData
import com.rbs.iso8583lib.storage.entity.TransactionEntity
import com.rbs.iso8583lib.utils.cores.extensions.firstTwoChars
import com.rbs.iso8583lib.utils.cores.extensions.toJson
import com.rbs.iso8583lib.utils.tranzware_errorcodes.TranzwareErrorCodes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jpos.iso.ISOMsg
import timber.log.Timber

class TransactionManager(
    private val jposHandler: JPOSTCPHandler,
    private val maxRetries: Int = 3,
    private val viewModel: TciViewModel?
) {

    interface TransactionListener {
        fun onTransactionSuccess(response: ISOMsg)
        fun onTransactionFailure(error: String, transactionType: String)
        fun onReversalInitiated(reversalMessage: ISOMsg)
        fun onPerformBatchUpload(batchNumber: String)
    }

    var  listener: TransactionListener? = null
    private val transactionStates = mutableMapOf<String, TransactionState>()
    val transactionRepository = Iso8583DataProvider.repository


    data class TransactionState(
        val messageTypeIdentifiers: String,
        val isoMessage: ISOMsg,
        var retries: Int = 0,
        var reversed: Boolean = false
    )

    fun sendTransaction(messageTypeIdentifiers: String, isoMessage: ISOMsg) {
        Timber.d("sendTransaction ------> Sending transaction: $messageTypeIdentifiers and ${isoMessage.toJson()}")
        val transactionId = isoMessage.getString(11) // Use STAN (field 11) as the transaction ID
        transactionStates[transactionId] = TransactionState(messageTypeIdentifiers = messageTypeIdentifiers, isoMessage = isoMessage)

        // Send the initial transaction
        attemptTransaction(transactionId)
    }

    private fun attemptTransaction(transactionId: String) {
        Timber.d("attemptTransaction ------> Attempting transaction ID $transactionId")
        val state = transactionStates[transactionId] ?: return
        val isoMessage = state.isoMessage

        CoroutineScope(Dispatchers.IO).launch {
            Timber.d("attemptTransaction ------> Sending ISO message: ${isoMessage.toJson()}")
            try {
                jposHandler.sendISOMessage(isoMessage)
            }catch (e: Exception){
               Timber.e("attemptTransaction ------> Error sending ISO message: ${e.message}")
                listener?.onTransactionFailure("Error Sending Message ${e.message}", isoMessage.getString(0))
            }
        }
    }

    fun getLastTransactionId(): String? {
        return transactionStates.keys.lastOrNull()
    }


    fun getMessageTypeName(messageType: String, processingCode: String, state: TransactionState): String? {
        return when {
            // Response to Pre-Authorization
            messageType == "0110" && processingCode.startsWith("00") -> TransactionTypes.PREAUTHORIZATION_PURCHASE.name

            // Response to Pre-Auth Completion
            messageType == MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE && processingCode.startsWith("00") && state.isoMessage.hasField(37) -> TransactionTypes.PREAUTHORIZATION_COMPLETION.name

            // Response to Purchase
            messageType == MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE && processingCode.startsWith("00") && !state.isoMessage.hasField(37) -> TransactionTypes.PURCHASE.name

            // Response to Cash Advance
            messageType == MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE && processingCode.startsWith("01") -> TransactionTypes.CASH_ADVANCE.name

            // Response to Purchase with Cashback
            messageType == MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE && processingCode.startsWith("09") -> TransactionTypes.PURCHASE_WITH_CASHBACK.name

            // Response to Void
            messageType == MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE && processingCode.startsWith("02") && state.isoMessage.getString(4) == "0" -> TransactionTypes.VOID.name

            // Response to Refund
            messageType == MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE && processingCode.startsWith("02") && state.isoMessage.getString(4) != "0" -> TransactionTypes.REFUND.name

            // Response to Bill Payment
            messageType == MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE && processingCode.startsWith("50") -> TransactionTypes.BILL_PAYMENT.name

            // Response to Deposit
            messageType == MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE && processingCode.startsWith("21") -> TransactionTypes.DEPOSIT.name

            // Response to Transfer
            messageType == MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE && processingCode.startsWith("40") -> TransactionTypes.TRANSFER.name

            // Response to Balance Inquiry
            messageType == MessageTypeIdentifiers.AUTHORIZATION_RESPONSE && processingCode.startsWith("31") -> TransactionTypes.BALANCE_INQUIRY.name

            // Response to Card Verification
            messageType == MessageTypeIdentifiers.AUTHORIZATION_RESPONSE && processingCode.startsWith("38") -> TransactionTypes.CARD_VERIFICATION.name

            // Response to Network Management
            messageType == MessageTypeIdentifiers.NETWORK_MANAGEMENT_RESPONSE -> TransactionTypes.NETWORK_MANAGEMENT.name


            else -> TransactionTypes.Companion.getKeyFromResponseCode(messageType) ?: "UNKNOWN"
        }
    }


    fun handleResponse(transactionId: String, response: ISOMsg, lastCardOutputData:CardOutputData?) {
        val state = transactionStates[transactionId] ?: return
        val responseCode = response.getString(39) // Field 39 for response code
        var messageType = response.getString(0) // Field 0 for message type
        val checkprocessingCode = response.getString(3).firstTwoChars() // Field 3 for processing code
        val processingCode = response.getString(3) // Field 3 for processing code
        val retrievalReferenceNumber = response.getString(37) ?: "" // Field 3 for processing code

        CoroutineScope(Dispatchers.IO).launch {
            if (responseCode == TranzwareErrorCodesConstants.APPROVED) {
                when(messageType == MessageTypeIdentifiers.NETWORK_MANAGEMENT_RESPONSE){
                    true -> {
                        Timber.Forest.i("No need to save this")
                    }
                    false -> {
                        val messageTypeName =  getMessageTypeName(messageType, processingCode, state)


                        if (messageTypeName == TransactionTypes.PREAUTHORIZATION_COMPLETION.name) {
                            val preAuthTxn = transactionRepository?.getTransactionByRrn(state.isoMessage.getString(37)) // <- from original pre-auth
                            preAuthTxn?.let {
                                val updatedPreAuth = it.copy(
                                    isPreAuthCompleted = true,
                                    authCompletedOn = System.currentTimeMillis()
                                )
                                transactionRepository?.saveTransaction(updatedPreAuth)
                            }

                            // Save Auth Completion as a new transaction
                            transactionRepository?.saveTransaction(
                                TransactionEntity.fromISOMsg(
                                    transactionId = response.getString(11),
                                    transactionType = messageType,
                                    isoMsg = response,
                                    responseCode = response.getString(39),
                                    processingCode = response.getString(3),
                                    retrievalReferenceNumber = response.getString(37), // new RRN
                                    lastCardOutputData = lastCardOutputData?.toJson() ?: "",
                                    messageTypeName = TransactionTypes.PREAUTHORIZATION_COMPLETION.name
                                )
                            )
                            listener?.onTransactionSuccess(response)
                            transactionStates.remove(transactionId)
                            return@launch
                        }



                        if (messageTypeName == TransactionTypes.VOID.name) {
                            val requestP37 = state.isoMessage.getString(37)
                            val preAuthTxn = transactionRepository?.getTransactionByRrn(requestP37) // <- from original pre-auth
                            preAuthTxn?.let {
                                val updatedPreAuth = it.copy(
                                    voided = true,
                                    voidedOn = System.currentTimeMillis()
                                )
                                transactionRepository?.saveTransaction(updatedPreAuth)
                            }

                            // Save Auth Completion as a new transaction
                            transactionRepository?.saveTransaction(
                                TransactionEntity.fromISOMsg(
                                    transactionId = response.getString(11),
                                    transactionType = messageType,
                                    isoMsg = response,
                                    responseCode = response.getString(39),
                                    processingCode = response.getString(3),
                                    retrievalReferenceNumber = response.getString(37), // new RRN
                                    lastCardOutputData = lastCardOutputData?.toJson() ?: "",
                                    messageTypeName = messageTypeName
                                )
                            )
                            listener?.onTransactionSuccess(response)
                            transactionStates.remove(transactionId)
                            return@launch
                        }


                        transactionRepository?.saveTransaction(
                            TransactionEntity.fromISOMsg(
                                transactionId=transactionId,
                                transactionType=messageType,
                                isoMsg=response,
                                responseCode=responseCode,
                                processingCode=processingCode,
                                retrievalReferenceNumber = retrievalReferenceNumber,
                                lastCardOutputData= lastCardOutputData.toJson(),
                                messageTypeName= messageTypeName.toString()
                            )
                        )
                    }
                }

                listener?.onTransactionSuccess(response)
                transactionStates.remove(transactionId)
            } else {
                val errorMessage = TranzwareErrorCodes.getErrorMessage(responseCode)
                handleFailure(transactionId, errorMessage, responseCode)
            }
        }
    }


    fun retryTransaction(transactionId: String) {
        val state = transactionStates[transactionId]
        val messageType = state?.messageTypeIdentifiers
        if (state != null) {
            if (state.retries < maxRetries) {
                state.retries++
                Log.d("TransactionManager", "Retrying transaction ID $transactionId (Attempt ${state.retries}/$maxRetries)")

                attemptTransaction(transactionId) // Retry sending the transaction

            } else {
                Log.e("TransactionManager", "Max retries reached for transaction ID $transactionId")
                listener?.onTransactionFailure("Transaction failed: Failed to connect to the server.",
                    messageType.toString()
                )
            }
        } else {
            Log.e("TransactionManager", "No transaction state found for ID $transactionId")
        }
    }


    fun handleFailure(transactionId: String, errorMessage: String, responseCode: String) {
        val state = transactionStates[transactionId] ?: return
        val messageTypeIdentifiers = state.messageTypeIdentifiers
        val isNoResponse = errorMessage.isNullOrEmpty()
        val eligibleForReversal = when (messageTypeIdentifiers) {
            MessageTypeIdentifiers.FINANCIAL_TRANSACTION_REQUEST,
            MessageTypeIdentifiers.AUTHORIZATION_REQUEST -> true
            else -> false
        }



        if (messageTypeIdentifiers == MessageTypeIdentifiers.CHARGEBACK_REQUEST  &&  responseCode == "95") {
            val batchNumber = state.isoMessage.getString(60) ?: generateBatchNumber()
            listener?.onPerformBatchUpload(batchNumber)
            Timber.Forest.e("Settlement failed. Fallback to Batch Upload $batchNumber")
        }else{
            when(state.messageTypeIdentifiers){
                MessageTypeIdentifiers.NETWORK_MANAGEMENT_REQUEST ->{
                    state.retries++
                    if (state.retries < maxRetries) {
                        retryTransaction(transactionId)
                    }
                }
                else -> {
                    if (eligibleForReversal && isNoResponse && !state.reversed) {
                        // ✅ Reversal logic for financial and auth requests
                        Timber.Forest.w("No response from host. Initiating reversal for $transactionId")
                        initiateReversal(transactionId)
                    } else {
                        listener?.onTransactionFailure(errorMessage ?: "Unknown failure", messageTypeIdentifiers)
                    }
                }
            }

        }

    }

    private fun initiateReversal(transactionId: String) {
        val state = transactionStates[transactionId] ?: return

        if (!state.reversed) {

            state.reversed = true
            IsoMessageUtils.sendPackedMessage(
                message = createReversalRequest(
                    state.isoMessage,
                    ReversalResponseCodes.TIMEOUT_REVERSAL
                ), viewModel = viewModel!!, transactionManager = this
            )
        }
    }


}