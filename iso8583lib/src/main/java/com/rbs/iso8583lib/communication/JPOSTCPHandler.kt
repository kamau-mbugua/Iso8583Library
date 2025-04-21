package com.rbs.iso8583lib.communication

import com.rbs.iso8583lib.expose.Iso8583Core.getConnectionIp
import com.rbs.iso8583lib.expose.Iso8583Core.getConnectionPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jpos.iso.ISOMsg
import org.jpos.iso.ISOPackager
import org.jpos.util.Logger
import timber.log.Timber
import java.io.EOFException
import java.io.IOException
import java.net.SocketTimeoutException

class JPOSTCPHandler(
    private val tranzwareHost: String = getConnectionIp(),
    private val tranzwarePort: Int = getConnectionPort().toInt(),
    private val packager: ISOPackager,
    private val jposLogger: Logger,
    private val connectionTimeout: Int = 30000
) {

    interface JPOSListener {
        fun onMessageSent(request: ISOMsg)
        fun onMessageReceived(response: ISOMsg)
        fun onError(error: String, request: ISOMsg)
    }

    var listener: JPOSListener? = null

    /**
     * Send an ISOMsg using our custom TranzWareChannel. Let the channel
     * do normal ISO pack/unpack (and custom header logic, encryption, etc. if you coded it there).
     */


    suspend fun sendISOMessage(message: ISOMsg) {
        Timber.d("JPOSTCPHandler ---> Sending ISO message: ${message.pack().contentToString()}")
        withContext(Dispatchers.Main) {
            val customHeader = byteArrayOf(0x60, 0x00, 0x00, 0x00, 0x00)

            val channel = TranzWareChannel(
                host = tranzwareHost,
                port = tranzwarePort,
                packager = packager,
                customHeader = customHeader
            ).apply {
                setLogger(jposLogger, "ISOLogger")
                timeout = connectionTimeout
            }


            try {
                channel.connect()
            } catch (e: IOException) {
                e.printStackTrace()
                Timber.e("JPOSTCPHandler ---> Connection failed: ${e.message}")
                listener?.onError("Connection failed: ${e.message}", message)
                return@withContext
            } finally {
                // Disconnect when done
//                channel.disconnect()
            }
            Timber.d("JPOSTCPHandler ---> Connected to $tranzwareHost:$tranzwarePort")

            // Send the ISOMsg
            listener?.onMessageSent(message)
            channel.send(message)  // <--- the channel calls pack(...), plus any custom logic

            // Receive the response as ISOMsg
            val response = try {
                channel.receive() // returns ISOMsg
            } catch (e: EOFException) {
                e.printStackTrace()
                Timber.e("JPOSTCPHandler ---> EOFException: ${e.message}")
                listener?.onError("EOFException: ${e.message}", message)
                throw EOFException("No response received (EOF) from $tranzwareHost:$tranzwarePort and ErrorMessage is ${e.message}")
            } catch (e: SocketTimeoutException) {
                e.printStackTrace()
                Timber.e("JPOSTCPHandler ---> Timeout: ${e.message}")
                listener?.onError("Timeout: ${e.message}", message)
                throw SocketTimeoutException("Timeout waiting for response from $tranzwareHost:$tranzwarePort and ErrorMessage is ${e.message}")
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.e("JPOSTCPHandler ---> Error: ${e.message}")
                listener?.onError("Error: ${e.message}", message)
                throw Exception("Error receiving response from $tranzwareHost:$tranzwarePort and ErrorMessage is ${e.message}")
            } finally {
                // Disconnect when done
                channel.disconnect()
            }


            if (response != null) {
                Timber.e("JPOSTCPHandler ---> Received response: ${response.pack().contentToString()}")
                listener?.onMessageReceived(response)
            } else {
                Timber.e("JPOSTCPHandler ---> Null response from host.")
                listener?.onError("Null response from $tranzwareHost:$tranzwarePort", message)
            }

            Timber.d("JPOSTCPHandler ---> Done processing message.")


        }
    }
}
