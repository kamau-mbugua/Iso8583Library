package com.rbs.iso8583corelibrary

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.rbs.iso8583lib.expose.Iso8583Core
import com.rbs.iso8583lib.expose.Iso8583Core.generateSignOnRequests
import com.rbs.iso8583lib.interfaces.TransactionCallback
import com.rbs.iso8583lib.model.Iso8583Message
import com.rbs.iso8583lib.utils.transactionTypesIdentifiers.MessageTypeIdentifiers
import com.rbs.iso8583lib.utils.transactionTypesIdentifiers.MessageTypeIdentifiers.AUTHORIZATION_RESPONSE
import com.rbs.iso8583lib.utils.transactionTypesIdentifiers.MessageTypeIdentifiers.FINANCIAL_TRANSACTION_RESPONSE
import org.jpos.iso.ISOMsg

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpListeners()
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        signOn()
    }

    private fun setUpListeners() {
        Iso8583Core.setTransactionCallback(object : TransactionCallback {
            override fun onTransactionSuccess(response: ISOMsg) {
                val mti = response.getString(0)
                val stan = response.getString(11)
                if (mti == FINANCIAL_TRANSACTION_RESPONSE || mti == AUTHORIZATION_RESPONSE) {
//                    dismissLoadingDialog()
//                    fetchExistingData(stan, "Merchant Receipt", COPYTYPE.Merchant_Copy)
                }else if (mti == MessageTypeIdentifiers.NETWORK_MANAGEMENT_RESPONSE) {
                    // Handle network management response
                    Log.d("setUpListeners", "Network Management Response: $response")
                } else {

                }
            }

            override fun onTransactionFailure(error: String, transactionType: String) {
//                dismissLoadingDialog()
//                setUpPrintingDialog("Transaction Failed", error, transactionType)
            }

            override fun onError(error: String, request: ISOMsg) {
                val mti = request.getString(0)
                if (mti == MessageTypeIdentifiers.NETWORK_MANAGEMENT_REQUEST) {
                    val lastTransactionId = Iso8583Core.getTransactionManager().getLastTransactionId()
                    if (lastTransactionId == null) {
//                        dismissLoadingDialog()
//                        setUpPrintingDialog("Network Error", error, mti)
                    }
                } else {
//                    dismissLoadingDialog()
//                    setUpPrintingDialog("Network Error", error, mti)
                }
            }
        })

    }

    fun signOn() {
        val signOnMessage = generateSignOnRequests()
        sendTransaction(signOnMessage)
    }

    fun sendTransaction(iso8583Message:Iso8583Message){
        Iso8583Core.sendTransaction(iso8583Message)
    }
}