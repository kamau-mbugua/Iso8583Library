package com.rbs.iso8583lib.expose

import android.app.Application
import android.content.Context
import com.rbs.iso8583lib.communication.JPOSTCPHandler
import com.rbs.iso8583lib.iso.*
import com.rbs.iso8583lib.iso.pack_iso.IsoMessageUtils
import com.rbs.iso8583lib.model.CardOutputData
import com.rbs.iso8583lib.model.FileActionAdvice
import com.rbs.iso8583lib.model.Iso8583Message
import com.rbs.iso8583lib.storage.db.AppDatabase
import com.rbs.iso8583lib.storage.entity.TransactionEntity
import com.rbs.iso8583lib.transaction.TransactionManager
import com.rbs.iso8583lib.utils.cores.LoggerInit
import com.rbs.iso8583lib.utils.cores.PrefManager
import com.rbs.iso8583lib.utils.cores.extensions.toJson
import com.rbs.iso8583lib.utils.packager.PackagerProvider
import com.rbs.iso8583lib.utils.transactionTypesIdentifiers.MessageTypeIdentifiers
import com.rbs.iso8583lib.viewmodel.TciViewModel
import org.jpos.iso.ISOMsg
import org.jpos.iso.packager.GenericPackager
import org.jpos.util.Logger
import timber.log.Timber

object Iso8583Core {


    private lateinit var transactionManager: TransactionManager
    private lateinit var jposHandler: JPOSTCPHandler
    private lateinit var viewModel: TciViewModel
    private lateinit var connectionPort: String
    private lateinit var connectionIp: String

    fun init(app: Application, debug: Boolean = false, connectionPort: String, connectionIp: String) {
        // Initialize SharedPreferences
        Timber.e("Iso8583Coreinit: $connectionPort, $connectionIp")
        this.connectionPort = connectionPort
        this.connectionIp = connectionIp

        PrefManager.init(app)

        // Initialize Timber
        LoggerInit.init(debug)

        // Initialize Room DB (singleton cached inside Room itself)
        AppDatabase.getDatabase(app)

        // Init Repository (does not store context statically)
        Iso8583DataProvider.initRepository(app)

        // Pre-load packager and cache it internally (see next step)
        PackagerProvider.init(app)

        // ViewModel & Packager
        viewModel = TciViewModel(PackagerProvider.get())

        // Logger for JPOS (optional to inject externally)
        val jposLogger = Logger().apply {
            addListener(org.jpos.util.SimpleLogListener(System.out))
        }

        // Init JPOS Handler
        jposHandler = JPOSTCPHandler(
            packager = PackagerProvider.get(),
            jposLogger = jposLogger
        ).apply {
            this.listener = object : JPOSTCPHandler.JPOSListener {
                override fun onMessageSent(request: org.jpos.iso.ISOMsg) {

                }

                override fun onMessageReceived(response: org.jpos.iso.ISOMsg) {
                    val transactionId = response.getString(11)
                    transactionManager.handleResponse(transactionId, response, null)
                }

                override fun onError(error: String, request: org.jpos.iso.ISOMsg) {

                    val mti = request.getString(0)
                    if (mti == MessageTypeIdentifiers.NETWORK_MANAGEMENT_REQUEST) {
                        val lastTransactionId = transactionManager.getLastTransactionId()
                        if (lastTransactionId != null) {
                            transactionManager.retryTransaction(lastTransactionId)
                        } else {
                        }
                    } else {

                    }
                }
            }
        }

        // Transaction Manager
        transactionManager = TransactionManager(
            jposHandler = jposHandler,
            viewModel = viewModel
        ).apply {
            this.listener = listener
        }

    }


    // Core Transaction Executor
    fun sendTransaction(message: Iso8583Message) {
        Timber.e("sendTransaction: ${message.toJson()}")
        IsoMessageUtils.sendPackedMessage(
            transactionManager = transactionManager,
            viewModel = viewModel,
            message = message
        )
    }

    // Expose Core Components
    fun getTransactionManager() = transactionManager
    fun getRepository() = Iso8583DataProvider.repository
    fun getViewModel() = viewModel
    fun getConnectionPort() = connectionPort
    fun getConnectionIp() = connectionIp


    // Exposed Message Generators
    fun generateSignOnRequests() = generateSignOnRequest()
    fun createPurchaseRequest(payload: CardOutputData) = createPurchaseRequests(payload)
    fun createPurchaseWithCashbackRequests(payload: CardOutputData) = createPurchaseWithCashbackRequest(payload)
    fun createCashAdvanceRequests(payload: CardOutputData) = createCashAdvanceRequest(payload)
    fun createBalanceInquiryRequests(payload: CardOutputData) = createBalanceInquiryRequest(payload)
    fun createRefundRequests(payload: CardOutputData) = createRefundRequest(payload)
    fun createDepositRequests(payload: CardOutputData) = createDepositRequest(payload)
    fun createBillPaymentRequests(payload: CardOutputData) = createBillPaymentRequest(payload)
    fun createTransferRequests(payload: CardOutputData) = createTransferRequest(payload)
    fun createCardVerificationRequests(payload: CardOutputData) = createCardVerificationRequest(payload)
    fun createAuthroizationRequests(payload: CardOutputData) = createAuthroizationRequest(payload)
    fun createVoidCardPresentRequests(payload: CardOutputData) = createVoidCardPresentRequest(payload)
    fun createVoidCardAbsentRequests(payload: CardOutputData) = createVoidCardAbsentRequest(payload)
    fun createReversalRequests(message: ISOMsg, reason: String? = null) = createReversalRequest(message, reason)
    fun createMerchantBatchUploadRequests(advice: FileActionAdvice) = createMerchantBatchUploadRequest(advice)
    fun createSettlementRequests(totalAmount: String, list: List<TransactionEntity>) = createSettlementRequest(totalAmount, list)
    fun createFinalSettlementAfterBatchUploads(batchNumber: String) = createFinalSettlementAfterBatchUpload(batchNumber)
    fun createBatchUploadRequests(transactions: List<TransactionEntity>, batchNumber: String) = createBatchUploadRequest(transactions, batchNumber)
    fun createPreAuthCompletionRequests(transaction: TransactionEntity) = createPreAuthCompletionRequest(transaction)

}
