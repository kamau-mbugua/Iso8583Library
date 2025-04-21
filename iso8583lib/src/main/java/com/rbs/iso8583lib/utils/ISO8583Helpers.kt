package com.rbs.iso8583lib.utils


import com.rbs.iso8583lib.model.TransactionTypes
import com.rbs.iso8583lib.model.AccountTypes
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger

fun getProcessingCode(transactionType: String, fromAccount: String, toAccount: String): String {
    val transCode = getTransactionCode(TransactionTypes.Companion.fromKey(transactionType))  // Convert string to enum
    val fromType = getAccountType(fromAccount)           // returns 2-digit code
    val toType = getAccountType(toAccount)               // returns 2-digit code
    return transCode + fromType + toType
}


fun getTransactionCode(transactionType: TransactionTypes): String {




    return when (transactionType) {
        TransactionTypes.PURCHASE,
        TransactionTypes.PREAUTHORIZATION_PURCHASE,
        TransactionTypes.PREAUTHORIZATION_COMPLETION -> "00"  // Purchase, Pre-Auth & Completion

        TransactionTypes.CASH_ADVANCE -> "01"  // Cash Advance (ATM/Bank Withdrawals)

        TransactionTypes.REFUND -> "20"  // Refund / Reversal

        TransactionTypes.WITHDRAWAL -> "01"  // ATM Withdrawal / Cash Advance

        TransactionTypes.PURCHASE_WITH_CASHBACK -> "09"  // Purchase + Cashback

        TransactionTypes.BALANCE_INQUIRY -> "31"  // Balance Inquiry

        TransactionTypes.TRANSFER -> "40"  // Funds Transfer Between Accounts

        TransactionTypes.BILL_PAYMENT -> "50"  // Bill Payment

        TransactionTypes.REVERSAL -> "20"  // Reversal Transactions

        TransactionTypes.VOID -> "02"  // Void Transactions

        TransactionTypes.DEPOSIT -> "21"  // Deposit
        TransactionTypes.CARD_VERIFICATION -> "38"  // Deposit

        else -> "00"  // Default (Unknown transaction type)
    }
}


fun getAccountType(accountType: String): String {
    return when (accountType) {
        AccountTypes.UNKNOWN -> "00"
        AccountTypes.CHECKING -> "01"
        AccountTypes.SAVINGS -> "11"
        AccountTypes.CREDIT -> "31"
        AccountTypes.BONUS -> "91"
        else -> "00"  // Default to Unknown
    }
}

//RETURN P-3
fun getProcessingCode(): String{
    return "920000"
}



fun generateSTAN(): String{
    return STANGenerator.generateSTANPrimary()
}
fun generateBatchNumber(): String{
    return BatchNoGenerator.generateBatchNumberPrimary()
}


//RETURNS P-12
fun getLocalTransactionTime(): String {
    val localTime = LocalTime.now() // Gets current terminal time
    val formatter = DateTimeFormatter.ofPattern("HHmmss") // Format as HHMMSS
    return localTime.format(formatter) // Returns formatted time
}

//RETURNS P-13

fun getLocalTransactionDate(): String {
    val localDate = LocalDate.now() // Get current date
    val formatter = DateTimeFormatter.ofPattern("MMdd") // Format as MMDD
    return localDate.format(formatter) // Return formatted date
}

//RETURN P-26 Message Reason Code


//RETURNS P-28 Fee Amount
fun formatFeeAmount(amount: Double, isCredit: Boolean): String {
    val minorUnitsAmount = (amount * 100).toLong() // Convert to minor units (cents)
    val debitOrCredit = if (isCredit) "C" else "D" // "C" for credit, "D" for debit
    return debitOrCredit + minorUnitsAmount.toString().padStart(8, '0') // Ensure A1+N8 format
}

//RETURNS P-32 Acquiring Institution Identification Implementation
fun getAcquiringInstitutionCode(): String {
    //TODO: Implement institution code provided by Tranzware LATER, for now return default value
    return "000000"
}

//RETURNS P-33 Forwarding Institution Identification
fun getForwardingInstitutionCode(): String {
    //TODO: Implement institution code provided by Tranzware LATER, for now return default value
    return ""
}

//RETURNS P-37 Transaction Retrieval Reference Number

fun generateRRN(): String {
    val counter = AtomicInteger(100000) // Ensures uniqueness within 24 hours

    val now = LocalDateTime.now()
    val timestamp = now.format(DateTimeFormatter.ofPattern("MMddHHmmss")) // MMDDHHMMSS
    val uniquePart = counter.getAndIncrement().toString().takeLast(2) // Ensures uniqueness
    return (timestamp + uniquePart).take(12) // Ensures length is 12
}




//RETURNS P-41 Card Acceptor Terminal ID
fun getTerminalID(): String {
    //TODO: RETURN TERSMINAL SERIAL NUMBER, For now return a defult value
//    return "22837020"
    return "14150026"
}


// P-54
fun calculateFee(transactionAmount: Int, feeRate: Double): String {
    // transactionAmount is in minor units, e.g. cents
    val feeAmount = (transactionAmount * feeRate).toInt()
    // Format as 12-digit, zero-padded string
    return feeAmount.toString().padStart(12, '0')
}

// P-55








